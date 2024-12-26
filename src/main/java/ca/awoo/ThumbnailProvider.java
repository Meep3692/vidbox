package ca.awoo;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import jakarta.inject.Singleton;

@Singleton
public interface ThumbnailProvider {
    public CompletableFuture<InputStream> getThumbnail(String source);
}
