package ca.awoo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class YtdlThumbnailProvider implements ThumbnailProvider {
    private final String ytdl;
    private final String[] options;
    private File tempdir = new File(System.getProperty("java.io.tmpdir"));

    public YtdlThumbnailProvider(String ytdl, String... options) {
        this.ytdl = ytdl;
        this.options = options;
        workThread.start();
    }

    private static class Job{
        public final String source;
        public final CompletableFuture<File> future;
        public Job(String source, CompletableFuture<File> future) {
            this.source = source;
            this.future = future;
        }
        
    }

    private final BlockingQueue<Job> jobs = new LinkedBlockingQueue<>();
    private final ExecutorService waiter = Executors.newCachedThreadPool();
    private final Thread workThread = new Thread(() -> {
        while(true){
            try {
                Job job = jobs.take();
                if(job.source.contains("youtube.com/playlist")){
                    job.future.completeExceptionally(new Exception("Can't get thumbnail for playlist"));
                    continue;
                }
                File dest = getThumbnailLocationExt(job.source);
                if(dest.exists()){
                    job.future.complete(dest);
                }else{
                    try {
                        Process proc = download(job.source);
                        waiter.submit(() -> {
                            try {
                                proc.waitFor();
                            } catch (InterruptedException e) {
                                job.future.completeExceptionally(e);
                                return;
                            }
                            if(dest.exists()){
                                job.future.complete(dest);
                            }else{
                                job.future.completeExceptionally(new Exception("Could not download thumbnail"));
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        job.future.completeExceptionally(e);
                    }
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
        }
    });

    private File getThumbnailLocationExt(String source){
        String filename = Integer.toString(source.hashCode()) + ".png";
        return new File(tempdir, filename);
    }

    private File getThumbnailLocation(String source){
        String filename = Integer.toString(source.hashCode());
        return new File(tempdir, filename);
    }

    private Process download(String source) throws IOException{
        String[] command = new String[8 + options.length];
        command[0] = ytdl;
        command[1] = "--skip-download";
        command[2] = "--write-thumbnail";
        command[3] = "--convert-thumbnails";
        command[4] = "png";
        command[5] = "-o";
        command[6] = getThumbnailLocation(source).getAbsolutePath();
        for(int i = 0; i < options.length; i++){
            command[7+i] = options[i];
        }
        command[7+options.length] = source;
        ProcessBuilder pb = new ProcessBuilder(command).inheritIO();
        return pb.start();
    }

    @Override
    public CompletableFuture<File> getThumbnail(String source) {
        if(source.equals("$source")){
            //Browsers are always asking for a beautiful girl called $source
            return CompletableFuture.failedFuture(new Exception("Asking for $source again, are we?"));
        }
        File dest = getThumbnailLocationExt(source);
        if(dest.exists()){
            return CompletableFuture.completedFuture(dest);
        }
        return jobs.stream()
                   .filter((job) -> job.source.equals(source))
                   .findFirst()
                   .orElseGet(() -> {Job job = new Job(source, new CompletableFuture<>()); jobs.add(job); return job;}).future;
    }
    
}
