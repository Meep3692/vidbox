package ca.awoo;

import java.util.List;
import java.util.function.Consumer;

import jakarta.inject.Singleton;

@Singleton
public interface Player {
    public void setOption(PlayerOption option);
    public void enqueue(String source);
    public void playIndex(int index);
    public List<VideoInfo> getPlaylist();
    public void onChange(Consumer<PlayerState> listener);
    public PlayerState getState();
    public PlayerState getStateWithoutPlaylist();
    public void prev();
    public void pause();
    public void play();
    public void next();
    public void seek(double pos);
}
