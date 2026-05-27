package ca.awoo.ytdl;

import jakarta.json.bind.annotation.JsonbProperty;

public class Format {
    @JsonbProperty("format_id")
    private final String formatId;
    @JsonbProperty("format_note")
    private final String formatNote;
    private final String format;
    private final String ext;
    private final String protocol;
    private final String acodec;
    private final String vcodec;
    private final String url;
    private final int width;
    private final int height;
    private final double fps;
    @JsonbProperty("audio_ext")
    private final String audioExt;
    @JsonbProperty("video_ext")
    private final String videoExt;
    private final int quality;

    public Format(String formatId, String formatNote, String format, String ext, String protocol, String acodec,
            String vcodec, String url, int width, int height, double fps, String audioExt, String videoExt,
            int quality) {
        this.formatId = formatId;
        this.formatNote = formatNote;
        this.format = format;
        this.ext = ext;
        this.protocol = protocol;
        this.acodec = acodec;
        this.vcodec = vcodec;
        this.url = url;
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.audioExt = audioExt;
        this.videoExt = videoExt;
        this.quality = quality;
    }

    public int getQuality() {
        return quality;
    }

    public String getFormatId() {
        return formatId;
    }

    public String getFormatNote() {
        return formatNote;
    }

    public String getFormat() {
        return format;
    }

    public String getExt() {
        return ext;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getAcodec() {
        return acodec;
    }

    public String getVcodec() {
        return vcodec;
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getFps() {
        return fps;
    }

    public String getAudioExt() {
        return audioExt;
    }

    public String getVideoExt() {
        return videoExt;
    }

}
