package ca.awoo;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YtdlVideo implements Video {
    private final ca.awoo.ytdl.Video metadata;
    private final YtdlSourceProvider provider;
    private final String source;
    private static final Logger LOG = LoggerFactory.getLogger(YtdlVideo.class);

    public YtdlVideo(ca.awoo.ytdl.Video metadata, YtdlSourceProvider provider) {
        this.metadata = metadata;
        this.provider = provider;
        this.source = metadata.getOriginalUrl() != null ? metadata.getOriginalUrl() : metadata.getWebpageUrl();
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public CompletableFuture<String> getTitle() {
        return CompletableFuture.completedFuture(metadata.getTitle());
    }

    @Override
    public CompletableFuture<BufferedImage> getThumbnail() {
        return provider.fetchThumbnail(source);
    }

    @Override
    public CompletableFuture<StreamInfo> getStream(int quality) {
        ca.awoo.ytdl.Format bestVideo = null;
        ca.awoo.ytdl.Format bestAudio = null;
        ca.awoo.ytdl.Format bestCombined = null;

        for (ca.awoo.ytdl.Format f : metadata.getFormats()) {
            boolean hasVideo = f.getVideoExt() != null && !f.getVideoExt().equals("none");
            boolean hasAudio = f.getAudioExt() != null && !f.getAudioExt().equals("none");

            if (hasVideo && hasAudio) {
                if (f.getHeight() <= quality) {
                    if (bestCombined == null || f.getHeight() >= bestCombined.getHeight()) {
                        bestCombined = f;
                    }
                }
            }
            if (hasVideo) {
                if (f.getHeight() <= quality) {
                    if (bestVideo == null || f.getHeight() >= bestVideo.getHeight()) {
                        bestVideo = f;
                    }
                }
            }
            if (hasAudio && !hasVideo) {
                // Pick the last audio format (usually highest quality)
                if (bestAudio == null || f.getQuality() > bestAudio.getQuality()) {
                    bestAudio = f;
                }
            }
        }

        if (bestCombined != null && (bestVideo == null || bestCombined.getHeight() >= bestVideo.getHeight())) {
            LOG.info("Using combined format: " + bestCombined.getFormat());
            return CompletableFuture.completedFuture(new StreamInfo(bestCombined.getUrl(), null));
        }

        if (bestVideo != null) {
            LOG.info("Using video format: " + bestVideo.getFormat() + " audio: " + bestAudio.getFormat());
            return CompletableFuture
                    .completedFuture(new StreamInfo(bestVideo.getUrl(), bestAudio != null ? bestAudio.getUrl() : null));
        }

        return CompletableFuture
                .completedFuture(new StreamInfo(metadata.getUrl() != null ? metadata.getUrl() : source, null));
    }
}
