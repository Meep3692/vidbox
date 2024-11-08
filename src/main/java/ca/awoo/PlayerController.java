package ca.awoo;

import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.View;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;

@Controller("/player")
public class PlayerController {
    private Player player;

    public PlayerController(Player player){
        this.player = player;
    }

    @Post("enqueue")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.ALL)
    public String enqueue(String source){
        player.enqueue(source);
        return source;
    }

    @Post("enqueue")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.ALL)
    public HttpResponse<String> enqueueWeb(String source) throws URISyntaxException{
        player.enqueue(source);
        URI location = new URI("/player");
        return HttpResponse.redirect(location);
    }

    @Get("skipto/{index}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.ALL)
    public HttpResponse<Void> skipTo(@PathVariable Integer index) throws URISyntaxException{
        player.playIndex(index);
        URI location = new URI("/player");
        return HttpResponse.redirect(location);
    }

    @Produces(MediaType.TEXT_HTML)
    @View("player.html")
    @Get
    public Map<String, List<VideoInfo>> index(){
        return Collections.singletonMap("playlist", player.getPlaylist());
    }
}
