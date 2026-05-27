package ca.awoo.vidbox.ytdl;

import java.util.Map;

import jakarta.json.bind.annotation.JsonbProperty;


public class Video {
    private final String id;
    private final String title;
    private final Format[] formats;
    private final Thumbnail[] thumbnails;
    private final String thumbnail;
    private final String description;
    @JsonbProperty("channel_id")
    private final String channelId;
    @JsonbProperty("channel_url")
    private final String channelUrl;
    @JsonbProperty("view_count")
    private final int viewCount;
    @JsonbProperty("automatic_captions")
    private final Map<String, Caption[]> automaticCaptions;
    private final Map<String, Caption[]> subtitles;
    private final String location;
    @JsonbProperty("like_count")
    private final int likeCount;
    private final String uploader;
    @JsonbProperty("uploader_id")
    private final String uploaderId;
    @JsonbProperty("uploader_url")
    private final String uploaderUrl;
    @JsonbProperty("original_url")
    private final String originalUrl;
    @JsonbProperty("webpage_url")
    private final String webpageUrl;
    private final String url;
    @JsonbProperty("requested_formats")
    private final Format[] requestedFormats;
    private final String fulltitle;
    public Video(String id, String title, Format[] formats, Thumbnail[] thumbnails, String thumbnail,
            String description, String channelId, String channelUrl, int viewCount,
            Map<String, Caption[]> automaticCaptions, Map<String, Caption[]> subtitles, String location, int likeCount,
            String uploader, String uploaderId, String uploaderUrl, String originalUrl, String webpageUrl, String url, Format[] requestedFormats, String fulltitle) {
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
        this.webpageUrl = webpageUrl;
        this.url = url;
        this.requestedFormats = requestedFormats;
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
    public String getUrl() {
        return url;
    }
    public String getWebpageUrl() {
        return webpageUrl;
    }
    public String getFulltitle() {
        return fulltitle;
    }
    public Format[] getRequestedFormats() {
        return requestedFormats;
    }

    
}
