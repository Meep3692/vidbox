package ca.awoo.vidbox;

import jakarta.ejb.Startup;

@Startup
public interface Player {
    public void enqueue(String source);
    public PlayerState getState(boolean includePlaylist);
    public void pause();
    public void resume();
    public void skipTo(int index);
    public void prev();
    public void next();
    public void stop();
    public void seek(float offest, Whence whence);
    public void setQuality(int quality);
    public void setSubtitles(boolean subs);
    public void remove(int index);
}
