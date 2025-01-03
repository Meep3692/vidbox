package ca.awoo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import ca.awoo.MPV.mpv_event;
import io.micronaut.runtime.server.EmbeddedServer;

public class MpvPlayer implements Player {
    private final MPV mpv;
    private final long handle;

    private Thread listenThread;

    private TitleProvider titleProvider;
    private int quality = 480;

    private double loadseek = 0;

    private static final Logger LOG = LoggerFactory.getLogger(MpvPlayer.class);

    public MpvPlayer(EmbeddedServer embeddedServer, TitleProvider titleProvider, PlayerOption... options) throws MpvException{
        this.titleProvider = titleProvider;
        this.mpv = MPV.INSTANCE;
        if(Platform.isLinux())
            CLib.INSTANCE.setlocale(CLib.LC_NUMERIC, "C");
        this.handle = mpv.mpv_create();
        if(handle == 0) throw new RuntimeException("Cannot make MPV");
        for(PlayerOption option : options){
            if(option.option()){
                setOption(option);
            }else{
                setProperty(option);
            }
        }

        try {
            InetAddress localhost = InetAddress.getLocalHost();
            InetAddress[] addresses = InetAddress.getAllByName(localhost.getCanonicalHostName());
            Map<InetAddress, Integer> addressScores = new HashMap<>();
            for(InetAddress address : addresses){
                int score = 0;
                byte[] bparts = address.getAddress();
                short[] parts = new short[bparts.length];
                for(int i = 0; i < bparts.length; i++){
                    if(bparts[i] < 0){
                        parts[i] = (short) (bparts[i] + 256);
                    }else{
                        parts[i] = bparts[i];
                    }
                }
                if(parts[0] == 10){
                    score += 4;
                }
                if(parts[0] == 192){
                    score++;
                    if(parts[1] == 168){
                        score++;
                        for(int i = 7; i >= 0; i--){
                            if((parts[2] & (1 << i)) == 0){
                                score++;
                            }
                        }
                    }
                }
                addressScores.put(address, score);
            }
            for(Entry<InetAddress, Integer> entry : addressScores.entrySet()){
                LOG.debug(entry.getKey() + ": " + entry.getValue());
            }
            InetAddress address = addressScores.entrySet().stream().sorted((e1, e2) -> e1.getValue() - e2.getValue()).findFirst().get().getKey();
            setProperty("osd-msg1", "http://" + address.getHostName() + ":" + embeddedServer.getPort() + "/player");
        } catch (UnknownHostException e) {
            //wat
        }

        int error;
        error = mpv.mpv_initialize(handle);
        if(error != 0){
            throw new MpvException(error, "Failed to initialize player");
        }
        listenThread = new Thread(() -> {
            while(true){
                MpvEvent event = waitEvent(10);
                LOG.debug(event.toString());
                switch(event){
                    case MPV_EVENT_SHUTDOWN:
                        return;
                    case MPV_EVENT_FILE_LOADED:
                        if(loadseek > 0){
                            seek(loadseek);
                            loadseek = 0;
                        }
                    default:
                        notifyChange();
                        break;
                }
                cleanPlaylist();
            }
        });
        listenThread.start();
    }

    private void command(String... command) throws MpvException{
        int error = mpv.mpv_command(handle, command);
        if(error != 0){
            throw new MpvException(error);
        }
    }

    private String getProperty(String name){
        Pointer p = mpv.mpv_get_property_string(handle, name);
        if(p == null) return null;
        return p.getString(0, "UTF-8");
    }

    private int getIntProperty(String name){
        return Integer.parseInt(getProperty(name));
    }

    private double getDoubleProperty(String name){
        return Double.parseDouble(getProperty(name));
    }

    private boolean getBoolProperty(String name){
        String prop = getProperty(name);
        if(prop.equals("yes")){
            return true;
        }else if(prop.equals("no")){
            return false;
        }else{
            throw new RuntimeException("Unexpected bool: " + prop);
        }
    }

    private void setProperty(String name, String value){
        int error = mpv.mpv_set_property_string(handle, name, value);
        if(error != 0){
            throw new RuntimeException("oops: " + error);
        }
    }

    public int playlistPos(){
        return getIntProperty("playlist-current-pos");
    }

    public int playlistSize(){
        return getIntProperty("playlist-count");
    }

    public boolean isPaused(){
        return getBoolProperty("pause");
    }

    private MpvEvent waitEvent(double timeout){
        mpv_event nativeEvent = mpv.mpv_wait_event(handle, 10);
        MpvEvent event = MpvEvent.fromValue(nativeEvent.event_id);
        return event;
    }

    @Override
    public void setProperty(PlayerOption option) throws MpvException{
        synchronized(this){
            int error = mpv.mpv_set_property_string(handle, option.name(), option.value());
            if(error != 0){
                //TODO: not this
                throw new MpvException(error);
            }
        }
    }

