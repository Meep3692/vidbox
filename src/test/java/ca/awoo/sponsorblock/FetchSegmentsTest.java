package ca.awoo.sponsorblock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
public class FetchSegmentsTest {
    @Inject
    //@Client("https://sponsor.ajay.app")
    SponsorBlockClient client;

    @Test
    public void getSegments(){
        List<Segment> segments = client.fetchSegments("pU9sHwNKc2c");
        assertNotNull(segments);
        assertEquals(2, segments.size());
        assertEquals(26.631,  segments.get(0).segment()[0], 0.001);
        assertEquals(47.604,  segments.get(0).segment()[1], 0.001);
        assertEquals(439.8,   segments.get(1).segment()[0], 0.001);
        assertEquals(505.819, segments.get(1).segment()[1], 0.001);
    }
}
