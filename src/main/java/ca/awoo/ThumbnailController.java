package ca.awoo;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller("/thumbnail.png")
public class ThumbnailController {
    private SourceProvider provider;

    public ThumbnailController(SourceProvider provider){
        this.provider = provider;
    }

    @Get
    @Produces(MediaType.IMAGE_PNG)
    public HttpResponse<CompletableFuture<BufferedImage>> getImage(String source) {
        List<Video> videos = provider.expandSources(source);
        if (videos.isEmpty()) {
            return HttpResponse.notFound();
        }
        return HttpResponse.ok(videos.get(0).getThumbnail()).header("Cache-Control", "public, max-age=604800, immutable");
    }
}
