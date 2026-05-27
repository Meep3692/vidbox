package ca.awoo.sponsorblock;

public record Segment(float[] segment,
                      String uuid,
                      String category,
                      float videoDuration,
                      String actionType,
                      int locked,
                      int votes,
                      String description) {
    
}
