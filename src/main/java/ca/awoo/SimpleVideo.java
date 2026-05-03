package ca.awoo;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;

public class SimpleVideo implements Video {
    private final String source;
    private static BufferedImage noThumbnail;

    static {
        try {
            noThumbnail = ImageIO.read(SimpleVideo.class.getResource("/video.png"));
        } catch (Exception e) {
            // ignore
        }
    }

    public SimpleVideo(String source) {
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public CompletableFuture<String> getTitle() {
        return CompletableFuture.completedFuture(source);
    }

    @Override
    public CompletableFuture<BufferedImage> getThumbnail() {
        return CompletableFuture.completedFuture(noThumbnail);
    }
}
