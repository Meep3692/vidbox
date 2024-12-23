package ca.awoo;

import java.util.List;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class PlayerState {
    public final List<VideoInfo> playlist;
    public final int playing;
    public final boolean paused;
    public final double playingLength;
    public final double playingPosition;
    
    public PlayerState(List<VideoInfo> playlist, int playing, boolean paused, double playingLength,
            double playingPosition) {
        this.playlist = playlist;
        this.playing = playing;
        this.paused = paused;
        this.playingLength = playingLength;
        this.playingPosition = playingPosition;
    }
    public List<VideoInfo> getPlaylist() {
        return playlist;
    }
    public int getPlaying() {
        return playing;
    }
    public boolean getPaused(){
        return paused;
    }
    public double getPlayingLength(){
        return playingLength;
    }
    public double getPlayingPosition(){
        return playingPosition;
    }
}
