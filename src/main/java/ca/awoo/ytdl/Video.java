package ca.awoo.ytdl;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class Video {
    private final String id;
    private final String title;
    private final Format[] formats;
    private final Thumbnail[] thumbnails;
    private final String thumbnail;
    private final String description;
    @JsonProperty("channel_id")
    private final String channelId;
    @JsonProperty("channel_url")
    private final String channelUrl;
    @JsonProperty("view_count")
    private final int viewCount;
    @JsonProperty("automatic_captions")
    private final Map<String, Caption[]> automaticCaptions;
    private final Map<String, Caption[]> subtitles;
    private final String location;
    @JsonProperty("like_count")
    private final int likeCount;
    private final String uploader;
    @JsonProperty("uploader_id")
    private final String uploaderId;
    @JsonProperty("uploader_url")
    private final String uploaderUrl;
    @JsonProperty("original_url")
    private final String originalUrl;
    private final String fulltitle;
    public Video(String id, String title, Format[] formats, Thumbnail[] thumbnails, String thumbnail,
            String description, String channelId, String channelUrl, int viewCount,
            Map<String, Caption[]> automaticCaptions, Map<String, Caption[]> subtitles, String location, int likeCount,
            String uploader, String uploaderId, String uploaderUrl, String originalUrl, String fulltitle) {
        this.id = id;
        this.title = title;
        this.formats = formats;
        this.thumbnails = thumbnails;
        this.thumbnail = thumbnail;
        this.description = description;
        this.channelId = channelId;
        this.channelUrl = channelUrl;
        this.viewCount = viewCount;
        this.automaticCaptions = automaticCaptions;
        this.subtitles = subtitles;
        this.location = location;
        this.likeCount = likeCount;
        this.uploader = uploader;
        this.uploaderId = uploaderId;
        this.uploaderUrl = uploaderUrl;
        this.originalUrl = originalUrl;
        this.fulltitle = fulltitle;
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public Format[] getFormats() {
        return formats;
    }
    public Thumbnail[] getThumbnails() {
        return thumbnails;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public String getDescription() {
        return description;
    }
    public String getChannelId() {
        return channelId;
    }
    public String getChannelUrl() {
        return channelUrl;
    }
    public int getViewCount() {
        return viewCount;
    }
    public Map<String, Caption[]> getAutomaticCaptions() {
        return automaticCaptions;
    }
    public Map<String, Caption[]> getSubtitles() {
        return subtitles;
    }
    public String getLocation() {
        return location;
    }
    public int getLikeCount() {
        return likeCount;
    }
    public String getUploader() {
        return uploader;
    }
    public String getUploaderId() {
        return uploaderId;
    }
    public String getUploaderUrl() {
        return uploaderUrl;
    }
    public String getOriginalUrl() {
        return originalUrl;
    }
    public String getFulltitle() {
        return fulltitle;
    }

    
}
