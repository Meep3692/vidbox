package ca.awoo.vidbox;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/controls")
public class ControlEndpoint {

    @Inject
    private Player player;

    Logger log = Logger.getLogger(getClass().getName());

    List<Session> sessions = new ArrayList<>();

    public ControlEndpoint() {
        
    }

    @OnOpen
    public void open(Session session, EndpointConfig conf){
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(Session session, String msg){
        if(msg.startsWith("enqueue")){
            String url = msg.substring("enqueue".length());
            log.info("Queueing new url: " + url);
            player.enqueue(url);
        }else{
            switch(msg){
                case "pause":
                    player.pause();
                    break;
                case "play":
                    player.resume();
                    break;
                default:
                    log.info("Unknown command from client: " + msg);
            }
        }
        Jsonb jsonb = JsonbBuilder.create();
        try {
            Writer writer = session.getBasicRemote().getSendWriter();
            jsonb.toJson(player.getState(true), writer);
            writer.close();
        } catch (JsonbException | IOException e) {
            e.printStackTrace();
        }
    }
}
