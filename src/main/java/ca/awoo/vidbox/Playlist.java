package ca.awoo.vidbox;

import java.util.List;

public interface Playlist {
    public void enqueue(Video video);
    public void next();
    public void prev();
    public List<Video> getVideos();
    public int getPosition();
}
