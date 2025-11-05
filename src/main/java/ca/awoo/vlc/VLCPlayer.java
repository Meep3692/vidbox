package ca.awoo.vlc;

import java.util.List;
import java.util.function.Consumer;

import ca.awoo.Player;
import ca.awoo.PlayerOption;
import ca.awoo.PlayerState;
import ca.awoo.VideoInfo;

public class VLCPlayer implements Player {

    // @Override
    public void setProperty(PlayerOption option) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setProperty'");
    }

    @Override
    public void enqueue(String source) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'enqueue'");
    }

    @Override
    public void playIndex(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'playIndex'");
    }

    @Override
    public List<VideoInfo> getPlaylist() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPlaylist'");
    }

    @Override
    public VideoInfo nowPlaying() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nowPlaying'");
    }

    @Override
    public double playingPosition() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'playingPosition'");
    }

    @Override
    public void onChange(Consumer<PlayerState> listener) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onChange'");
    }

    @Override
    public PlayerState getState() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getState'");
    }

    @Override
    public PlayerState getStateWithoutPlaylist() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStateWithoutPlaylist'");
    }

    @Override
    public void prev() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'prev'");
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void play() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'play'");
    }

    @Override
    public void next() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'next'");
    }

    @Override
    public void seek(double pos) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'seek'");
    }

    @Override
    public void seekRelative(double pos) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'seekRelative'");
    }

    @Override
    public void setQuality(int scan) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setQuality'");
    }

    @Override
    public void subtitles(boolean on) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'subtitles'");
    }

    @Override
    public void toast(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toast'");
    }

    @Override
    public void remove(int index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }
    
}
