package ca.awoo.vidbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class Playlist{

    private final List<Video> videos = new ArrayList<>();
    private int position = 0;

    Logger log = Logger.getLogger(getClass().getName());

    public void enqueue(Video video){
        videos.add(video);
    }

    public Optional<Video> next() {
        position++;
        if(position < videos.size()){
            log.info("Playing next file");
            return Optional.of(videos.get(position));
        }else{
            log.info("End of list");
            position = videos.size();
            return Optional.empty();
        }
    }

    public Optional<Video> prev() {
        log.info("Moving to previous file");
        position--;
        if(position < 0) position = 0;
        if(position < videos.size()){
            log.info("Playing previous file");
            return Optional.of(videos.get(position));
        }
        return Optional.empty();
    }

    public Optional<Video> end(){
        position = videos.size()-1;
        if(position < 0) position = 0;
        if(position < videos.size()){
            log.info("Playing last file");
            return Optional.of(videos.get(position));
        }
        return Optional.empty();
    }

    public Optional<Video> skipTo(int pos){
        this.position = pos;
        if(position < 0) position = 0;
        if(position < videos.size()){
            log.info("Playing file at " + position);
            return Optional.of(videos.get(position));
        }
        return Optional.empty();
    }

    public List<Video> getVideos() {
        return videos;
    }

    public int getPosition() {
        return position;
    }
    
}
