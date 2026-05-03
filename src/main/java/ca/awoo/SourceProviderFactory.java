package ca.awoo;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class SourceProviderFactory {
    @Bean
    public SourceProvider sourceProvider(YtdlExecutor ytdlExecutor){
        YtdlSourceProvider ytdl = new YtdlSourceProvider(ytdlExecutor);
        return new CompositeSourceProvider(java.util.Collections.singletonList(ytdl));
    }
}

