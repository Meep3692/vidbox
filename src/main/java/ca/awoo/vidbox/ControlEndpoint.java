package ca.awoo.vidbox;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.logging.Logger;

import ca.awoo.vidbox.StreamSet.QualityStream;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/controls")
public class ControlEndpoint {

    @Inject
    private Renderer renderer;

    @Inject
    private Playlist playlist;

    Logger log = Logger.getLogger(getClass().getName());

    @OnMessage
    public void onMessage(Session session, String msg){
        if(msg.startsWith("enqueue")){
            String url = msg.substring("enqueue".length());
            log.info("Queueing new url: " + url);
            Stream stream = new Stream(URI.create(url), null, null);
            // renderer.playStream(stream);
            Video vid = new Video("An video", "It sure is", url, new StreamSet(new QualityStream(0, stream)));
            playlist.enqueue(vid);
            log.info("Queued new url: " + url);
        }else{
            switch(msg){
                default:
                    log.info("Unknown command from client: " + msg);
            }
        }
        Jsonb jsonb = JsonbBuilder.create();
        try {
            Writer writer = session.getBasicRemote().getSendWriter();
            jsonb.toJson(getState(), writer);
            writer.close();
        } catch (JsonbException | IOException e) {
            e.printStackTrace();
        }
    }

    private PlayerState getState(){
        return new PlayerState(playlist.getVideos(), playlist.getPosition(), renderer.isPaused(), renderer.getDuration(), renderer.getPos(), 480, false);
    }
}
