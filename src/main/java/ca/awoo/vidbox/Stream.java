package ca.awoo.vidbox;

import java.net.URI;

public record Stream(URI video, URI audio, URI subtitles) {
    
}
