package ca.awoo;

import java.awt.image.BufferedImage;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.http.body.MessageBodyHandler;

@Factory
public class MessageHandlerFactory {
    @Bean
    public MessageBodyHandler<BufferedImage> awtImageHandler(){
        return new AwtImageMessageHandler();
    }
}
