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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;

@Controller("/player")
public class PlayerController {
    private Player player;

    private static final Logger LOG = LoggerFactory.getLogger(PlayerController.class);

    public PlayerController(Player player) {
        this.player = player;
    }

    @Post("enqueue")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.ALL)
    public String enqueue(String source) {
        LOG.trace("Enqueuing: " + source);
        player.enqueue(source);
        return source;
    }

    @Post("enqueue")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.ALL)
    public HttpResponse<String> enqueueWeb(String source) throws URISyntaxException {
        LOG.trace("Enqueuing: " + source);
        player.enqueue(source);
        LOG.trace("Redirecting to /player");
        URI location = new URI("/player");
        return HttpResponse.redirect(location);
    }

    @Get("skipto/{index}")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.ALL)
    public HttpResponse<Void> skipTo(@PathVariable Integer index) throws URISyntaxException {
        player.playIndex(index);
        URI location = new URI("/player");
        return HttpResponse.temporaryRedirect(location);
    }

    @Produces(MediaType.TEXT_HTML + ";charset=utf-8")
    @View("player.html")
    @Get
    public HttpResponse<PlayerState> index() {
        return HttpResponse.ok(player.getState()).header("Cache-Control", "no-store");
    }
}
