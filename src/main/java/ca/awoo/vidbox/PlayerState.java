package ca.awoo.vidbox;

import java.util.List;

public class PlayerState {
    public final List<Video> playlist;
    public final int playing;
    public final boolean paused;
    public final double playingLength;
    public final double playingPosition;
    public final int quality;
    public final boolean subs;
    
    public PlayerState(List<Video> playlist, int playing, boolean paused, double playingLength,
            double playingPosition, int quality, boolean subs) {
        this.playlist = playlist;
        this.playing = playing;
        this.paused = paused;
        this.playingLength = playingLength;
        this.playingPosition = playingPosition;
        this.quality = quality;
        this.subs = subs;
    }
    public List<Video> getPlaylist() {
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
    public int getQuality(){
        return quality;
    }
    public boolean getSubs(){
        return subs;
    }
}
