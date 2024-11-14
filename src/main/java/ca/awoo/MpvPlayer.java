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
                    case MPV_EVENT_END_FILE:
                        notifyChange();
                        break;
                    case MPV_EVENT_FILE_LOADED:
                        notifyChange();
                        break;
                    default:
                        break;
                }
            }
        });
        listenThread.start();
    }

    private String getProperty(String name){
        Pointer p = mpv.mpv_get_property_string(handle, name);
        if(p == null) return null;
        return p.getString(0, "UTF-8");
    }

    private int getIntProperty(String name){
        return Integer.parseInt(getProperty(name));
    }

    public int playlistPos(){
        return getIntProperty("playlist-current-pos");
    }

    public int playlistSize(){
        return getIntProperty("playlist-count");
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
            int error = mpv.mpv_command(handle, new String[]{"loadfile", source, "append-play"});
            if(error != 0){
                throw new RuntimeException("oops");
            }
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
        int error = mpv.mpv_command(handle, new String[]{"playlist-play-index", Integer.toString(index)});
        if(error != 0){
            throw new RuntimeException("oops");
        }
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

    @Override
    public PlayerState getState() {
        return new PlayerState(getPlaylist(), playlistPos());
    }
    
}
