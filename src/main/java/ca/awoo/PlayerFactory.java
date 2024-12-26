package ca.awoo;

import java.io.IOException;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.runtime.server.EmbeddedServer;

@Factory
public class PlayerFactory {
    @Bean
    Player mpvPlayer(EmbeddedServer embeddedServer, TitleProvider titleProvider) throws IOException, MpvException{
        return new MpvPlayer(
            embeddedServer,
            titleProvider,
            new PlayerOption("idle", "yes"),
            new PlayerOption("force-window", "immediate"),
            new PlayerOption("osc", "no"),
            new PlayerOption("osd-msg1", "skibidi toilet"),
            new PlayerOption("osd-align-x", "center"),
            new PlayerOption("osd-align-y", "center"),
            new PlayerOption("input-default-bindings", "yes"),
            new PlayerOption("input-builtin-bindings", "yes"),
            new PlayerOption("auto-window-resize", "no"),
            new PlayerOption("slang", "en"),
            new PlayerOption("sub-auto", "fuzzy"),
            new PlayerOption("ytdl-raw-options", "cookies-from-browser=firefox,sub-langs=\"en,en-US\",write-subs=,write-auto-subs="),
            new PlayerOption("ytdl-format", "bv*[height<=480]+ba/b[height<=480]/bv+ba/b")
            );
    }
}
