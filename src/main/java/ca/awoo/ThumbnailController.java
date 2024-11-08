package ca.awoo;

import java.io.File;
import java.io.IOException;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller("/thumbnail.png")
public class ThumbnailController {
    private File tempdir = new File(System.getProperty("java.io.tmpdir"));
    private ThumbnailProvider provider;

    public ThumbnailController(ThumbnailProvider provider){
        this.provider = provider;
    }

    @Get
    @Produces(MediaType.IMAGE_PNG)
    public File getImage(String source) throws IOException{
        String filename = Integer.toString(source.hashCode()) + ".png";
        File file = new File(tempdir, filename);
        if(file.exists()){
            return file;
        }else{
            provider.getThumbnail(source, file);
            return file;
        }
    }
}
