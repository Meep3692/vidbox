package ca.awoo;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller("/thumbnail.png")
public class ThumbnailController {
    private ThumbnailProvider provider;

    public ThumbnailController(ThumbnailProvider provider){
        this.provider = provider;
    }

    @Get
    @Produces(MediaType.IMAGE_PNG)
    public CompletableFuture<File> getImage(String source) {
        return provider.getThumbnail(source);
    }
}
