package ca.awoo.vidbox;

public interface Player {
    public void enqueue(String source);
    public PlayerState getState(boolean includePlaylist);
    public void pause();
    public void resume();
    public void skipTo(int index);
}
