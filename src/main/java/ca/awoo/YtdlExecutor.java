package ca.awoo;

import java.util.List;

public interface YtdlExecutor {
    ProcessBuilder buildProcess(List<String> args);
}
