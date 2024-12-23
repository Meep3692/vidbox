package ca.awoo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import ca.awoo.MPV.mpv_event;

public class MpvPlayer implements Player {
    private final MPV mpv;
    private final long handle;

    private Thread listenThread;

    public MpvPlayer(PlayerOption... options) throws MpvException{
        this.mpv = MPV.INSTANCE;
        if(Platform.isLinux())
            CLib.INSTANCE.setlocale(CLib.LC_NUMERIC, "C");
        this.handle = mpv.mpv_create();
        if(handle == 0) throw new RuntimeException("Cannot make MPV");
        for(PlayerOption option : options){
            setOption(option);
        }
        int error;
        error = mpv.mpv_initialize(handle);
        if(error != 0){
            throw new MpvException(error, "Failed to initialize player");
        }
        listenThread = new Thread(() -> {
            while(true){
                MpvEvent event = waitEvent(10);
                System.out.println(event);
                switch(event){
                    case MPV_EVENT_SHUTDOWN:
                        return;
                    default:
                        notifyChange();
                        break;
                }
            }
        });
        listenThread.start();
    }

    private void command(String... command){
        int error = mpv.mpv_command(handle, command);
        if(error != 0){
            throw new RuntimeException("oops: " + error);
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
    public void setOption(PlayerOption option) {
        synchronized(this){
            int error = mpv.mpv_set_property_string(handle, option.name(), option.value());
            if(error != 0){
                //TODO: not this
                throw new RuntimeException("oops");
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
            if(title == null){
                title = "Pending...";
            }
            String source = getProperty("playlist/" + i + "/filename");
            playlist.add(new VideoInfo(title, source));
        }
        return playlist;
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

    public double playingPosition(){
        String prop = getProperty("time-pos/full");
        if(prop == null) return 0;
        return getDoubleProperty("time-pos/full");
    }

    @Override
    public PlayerState getState() {
        return new PlayerState(getPlaylist(), playlistPos(), isPaused(), playingLength(), playingPosition());
    }

    @Override
    public PlayerState getStateWithoutPlaylist(){
        return new PlayerState(null, playlistPos(), isPaused(), playingLength(), playingPosition());
    }

    @Override
    public void prev() {
        command("playlist-prev");
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
        command("playlist-next");
        notifyChange();
    }

    @Override
    public void seek(double pos){
        command("seek", Double.toString(pos), "absolute");
    }

    private Consumer<PlayerState> changeListener;

    public void onChange(Consumer<PlayerState> listener){
        this.changeListener = listener;
    }

    private void notifyChange(){
        if(changeListener != null){
            changeListener.accept(getState());
        }
    }
}
