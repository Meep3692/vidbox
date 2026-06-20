package ca.awoo.vidbox;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlayerImpl implements Player {

    @Inject
    private Renderer renderer;

    @Inject
    private VideoService videoService;

    private Playlist playlist = new Playlist();
    private int quality = 480;

    private void playVideo(Video vid){
        renderer.playStream(vid.streams().getStream(quality));
    }

    @Override
    public void enqueue(String source) {
        Video vid = videoService.getVideo(source);
        playlist.enqueue(vid);
        if(renderer.isIdle()){
            playlist.end();
            playVideo(vid);
        }
    }

    @Override
    public PlayerState getState(boolean includePlaylist) {
        List<Video> vidList = includePlaylist ? playlist.getVideos() : null;
        PlayerState state = new PlayerState(
            vidList,
            playlist.getPosition(),
            renderer.isPaused(),
            renderer.getDuration(),
            renderer.getPos(),
            quality,
            false);
        return state;
    }

    @Override
    public void pause() {
        renderer.pause();
    }

    @Override
    public void resume() {
        renderer.resume();
    }

    @Override
    public void skipTo(int index) {
        playlist.skipTo(index).ifPresent((v) -> playVideo(v));
    }
    
}
