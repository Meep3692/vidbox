package ca.awoo.sponsorblock;

import java.util.List;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;

@Client(id = "sponsorblock")
public interface SponsorBlockClient {

    @Get("/api/skipSegments")
    List<Segment> fetchSegments(@QueryValue String videoID);
}
