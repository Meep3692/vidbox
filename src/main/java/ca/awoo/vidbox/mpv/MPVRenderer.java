package ca.awoo.vidbox.mpv;

import java.util.logging.Logger;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import ca.awoo.vidbox.CLib;
import ca.awoo.vidbox.Event;
import ca.awoo.vidbox.Renderer;
import ca.awoo.vidbox.Stream;
import ca.awoo.vidbox.Whence;
import ca.awoo.vidbox.mpv.MPV.mpv_event;
import ca.awoo.vidbox.mpv.MPV.mpv_event_end_file;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MPVRenderer implements Renderer {
    private final MPV mpv;
    private final long handle;
    private final Thread listenThread;

    private Stream currenStream;

    Logger log = Logger.getLogger(getClass().getName());

    private boolean supressEnd = false;

    public MPVRenderer(){
        this.mpv = MPV.INSTANCE;
        if(Platform.isLinux()){
            CLib.INSTANCE.setlocale(CLib.LC_NUMERIC, "C");
        }
        this.handle = mpv.mpv_create();
        if(handle == 0) throw new RuntimeException("Cannot make MPV");
        mpv.mpv_set_property_string(handle, "idle", "yes");
        mpv.mpv_set_property_string(handle, "force-window", "immediate");
        mpv.mpv_set_property_string(handle, "input-default-bindings", "yes");
        mpv.mpv_set_property_string(handle, "input-builtin-bindings", "yes");
        mpv.mpv_set_property_string(handle, "auto-window-resize", "no");
        mpv.mpv_set_property_string(handle, "slang", "en");
        mpv.mpv_set_property_string(handle, "sub-auto", "fuzzy");
        mpv.mpv_set_property_string(handle, "osc", "yes");

        int error = mpv.mpv_initialize(handle);
        if(error != 0){
            throw new MpvException(error, "Failed to init player");
        }

        listenThread = new Thread(() -> {
            while(true){
                mpv_event nativeEvent = mpv.mpv_wait_event(handle, 10);
                MpvEvent event = MpvEvent.fromValue(nativeEvent.event_id);
                log.info("Event " + event);
                switch(event){
                    case MPV_EVENT_END_FILE:
                        if(!supressEnd){
                            mpv_event_end_file endFile = Structure.newInstance(mpv_event_end_file.class, nativeEvent.data);
                            log.info("Reason for file end: " + endFile.reason);
                            if(endFile.reason == 0){ //EOF
                                //This should work but it doesn't
                                //reason is always 0 no matter what
                                log.info("File end event fire");
                                onStreamEndEvent.fire(currenStream);
                            }
                        }else{
                            log.info("End event was supressed");
                            supressEnd = false;
                            log.info("Unsupressed end events");
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        listenThread.start();
    }

    private void command(String... args) throws MpvException {
        int error = mpv.mpv_command(handle, args);
        if(error != 0){
            throw new MpvException(error);
        }
    }

    private String getProperty(String name) {
        Pointer p = mpv.mpv_get_property_string(handle, name);
        if(p == null) return null;
        return p.getString(0, "UTF-8");
    }

    private void setProperty(String name, String value){
        int err = mpv.mpv_set_property_string(handle, name, value);
        if(err != 0){
            throw new MpvException(err);
        }
    }

    private float getFloatProperty(String name){
        String propString = getProperty(name);
        if(propString == null) return 0;
        return Float.parseFloat(propString);
    }

    private boolean getBoolProperty(String name){
        String propString = getProperty(name);
        if(propString == null) return false;
        if("yes".equals(propString)){
            return true;
        }else if("no".equals(propString)){
            return false;
        }else{
            throw new RuntimeException("Bad boolean value: " + propString);
        }
    }

    @Override
    public void playStream(Stream stream) {
        supressEnd = currenStream != null;
        log.info("Supressing end events");
        stop();
        log.info("Stopped player");
        currenStream = stream;
        command("loadfile", stream.video().toASCIIString());
        log.info("Loaded file");
        if(stream.audio() != null){
            command("audio-add", stream.audio().toASCIIString());
        }
        if(stream.subtitles() != null){
            command("sub-add", stream.subtitles().toASCIIString());
        }
    }

    @Override
    public void seek(float offset, Whence whence) {
        String flag = whence==Whence.BEGGINING ? "absolute" : "relative";
        command("seek", Float.toString(offset), "exact+" + flag);
    }

    @Override
    public float getPos() {
        return getFloatProperty("time-pos/full");
    }

    @Override
    public float getDuration() {
        return getFloatProperty("duration/full");
    }

    @Override
    public boolean isIdle() {
        return getBoolProperty("idle-active");
    }

    @Override
    public boolean isPaused() {
        return getBoolProperty("pause");
    }

    @Override
    public void pause() {
        setProperty("pause", "yes");
    }

    @Override
    public void resume() {
        setProperty("pause", "no");
    }

    private final Event<Stream> onStreamEndEvent = new Event<>();

    @Override
    public Event<Stream> onStreamEnd() {
        return onStreamEndEvent;
    }

    @Override
    public void stop() {
        command("stop");
    }

    @Override
    public void setSubtitles(boolean subtitles) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSubtitles'");
    }

    @Override
    public void getSubtitles() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSubtitles'");
    }
    
    
}
