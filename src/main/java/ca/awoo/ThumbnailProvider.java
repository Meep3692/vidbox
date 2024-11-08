package ca.awoo;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import jakarta.inject.Singleton;

@Singleton
public interface ThumbnailProvider {
    public CompletableFuture<File> getThumbnail(String source);
}
