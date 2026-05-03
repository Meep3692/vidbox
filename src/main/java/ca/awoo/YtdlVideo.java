package ca.awoo;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

public class YtdlVideo implements Video {
    private final String source;
    private final YtdlSourceProvider provider;

    public YtdlVideo(String source, YtdlSourceProvider provider) {
        this.source = source;
        this.provider = provider;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public CompletableFuture<String> getTitle() {
        return provider.fetchTitle(source);
    }

    @Override
    public CompletableFuture<BufferedImage> getThumbnail() {
        return provider.fetchThumbnail(source);
    }
}
