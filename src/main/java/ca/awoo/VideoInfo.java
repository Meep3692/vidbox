package ca.awoo;

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
    
}
