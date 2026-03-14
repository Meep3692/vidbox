package ca.awoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SourceProvider implementation backed by yt-dlp.
 *
 * - Non-playlist URLs are returned as a single-element list.
 * - YouTube playlist URLs (/playlist?list=...) are expanded into one URL
 *   per video using yt-dlp's flat playlist output.
 */
public class YtdlSourceProvider implements SourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(YtdlSourceProvider.class);

    private final String ytdl;
    private final String[] options;

    public YtdlSourceProvider(String ytdl, String... options){
        this.ytdl = ytdl;
        this.options = options;
    }

    @Override
    public List<String> expandSources(String source){
        if(isYoutubePlaylist(source)){
            List<String> expanded = expandYoutubePlaylist(source);
            if(!expanded.isEmpty()){
                return expanded;
            }
            LOG.warn("yt-dlp playlist expansion returned no entries for {}, falling back to single source", source);
        }
        List<String> single = new ArrayList<>(1);
        single.add(source);
        return single;
    }

    private boolean isYoutubePlaylist(String source){
        try{
            URI uri = new URI(source);
            String host = uri.getHost();
            String path = uri.getPath();
            if(host == null || path == null){
                return false;
            }
            host = host.toLowerCase(Locale.ROOT);
            // Treat /playlist URLs on YouTube domains as playlists.
            return (host.contains("youtube.com") || host.contains("youtu.be")) && "/playlist".equals(path);
        }catch(URISyntaxException e){
            LOG.debug("Failed to parse URL for playlist detection: {}", source, e);
            return false;
        }
    }

    private List<String> expandYoutubePlaylist(String source){
        List<String> videos = new ArrayList<>();
        Process proc = null;
        try{
            List<String> command = new ArrayList<>();
            command.add(ytdl);
            command.add("--flat-playlist");
            command.add("--yes-playlist");
            command.add("--print");
            command.add("webpage_url");
            for(String opt : options){
                command.add(opt);
            }
            command.add(source);
            ProcessBuilder pb = new ProcessBuilder(command).redirectError(ProcessBuilder.Redirect.INHERIT);
            proc = pb.start();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))){
                String line;
                while((line = reader.readLine()) != null){
                    line = line.trim();
                    if(!line.isEmpty()){
                        videos.add(line);
                    }
                }
            }
            int exit = proc.waitFor();
            if(exit != 0){
                LOG.warn("yt-dlp playlist expansion exited with code {} for {}", exit, source);
            }
        }catch(IOException e){
            LOG.error("Failed to expand playlist via yt-dlp for {}", source, e);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            LOG.error("Interrupted while expanding playlist via yt-dlp for {}", source, e);
        }
        return videos;
    }
}

