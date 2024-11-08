package ca.awoo;

import java.io.File;
import java.io.IOException;

public interface ThumbnailProvider {
    public void getThumbnail(String source, File output) throws IOException;
}
