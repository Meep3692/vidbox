package ca.awoo;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class PlayerFactory {
    @Bean
    Player mpvPlayer(){
        try {
            return new MpvPlayer(
                new PlayerOption("idle", "yes"),
                new PlayerOption("force-window", "immediate"),
                new PlayerOption("osc", "yes"),
                new PlayerOption("auto-window-resize", "no"),
                new PlayerOption("ytdl-raw-options", "cookies-from-browser=firefox")
                );
        } catch (MpvException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
