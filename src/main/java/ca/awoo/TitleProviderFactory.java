package ca.awoo;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class TitleProviderFactory {
    @Bean
    public TitleProvider ytdlTitleProvider(){
        return new YtdlTitleProvider("yt-dlp", "--cookies-from-browser", "firefox");
    }
}
