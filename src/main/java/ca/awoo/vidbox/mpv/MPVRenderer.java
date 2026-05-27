package ca.awoo.vidbox.mpv;

import com.sun.jna.Platform;

import ca.awoo.vidbox.CLib;
import ca.awoo.vidbox.Renderer;
import ca.awoo.vidbox.Stream;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MPVRenderer implements Renderer {
    private final MPV mpv;
    private final long handle;

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

        int error = mpv.mpv_initialize(handle);
        if(error != 0){
            throw new MpvException(error, "Failed to init player");
        }
    }

    @Override
    public void playStream(Stream stream) {

    }

    @Override
    public void seek(float offset, Whence whence) {
    }

    @Override
    public float getPos() {
        return 0;
    }
    
}
