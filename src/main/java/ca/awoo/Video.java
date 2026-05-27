package ca.awoo;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

public interface Video {
    String getSource();
    CompletableFuture<String> getTitle();
    CompletableFuture<BufferedImage> getThumbnail();
    CompletableFuture<StreamInfo> getStream(int quality);
}
