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
        }else if(msg.startsWith("skipTo")){
            int position = Integer.parseInt(msg.substring("skipTo".length()));
            log.info("Skipping to track " + position);
            player.skipTo(position);
        }else if(msg.startsWith("seekTo")){
            float position = Float.parseFloat(msg.substring("seekTo".length()));
            log.info("Seeking to position " + position);
            player.seek(position, Whence.BEGGINING);
        }else if(msg.startsWith("q")){
            int quality = Integer.parseInt(msg.substring("q".length()));
            log.info("Setting quality " + quality);
            player.setQuality(quality);
        }else if(msg.startsWith("remove")){
            int position = Integer.parseInt(msg.substring("remove".length()));
            log.info("Removing " + position);
            player.remove(position);
        }else{
            switch(msg){
                case "pause":
                    player.pause();
                    break;
                case "play":
                    player.resume();
                    break;
                case "prev":
                    player.prev();
                    break;
                case "next":
                    player.next();
                    break;
                case "stop":
                    player.stop();
                    break;
                case "seekBack":
                    player.seek(-5, Whence.CURRENT);
                    break;
                case "seekForward":
                    player.seek(5, Whence.CURRENT);
                    break;
                case "subsOn":
                    player.setSubtitles(true);
                    break;
                case "subsOff":
                    player.setSubtitles(false);
                    break;
                default:
                    log.info("Unknown command from client: " + msg);
            }
        }
        notifier.broadcastState();
    }
}
