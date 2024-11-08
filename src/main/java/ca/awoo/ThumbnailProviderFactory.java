package ca.awoo;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class ThumbnailProviderFactory {
    @Bean
    public ThumbnailProvider ytdlpProvider(){
        return new YtdlThumbnailProvider("yt-dlp", "--cookies-from-browser", "firefox");
    }
}
