package ca.awoo.vidbox;

import java.net.URI;
import java.util.logging.Logger;

import jakarta.inject.Inject;
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
            playlist.enqueue(stream);
            log.info("Queued new url: " + url);
        }
    }
}
