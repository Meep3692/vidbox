package ca.awoo;

import java.util.concurrent.CompletableFuture;

import jakarta.inject.Singleton;

@Singleton
public interface TitleProvider {
    public CompletableFuture<String> getTitle(String source);
}
