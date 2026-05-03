package ca.awoo;

import java.util.ArrayList;
import java.util.List;

import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DefaultYtdlExecutor implements YtdlExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultYtdlExecutor.class);

    @Property(name = "vidbox.ytdl.path", defaultValue = "yt-dlp")
    String ytdlPath;

    @Property(name = "vidbox.ytdl.options")
    List<String> defaultOptions;

    @Override
    public ProcessBuilder buildProcess(List<String> args) {
        List<String> command = new ArrayList<>();
        command.add(ytdlPath);
        if (defaultOptions != null) {
            for(String opt : defaultOptions){
                if(opt != null && !opt.trim().isEmpty()){
                    command.add(opt);
                }
            }
        }
        command.addAll(args);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Building yt-dlp command: {}", String.join(" ", command));
        }
        
        return new ProcessBuilder(command);
    }
}
