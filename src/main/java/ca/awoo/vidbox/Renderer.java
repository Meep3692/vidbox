package ca.awoo.vidbox;

public interface Renderer {
    public enum Whence{
        CURRENT,
        BEGGINING
    }
    public void playStream(Stream stream);
    public void seek(float offset, Whence whence);
    public float getPos();
    public boolean isPlaying();
    public default void swapStream(Stream newStream){
        float oldPos = getPos();
        playStream(newStream);
        seek(oldPos, Whence.BEGGINING);
    }

    public Event<Stream> onStreamEnd();
}
