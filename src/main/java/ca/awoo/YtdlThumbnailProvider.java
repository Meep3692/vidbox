package ca.awoo;

import java.io.File;
import java.io.IOException;

public class YtdlThumbnailProvider implements ThumbnailProvider {
    private final String ytdl;
    private final String[] options;

    public YtdlThumbnailProvider(String ytdl, String... options) {
        this.ytdl = ytdl;
        this.options = options;
    }


    @Override
    public void getThumbnail(String source, File output) throws IOException {
        String[] command = new String[8 + options.length];
        command[0] = ytdl;
        command[1] = "--skip-download";
        command[2] = "--write-thumbnail";
        command[3] = "--convert-thumbnails";
        command[4] = "png";
        command[5] = "-o";
        command[6] = output.getAbsolutePath();
        command[6] = command[6].substring(0, command[6].length() - 4);
        for(int i = 0; i < options.length; i++){
            command[7+i] = options[i];
        }
        command[7+options.length] = source;
        ProcessBuilder pb = new ProcessBuilder(command).inheritIO();
        Process proc = pb.start();
        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            //TODO: not this
            throw new RuntimeException(e);
        }
    }
    
}
