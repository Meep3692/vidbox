package ca.awoo.vidbox.ytdl;

public class Caption {
    private final String url;
    private final String ext;
    private final String protocol;
    private final String name;
    public Caption(String url, String ext, String protocol, String name) {
        this.url = url;
        this.ext = ext;
        this.protocol = protocol;
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public String getExt() {
        return ext;
    }
    public String getProtocol() {
        return protocol;
    }
    public String getName() {
        return name;
    }
    
}
