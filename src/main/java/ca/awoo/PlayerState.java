package ca.awoo;

import java.util.List;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class PlayerState {
    public final List<VideoInfo> playlist;
    public final int position;
    public PlayerState(List<VideoInfo> playlist, int position) {
        this.playlist = playlist;
        this.position = position;
    }
    public List<VideoInfo> getPlaylist() {
        return playlist;
    }
    public int getPosition() {
        return position;
    }
}
