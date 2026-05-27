package ca.awoo;

import java.util.List;
import java.util.ArrayList;

public class StreamInfo {
    public final String videoUrl;
    public final String audioUrl;
    public final List<String> subtitleUrls;

    public StreamInfo(String videoUrl, String audioUrl) {
        this(videoUrl, audioUrl, new ArrayList<>());
    }

    public StreamInfo(String videoUrl, String audioUrl, List<String> subtitleUrls) {
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
        this.subtitleUrls = subtitleUrls;
    }
}