    private void setOption(PlayerOption option) throws MpvException{
        synchronized(this){
            int error = mpv.mpv_set_option_string(handle, option.name(), option.value());
            if(error != 0){
                //TODO: not this
                throw new MpvException(error);
            }
        }
    }

    @Override
    public void enqueue(String source) {
        synchronized(this){
            command("loadfile", source, "append-play");
            notifyChange();
        }
    }

    @Override
    public List<VideoInfo> getPlaylist() {
        List<VideoInfo> playlist = new ArrayList<>(playlistSize());
        int playlistSize = getIntProperty("playlist-count");
        for(int i = 0; i < playlistSize; i++){
            String title = getProperty("playlist/" + i + "/title");
            String source = getProperty("playlist/" + i + "/filename");
            if(title == null){
                CompletableFuture<String> titleFuture = titleProvider.getTitle(source);
                //Cheeky cheeky, we're not going to bother waiting, we'll get it from cache on the next pass anyways
                title = titleFuture.getNow("Pending...");
                if(title == null){
                    LOG.warn(source + " has no title!");
                }
            }
            playlist.add(new VideoInfo(title, source));
        }
        return playlist;
    }

    @Override
    public VideoInfo nowPlaying() {
        String title = getProperty("media-title");
        String source = getProperty("path");
        if(source == null) return null;
        return new VideoInfo(title, source);
    }

    @Override
    public void playIndex(int index) {
        command("playlist-play-index", Integer.toString(index));
    }

    public double playingLength(){
        String prop = getProperty("duration/full");
        if(prop == null) return 0;
        return getDoubleProperty("duration/full");
    }

    @Override
    public double playingPosition(){
        String prop = getProperty("time-pos/full");
        if(prop == null) return 0;
        return getDoubleProperty("time-pos/full");
    }

    public boolean getSubtitles(){
        String prop = getProperty("sid");
        if(prop == null) return false;
        return !prop.equals("no");
    }

    @Override
    public PlayerState getState() {
        return new PlayerState(getPlaylist(), playlistPos(), isPaused(), playingLength(), playingPosition(), quality, getSubtitles());
    }

    @Override
    public PlayerState getStateWithoutPlaylist(){
        return new PlayerState(null, playlistPos(), isPaused(), playingLength(), playingPosition(), quality, getSubtitles());
    }

    @Override
    public void prev() {
        try{
            command("playlist-prev");
        }catch(MpvException e){
            
        }
        notifyChange();
    }

    @Override
    public void pause() {
        setProperty("pause", "yes");
        notifyChange();
    }

    @Override
    public void play() {
        setProperty("pause", "no");
        notifyChange();
    }

    @Override
    public void next() {
        try{
            command("playlist-next");
        }catch(MpvException e){

        }
        notifyChange();
    }

    @Override
    public void seek(double pos){
        command("seek", Double.toString(pos), "absolute");
    }

    @Override
    public void seekRelative(double pos){
        command("seek", Double.toString(pos), "relative");
    }

    //Thank you https://github.com/christoph-heinrich/mpv-quality-menu
    //who thanks https://github.com/4e6/mpv-reload/
    private void standingReload(){
        double duration = playingLength();
        double position = playingPosition();
        //Check for live streams
        if(duration > 0){
            loadseek = position;
            playIndex(playlistPos());
        }
    }

    @Override
    public void setQuality(int scan){
        this.quality = scan;
        String format = "bv*[height<=" + scan + "]+ba/b[height<=" + scan + "]/bv+ba/b";
        LOG.debug(format);
        setProperty("ytdl-format", format);
        standingReload();
    }

    @Override
    public void subtitles(boolean on){
        setProperty("sid", on ? "auto" : "no");
        notifyChange();
    }

    private Consumer<PlayerState> changeListener;

    public void onChange(Consumer<PlayerState> listener){
        this.changeListener = listener;
    }

    private void notifyChange(){
        if(changeListener != null){
            changeListener.accept(getState());
        }
        setProperty("osd-level", playlistPos() < 0 ? "1" : "0");
    }

    @Override
    public void toast(String message) {
        command("show-text", message, Integer.toString(-1), Integer.toString(0));
    }

    @Override
    public void remove(int index) {
        command("playlist-remove", Integer.toString(index));
        notifyChange();
    }

    private void cleanPlaylist(){
        List<VideoInfo> playlist = getPlaylist();
        for(int i = 0; i < playlist.size(); i++){
            if(playlist.get(i).getName() == null){
                LOG.debug("Bad entry: " + playlist.get(i).getSource());
                command("playlist-remove", Integer.toString(i));
                cleanPlaylist();
                break;
            }
        }
    }
}
