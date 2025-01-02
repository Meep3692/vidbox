package ca.awoo;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

import jakarta.inject.Singleton;

@Singleton
public interface ThumbnailProvider {
    public CompletableFuture<BufferedImage> getThumbnail(String source);
}
