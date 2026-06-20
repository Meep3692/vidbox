package ca.awoo.vidbox;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlayerImpl implements Player {

    private final Renderer renderer;
    private final VideoService videoService;

    private Playlist playlist = new Playlist();
    private int quality = 480;

    @Inject
    public PlayerImpl(Renderer renderer, VideoService videoService){
        this.renderer = renderer;
        this.videoService = videoService;
        renderer.onStreamEnd().add((s) -> {
            next();
        });
        enqueue("https://www.youtube.com/watch?v=9vcPdFhIwjE");
        enqueue("https://www.youtube.com/watch?v=IWvBF6u0cIg");
        enqueue("https://www.youtube.com/watch?v=v4ss4B3CYzc");
    }

    public PlayerImpl(){
        throw new RuntimeException("Please don't call this");
    }

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
        playlist.skipTo(index).ifPresent(this::playVideo);
    }

    @Override
    public void prev() {
        playlist.prev().ifPresent(this::playVideo);
    }

    @Override
    public void next() {
        playlist.next().ifPresent(this::playVideo);
    }

    @Override
    public void stop() {
        renderer.stop();
        playlist.skipTo(0);
    }

    @Override
    public void seek(float offest, Whence whence) {
        renderer.seek(offest, whence);
    }

    @Override
    public void setQuality(int quality) {
        this.quality = quality;
        if(!renderer.isIdle()){
            playlist.getVideo().ifPresent((v) -> {
                renderer.swapStream(v.streams().getStream(quality));
            });
        }
    }

    @Override
    public void setSubtitles(boolean subs) {
        renderer.setSubtitles(subs);
    }

    @Override
    public void remove(int index) {
        if(playlist.remove(index)){
            playlist.getVideo().ifPresent(this::playVideo);
        }
    }
    
}
