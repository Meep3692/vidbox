package ca.awoo;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class VideoInfo {
    private final String name;
    private final String source;

    public VideoInfo(String name, String source){
        this.name = name;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "VideoInfo [name=" + name + ", source=" + source + "]";
    }
    
}
