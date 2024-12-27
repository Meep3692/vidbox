package ca.awoo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import ca.awoo.sponsorblock.Segment;
import ca.awoo.sponsorblock.SponsorBlockClient;
import io.micronaut.http.annotation.Controller;
import io.micronaut.scheduling.annotation.Scheduled;

@Controller("sponsor")
public class SponsorBlockController {
    private String playingSource;
    private String playingId;

    private List<Segment> segments;
    private String segmentsId;

    private final Player player;
    private final SponsorBlockClient client;
    private final Logger log = LoggerFactory.getLogger(SponsorBlockController.class);

    public SponsorBlockController(Player player, SponsorBlockClient client){
        this.player = player;
        this.client = client;
    }

    @Scheduled(fixedDelay = "500ms")
    void updateTime(){
        VideoInfo info = player.nowPlaying();
        if(info == null) return;
        if(!info.getSource().equals(playingSource)){
            log.info("New video: " + info.getSource() + ", old video: " + playingSource);
            playingSource = info.getSource();
            playingId = null;
            if(playingSource.contains("youtube.com")){
                log.info("New YouTube video");
                try {
                    URI uri = new URI(playingSource);
                    String query = uri.getRawQuery();
                    log.info("yt video query: " + query);
                    String[] queryComps = query.split("&");
                    for(String queryComp : queryComps){
                        String[] pair = queryComp.split("=");
                        log.info("component: " + queryComp);
                        log.info("split " + Arrays.toString(pair));
                        if(pair[0].equals("v")){
                            log.info("Video id: " + pair[1]);
                            playingId = pair[1];
                        }
                    }
                } catch (URISyntaxException e) {
                    log.error(playingSource + " is not a valid URI somehow", e);
                    playingId = null;
                }
            }else if(playingSource.contains("youtu.be")){
                log.info("youtu.be");
                try {
                    URI uri = new URI(playingSource);
                    playingId = uri.getPath().substring(1);
                } catch (URISyntaxException e) {
                    log.error(playingSource + " is not a valid URI somehow", e);
                    playingId = null;
                }

            }
        }
        if(playingId != null){
            if(!playingId.equals(segmentsId)){
                log.info("Playing new youtube video: " + playingId + " from old video: " + segmentsId + ", getting segments");
                segments = client.fetchSegments(playingId);
                if(segments == null){
                    //No segments handsome
                    //Make this list exist
                    log.info("No segments");
                    segments = new ArrayList<>();
                    segmentsId = playingId;
                    return;
                }
                for(Segment segment : segments){
                    log.info(segment.toString());
                }
                segmentsId = playingId;
            }
            if(segmentsId != null && segmentsId.equals(playingId)){
                double position = player.playingPosition();
                for(Segment segment : segments){
                    float start = segment.segment()[0];
                    float end = segment.segment()[1];
                    String category = segment.category();
                    if(category.equals("sponsor") && start < position && position < end){
                        log.info("Skipping sponsor");
                        player.seek(end);
                        player.toast("Skipped sponsor segment");
                    }
                }
            }
        }
    }
}
