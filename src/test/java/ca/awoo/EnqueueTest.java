package ca.awoo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class EnqueueTest {
    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    Player player;

    @Test
    public void enqueueTest(){
        HttpRequest<?> request = HttpRequest.POST("/player/enqueue", "{\"source\": \"https://www.youtube.com/watch?v=wTuhDxQk5fI\"}").accept(MediaType.TEXT_PLAIN);
        String body = client.toBlocking().retrieve(request);

        assertNotNull(body);
        assertEquals("https://www.youtube.com/watch?v=wTuhDxQk5fI", body);
        assertTrue(player.getPlaylist().contains(body));
    }
}
