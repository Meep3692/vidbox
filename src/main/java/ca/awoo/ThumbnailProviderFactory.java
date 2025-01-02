package ca.awoo;

import java.io.IOException;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class ThumbnailProviderFactory {
    @Bean
    public ThumbnailProvider ytdlpProvider() throws IOException{
        return new YtdlThumbnailProvider("yt-dlp", "--cookies-from-browser", "firefox");
    }
}
