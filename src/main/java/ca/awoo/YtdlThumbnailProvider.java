package ca.awoo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class YtdlThumbnailProvider implements ThumbnailProvider {
    private final String ytdl;
    private final String[] options;
    private File tempdir = new File(System.getProperty("java.io.tmpdir"));

    private static final String[] defaultOpts = new String[]
        {"--skip-download", "--write-thumbnail", "--convert-thumbnails", "png", "--no-playlist", "--break-on-reject", "--match-filter", "!playlist"};

    public YtdlThumbnailProvider(String ytdl, String... options) {
        this.ytdl = ytdl;
        this.options = options;
        workThread.start();
    }

    private static class Job{
        public final String source;
        public final CompletableFuture<InputStream> future;
        public Job(String source, CompletableFuture<InputStream> future) {
            this.source = source;
            this.future = future;
        }
        
    }

    private final Set<String> badSources = new HashSet<>();

    private final BlockingQueue<Job> jobs = new LinkedBlockingQueue<>();
    private final ExecutorService waiter = Executors.newCachedThreadPool();
    private final Thread workThread = new Thread(() -> {
        while(true){
            try {
                Job job = jobs.take();
                if(job.source.contains("youtube.com/playlist")){
                    badSources.add(job.source);
                    job.future.complete(getClass().getResourceAsStream("/video.png"));
                    continue;
                }
                File dest = getThumbnailLocationExt(job.source);
                if(dest.exists()){
                    job.future.complete(new FileInputStream(dest));
                }else{
                    try {
                        Process proc = download(job.source);
                        waiter.submit(() -> {
                            try {
                                proc.waitFor();
                            } catch (InterruptedException e) {
                                badSources.add(job.source);
                                job.future.complete(getClass().getResourceAsStream("/video.png"));
                                return;
                            }
                            if(dest.exists()){
                                try {
                                    job.future.complete(new FileInputStream(dest));
                                } catch (FileNotFoundException e) {
                                    //Literally impossible
                                    badSources.add(job.source);
                                    job.future.complete(getClass().getResourceAsStream("/video.png"));
                                }
                            }else{
                                badSources.add(job.source);
                                job.future.complete(getClass().getResourceAsStream("/video.png"));
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        badSources.add(job.source);
                        job.future.complete(getClass().getResourceAsStream("/video.png"));
                    }
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
        String[] command = new String[1 + defaultOpts.length + 2 + options.length + 1];
        command[0] = ytdl;
        for(int i = 0; i < defaultOpts.length; i++){
            command[1+i] = defaultOpts[i];
        }
        command[1 + defaultOpts.length + 0] = "-o";
        command[1 + defaultOpts.length + 1] = getThumbnailLocation(source).getAbsolutePath();
        for(int i = 0; i < options.length; i++){
            command[1+defaultOpts.length+2+i] = options[i];
        }
        command[1 + defaultOpts.length + 2 + options.length] = source;
        ProcessBuilder pb = new ProcessBuilder(command).inheritIO();
        return pb.start();
    }

    

    @Override
    public CompletableFuture<InputStream> getThumbnail(String source) {
        if(source.equals("$source")){
            //Browsers are always asking for a beautiful girl called $source
            return CompletableFuture.completedFuture(getClass().getResourceAsStream("/video.png"));
        }
        if(badSources.contains(source)){
            return CompletableFuture.completedFuture(getClass().getResourceAsStream("/video.png"));
        }
        File dest = getThumbnailLocationExt(source);
        if(dest.exists()){
            try {
                return CompletableFuture.completedFuture(new FileInputStream(dest));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return jobs.stream()
                   .filter((job) -> job.source.equals(source))
                   .findFirst()
                   .orElseGet(() -> {Job job = new Job(source, new CompletableFuture<>()); jobs.add(job); return job;}).future;
    }
    
}
