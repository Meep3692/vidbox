package ca.awoo;

import java.util.List;

import jakarta.inject.Singleton;

@Singleton
public interface Player {
    public void setOption(PlayerOption option);
    public void enqueue(String source);
    public List<String> getPlaylist();
}
