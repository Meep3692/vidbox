package ca.awoo.sponsorblock;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Segment(float[] segment,
                      String uuid,
                      String category,
                      float videoDuration,
                      String actionType,
                      int locked,
                      int votes,
                      String description) {
    
}
