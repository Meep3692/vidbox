package ca.awoo.vidbox;

public interface Playlist {
    public void enqueue(Stream stream);
    public void next();
    public void prev();
}
