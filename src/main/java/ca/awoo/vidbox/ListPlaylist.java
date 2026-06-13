package ca.awoo.vidbox;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ListPlaylist implements Playlist{
    private final Renderer renderer;

    private final List<Stream> streams = new ArrayList<>();
    private int position = 0;

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
    public void enqueue(Stream stream){
        streams.add(stream);
        log.info("Enqueued stream: " + stream);
        if(!renderer.isPlaying()){
            log.info("Renderer not playing, starting renderer");
            position = streams.size()-1;
            renderer.playStream(stream);
        }else{
            log.info("Renderer already playing. Leaving it alone");
        }
    }

    @Override
    public void next() {
        log.info("Moving to next file");
        position++;
        if(position < streams.size()){
            log.info("Playing next file");
            renderer.playStream(streams.get(position));
        }else{
            log.info("End of list");
            position = streams.size();
        }
    }

    @Override
    public void prev() {
        position--;
        if(position < 0) position = 0;
        if(position < streams.size()){
            renderer.playStream(streams.get(position));
        }
    }
}
