package ca.awoo.vidbox;

import java.net.URI;

import ca.awoo.vidbox.StreamSet.QualityStream;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DirectVideoService implements VideoService{

    @Override
    public Video getVideo(String source) {
        URI uri = URI.create(source);
        String[] path = uri.getPath().split("/");
        String title = path[path.length-1];
        Stream stream = new Stream(uri, null, null);
        StreamSet streamSet = new StreamSet(new QualityStream(0, stream));
        Video video = new Video(title, "", source, streamSet);
        return video;
    }
    
}
