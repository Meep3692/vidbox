package ca.awoo;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class SourceProviderFactory {
    @Bean
    public SourceProvider ytdlSourceProvider(){
        // Mirror the cookies/browser options used elsewhere so yt-dlp behaves consistently.
        return new YtdlSourceProvider("yt-dlp", "--cookies-from-browser", "firefox");
    }
}

