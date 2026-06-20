package ca.awoo.vidbox;

import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/controls", encoders = {PlayerStateEncoder.class})
public class ControlEndpoint {

    @Inject
    private Player player;

    @Inject
    private ControllerNotifier notifier;

    Logger log = Logger.getLogger(getClass().getName());

    public ControlEndpoint() {
        
    }

    @OnOpen
    public void open(Session session, EndpointConfig conf){
        log.info("Controller connected " + session);
        notifier.addSession(session);
    }

    @OnMessage
    public void onMessage(Session session, String msg){
        log.info("Message from controller: " + msg);
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
        notifier.broadcastState();
    }
}
