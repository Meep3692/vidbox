package ca.awoo;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class YtdlTitleProvider implements TitleProvider {
    private final String ytdl;
    private final String[] options;

    public YtdlTitleProvider(String ytdl, String... options) {
        this.ytdl = ytdl;
        this.options = options;
        workThread.start();
    }

    private static class Job{
        public final String source;
        public final CompletableFuture<String> future;
        public Job(String source, CompletableFuture<String> future) {
            this.source = source;
            this.future = future;
        }
        
    }

    private final Map<String, String> cache = Collections.synchronizedMap(new HashMap<>());
    private final BlockingQueue<Job> jobs = new LinkedBlockingQueue<>();
    private final ExecutorService waiter = Executors.newCachedThreadPool();
    private final Thread workThread = new Thread(() -> {
        while(true){
            try {
                Job job = jobs.take();
                try {
                    Process proc = download(job.source);
                    //waiter.submit(() -> {
                        try {
                            String title = proc.inputReader(Charset.forName("utf-8")).readLine();
                            cache.put(job.source, title);
                            System.out.println("Got title for " + job.source + ": " + title);
                            job.future.complete(title);
                        } catch (IOException e) {
                            job.future.completeExceptionally(e);
                            return;
                        }
                    //});
                } catch (IOException e) {
                    e.printStackTrace();
                    job.future.completeExceptionally(e);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
        }
    });

    private Process download(String source) throws IOException{
        String[] command = new String[4 + options.length];
        command[0] = ytdl;
        command[1] = "--print";
        command[2] = "title";
        for(int i = 0; i < options.length; i++){
            command[3+i] = options[i];
        }
        command[3+options.length] = source;
        StringBuilder sb = new StringBuilder();
        for(String c : command){
            sb.append(c);
            sb.append(" ");
        }
        System.out.println(sb.toString());
        ProcessBuilder pb = new ProcessBuilder(command).redirectError(Redirect.INHERIT);
        return pb.start();
    }

    @Override
    public CompletableFuture<String> getTitle(String source) {
        if(source.equals("$source")){
            //Browsers are always asking for a beautiful girl called $source
            return CompletableFuture.failedFuture(new Exception("Asking for $source again, are we?"));
        }
        if(cache.containsKey(source)){
            return CompletableFuture.completedFuture(cache.get(source));
        }
        return jobs.stream()
                   .filter((job) -> job.source.equals(source))
                   .findFirst()
                   .orElseGet(() -> {Job job = new Job(source, new CompletableFuture<>()); jobs.add(job); return job;}).future;
    }
    
}
