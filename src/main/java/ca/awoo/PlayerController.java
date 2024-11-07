package ca.awoo;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.MediaType;

@Controller("/player")
public class PlayerController {
    private Player player;

    public PlayerController(Player player){
        this.player = player;
    }

    @Post(uri="enqueue")
    @Produces(MediaType.TEXT_PLAIN)
    public String enqueue(String source){
        player.enqueue(source);
        return source;
    }
}
