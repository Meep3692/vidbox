package ca.awoo;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import io.micronaut.json.JsonMapper;
import javax.imageio.ImageIO;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SourceProvider implementation backed by yt-dlp.
 *
 * - Non-playlist URLs are returned as a single-element list.
 * - YouTube playlist URLs (/playlist?list=...) are expanded into one URL
 * per video using yt-dlp's flat playlist output.
 */
public class YtdlSourceProvider implements SourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(YtdlSourceProvider.class);

    private final YtdlExecutor ytdlExecutor;
    private final JsonMapper jsonMapper;

    // Metadata fetching logic
    private final Map<String, String> titleCache = Collections.synchronizedMap(new HashMap<>());
    private final Set<String> badThumbnailSources = new HashSet<>();
    private final File tempdir = new File(System.getProperty("java.io.tmpdir"));
    private BufferedImage noThumbnail = null;

    private static final String[] thumbnailOpts = new String[] { "--skip-download", "--write-thumbnail",
            "--convert-thumbnails", "png", "--no-playlist", "--break-on-reject", "--match-filter", "!playlist" };

    private static class ThumbnailJob {
        public final String source;
        public final CompletableFuture<BufferedImage> future;

        public ThumbnailJob(String source, CompletableFuture<BufferedImage> future) {
            this.source = source;
            this.future = future;
        }
    }

    private final BlockingQueue<ThumbnailJob> thumbnailJobs = new LinkedBlockingQueue<>();
    private final ExecutorService thumbnailWaiter = Executors.newCachedThreadPool();

    private final Thread thumbnailWorkThread;

    public YtdlSourceProvider(YtdlExecutor ytdlExecutor, JsonMapper jsonMapper) {
        this.ytdlExecutor = ytdlExecutor;
        this.jsonMapper = jsonMapper;
        try {
            noThumbnail = ImageIO.read(getClass().getResource("/video.png"));
        } catch (IOException e) {
            LOG.error("Failed to load default thumbnail", e);
        }
        this.thumbnailWorkThread = new Thread(this::thumbnailWorker);
        thumbnailWorkThread.start();
    }



    private void thumbnailWorker() {
        while (true) {
            try {
                ThumbnailJob job = thumbnailJobs.take();
                if (job.source.contains("youtube.com/playlist")) {
                    badThumbnailSources.add(job.source);
                    job.future.complete(noThumbnail);
                    continue;
                }
                File dest = getThumbnailLocationExt(job.source);
                if (dest.exists()) {
                    try {
                        job.future.complete(ImageIO.read(dest));
                    } catch (IOException e) {
                        job.future.complete(noThumbnail);
                    }
                } else {
                    try {
                        List<String> command = new ArrayList<>();
                        command.addAll(java.util.Arrays.asList(thumbnailOpts));
                        command.add("-o");
                        command.add(getThumbnailLocation(job.source).getAbsolutePath());
                        command.add(job.source);
                        ProcessBuilder pb = ytdlExecutor.buildProcess(command);
                        pb.inheritIO();
                        Process proc = pb.start();
                        thumbnailWaiter.submit(() -> {
                            try {
                                proc.waitFor();
                                if (dest.exists()) {
                                    job.future.complete(ImageIO.read(dest));
                                } else {
                                    badThumbnailSources.add(job.source);
                                    job.future.complete(noThumbnail);
                                }
                            } catch (Exception e) {
                                badThumbnailSources.add(job.source);
                                job.future.complete(noThumbnail);
                            }
                        });
                    } catch (IOException e) {
                        LOG.error("Failed to fetch thumbnail for " + job.source, e);
                        badThumbnailSources.add(job.source);
                        job.future.complete(noThumbnail);
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public CompletableFuture<BufferedImage> fetchThumbnail(String source) {
        if (source.equals("$source")) {
            return CompletableFuture.completedFuture(noThumbnail);
        }
        if (badThumbnailSources.contains(source)) {
            return CompletableFuture.completedFuture(noThumbnail);
        }
        File dest = getThumbnailLocationExt(source);
        if (dest.exists()) {
            try {
                return CompletableFuture.completedFuture(ImageIO.read(dest));
            } catch (IOException e) {
                // ignore
            }
        }
        return thumbnailJobs.stream()
                .filter(j -> j.source.equals(source))
                .findFirst()
                .orElseGet(() -> {
                    ThumbnailJob job = new ThumbnailJob(source, new CompletableFuture<>());
                    thumbnailJobs.add(job);
                    return job;
                }).future;
    }

    private File getThumbnailLocationExt(String source) {
        String filename = Integer.toString(source.hashCode()) + ".png";
        return new File(tempdir, filename);
    }

    private File getThumbnailLocation(String source) {
        String filename = Integer.toString(source.hashCode());
        return new File(tempdir, filename);
    }

    @Override
    public boolean supports(String source) {
        return true;
    }

    @Override
    public List<Video> expandSources(String source) {
        List<Video> videos = new ArrayList<>();
        try {
            List<String> args = new ArrayList<>();
            args.add("-j");
            if (isYoutubePlaylist(source)) {
                args.add("--yes-playlist");
            } else {
                args.add("--no-playlist");
            }
            args.add(source);
            
            ProcessBuilder pb = ytdlExecutor.buildProcess(args);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process proc = pb.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    try {
                        ca.awoo.ytdl.Video metadata = jsonMapper.readValue(line, ca.awoo.ytdl.Video.class);
                        videos.add(new YtdlVideo(metadata, this));
                    } catch (Exception e) {
                        LOG.error("Failed to parse metadata for line: " + line, e);
                    }
                }
            }
            proc.waitFor();
        } catch (Exception e) {
            LOG.error("Failed to expand sources for " + source, e);
        }
        return videos;
    }

    private boolean isYoutubePlaylist(String source) {
        try {
            URI uri = new URI(source);
            String host = uri.getHost();
            String path = uri.getPath();
            if (host == null || path == null) {
                return false;
            }
            host = host.toLowerCase(Locale.ROOT);
            return (host.contains("youtube.com") || host.contains("youtu.be")) && "/playlist".equals(path);
        } catch (URISyntaxException e) {
            return false;
        }
    }


}
