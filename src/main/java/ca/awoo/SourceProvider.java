package ca.awoo;

import java.util.List;

/**
 * Resolves one or more concrete video sources from a user-supplied input.
 * Implementations can expand playlists, normalize URLs, or delegate to
 * different tools (yt-dlp, streamlink, etc.).
 */
public interface SourceProvider {
    /**
     * Given a user input string, return one or more concrete source URLs
     * to enqueue. For non-playlist URLs this will typically be a singleton
     * list; for playlist URLs it may return multiple entries.
     */
    List<String> expandSources(String source);
}

