package ca.awoo;

import java.util.List;

public interface Player {
    public void setOption(PlayerOption option);
    public void enqueue(String source);
    public List<String> getPlaylist();
}
