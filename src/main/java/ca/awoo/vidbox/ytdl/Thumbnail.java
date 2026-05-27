package ca.awoo.vidbox.ytdl;

public class Thumbnail {
    private final String url;
    private final int preference;
    private final String id;
    
    public Thumbnail(String url, int preference, String id) {
        this.url = url;
        this.preference = preference;
        this.id = id;
    }
    public String getUrl() {
        return url;
    }
    public int getPreference() {
        return preference;
    }
    public String getId() {
        return id;
    }
}
