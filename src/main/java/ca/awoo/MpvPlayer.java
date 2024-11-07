package ca.awoo;

import java.util.ArrayList;
import java.util.List;

import ca.awoo.MPV.mpv_event;

public class MpvPlayer implements Player {
    private final MPV mpv;
    private final long handle;

    private List<String> playlist;
    private int position = 0;

    private Thread listenThread;

    public MpvPlayer(PlayerOption... options) throws MpvException{
        this.mpv = MPV.INSTANCE;
        this.handle = mpv.mpv_create();
        for(PlayerOption option : options){
            setOption(option);
        }
        int error;
        error = mpv.mpv_initialize(handle);
        if(error != 0){
            throw new MpvException(error, "Failed to initialize player");
        }
        playlist = new ArrayList<>();
        listenThread = new Thread(() -> {
            while(true){
                MpvEvent event = waitEvent(10);
                System.out.println(event);
                if(event.equals(MpvEvent.MPV_EVENT_SHUTDOWN)){
                    break;
                }
                if(event.equals(MpvEvent.MPV_EVENT_END_FILE)){
                    position++;
                }
            }
        });
        listenThread.start();
    }

    private MpvEvent waitEvent(double timeout){
        mpv_event nativeEvent = mpv.mpv_wait_event(handle, 10);
        MpvEvent event = MpvEvent.fromValue(nativeEvent.event_id);
        return event;
    }

    @Override
    public void setOption(PlayerOption option) {
        int error = mpv.mpv_set_property_string(handle, option.name(), option.value());
        if(error != 0){
            //TODO: not this
            throw new RuntimeException("oops");
        }
    }

    @Override
    public void enqueue(String source) {
        int error = mpv.mpv_command(handle, new String[]{"loadfile", source, "append-play"});
        if(error != 0){
            throw new RuntimeException("oops");
        }
        playlist.add(source);
    }

    @Override
    public List<String> getPlaylist() {
        return playlist;
    }
    
}
