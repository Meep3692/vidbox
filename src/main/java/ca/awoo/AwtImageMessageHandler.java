package ca.awoo;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.Headers;
import io.micronaut.core.type.MutableHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.http.body.MessageBodyHandler;
import io.micronaut.http.codec.CodecException;

public class AwtImageMessageHandler implements MessageBodyHandler<BufferedImage> {

    @Override
    public @Nullable BufferedImage read(@NonNull Argument<BufferedImage> type, @Nullable MediaType mediaType, @NonNull Headers httpHeaders,
            @NonNull InputStream inputStream) throws CodecException {
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new CodecException("Exception reading image from stream", e);
        }
    }

    @Override
    public void writeTo(@NonNull Argument<BufferedImage> type, @NonNull MediaType mediaType, BufferedImage image,
            @NonNull MutableHeaders outgoingHeaders, @NonNull OutputStream outputStream) throws CodecException {
        try {
            if(!ImageIO.write(image, mediaType.getSubtype(), outputStream)){
                throw new CodecException("Could not find writer for " + mediaType.getSubtype() + ", mediaType: " + mediaType);
            }
        } catch (IOException e) {
            throw new CodecException("Exception writing image to stream", e);
        }
    }
    
}
