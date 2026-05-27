package ca.awoo;

import io.micronaut.json.JsonMapper;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class SourceProviderFactory {
    @Bean
    public SourceProvider sourceProvider(YtdlExecutor ytdlExecutor, JsonMapper jsonMapper){
        YtdlSourceProvider ytdl = new YtdlSourceProvider(ytdlExecutor, jsonMapper);
        return new CompositeSourceProvider(java.util.Collections.singletonList(ytdl));
    }
}

