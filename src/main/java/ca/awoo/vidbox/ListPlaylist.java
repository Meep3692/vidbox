package ca.awoo.vidbox;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ListPlaylist implements Playlist{
    private final Renderer renderer;

    private final List<Video> videos = new ArrayList<>();
    private int position = 0;
    private int quality = 480;

    Logger log = Logger.getLogger(getClass().getName());

    @Inject
    public ListPlaylist(Renderer renderer){
        this.renderer = renderer;
        renderer.onStreamEnd().add((playing) -> {
            next();
        });
    }

    public ListPlaylist(){
        throw new RuntimeException("Called the bad constructor");
    }

    @Override
    public void enqueue(Video video){
        videos.add(video);
        log.info("Enqueued stream: " + video);
        if(renderer.isIdle()){
            log.info("Renderer not playing, starting renderer");
            position = videos.size()-1;
            renderer.playStream(video.streams().getStream(quality));
        }else{
            log.info("Renderer already playing. Leaving it alone");
        }
    }

    @Override
    public void next() {
        log.info("Moving to next file");
        position++;
        if(position < videos.size()){
            log.info("Playing next file");
            renderer.playStream(videos.get(position).streams().getStream(quality));
        }else{
            log.info("End of list");
            position = videos.size();
        }
    }

    @Override
    public void prev() {
        log.info("Moving to previous file");
        position--;
        if(position < 0) position = 0;
        if(position < videos.size()){
            log.info("Playing previous file");
            renderer.playStream(videos.get(position).streams().getStream(quality));
        }
    }

    @Override
    public List<Video> getVideos() {
        return videos;
    }

    @Override
    public int getPosition() {
        return position;
    }
    
}
