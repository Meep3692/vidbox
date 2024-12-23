package ca.awoo;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class PlayerFactory {
    @Bean
    Player mpvPlayer(TitleProvider titleProvider){
        try {
            return new MpvPlayer(
                titleProvider,
                new PlayerOption("idle", "yes"),
                new PlayerOption("force-window", "immediate"),
                new PlayerOption("osc", "yes"),
                new PlayerOption("input-default-bindings", "yes"),
                new PlayerOption("input-builtin-bindings", "yes"),
                new PlayerOption("auto-window-resize", "no"),
                new PlayerOption("ytdl-raw-options", "cookies-from-browser=firefox"),
                new PlayerOption("ytdl-format", "bv*[height<=480]+ba/b[height<=480]/bv+ba/b")
                );
        } catch (MpvException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
