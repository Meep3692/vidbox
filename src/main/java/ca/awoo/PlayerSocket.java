package ca.awoo;

import io.micronaut.scheduling.annotation.Scheduled;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;

@ServerWebSocket("/clicker")
public class PlayerSocket {
    private final Player player;
    private final WebSocketBroadcaster broadcaster;

    public PlayerSocket(Player player, WebSocketBroadcaster broadcaster) {
        this.player = player;
        this.broadcaster = broadcaster;
        player.onChange((state) -> broadcaster.broadcastSync(state));
    }

    @Scheduled(fixedDelay = "500ms")
    void updateTime(){
        broadcaster.broadcastSync(player.getStateWithoutPlaylist());
    }

    @OnOpen
    public void onOpen(WebSocketSession session){
        broadcaster.broadcastSync(player.getState());
    }

    @OnMessage
    public void onMessage(String message, WebSocketSession session){
        switch(message){
            case "prev":
                player.prev();
                break;
            case "next":
                player.next();
                break;
            case "play":
                player.play();
                break;
            case "pause":
                player.pause();
                break;
            case "skipBack":
                player.seekRelative(-5);
                break;
            case "skipForward":
                player.seekRelative(5);
                break;
            case "stop":
                player.playIndex(-1);
                break;
            case "subsOn":
                player.subtitles(true);
                break;
            case "subsOff":
                player.subtitles(false);
                break;
        }
        if(message.startsWith("seek")){
            double seekPos = Double.parseDouble(message.substring(4));
            player.seek(seekPos);
        }else if(message.startsWith("q")){
            int quality = Integer.parseInt(message.substring(1));
            player.setQuality(quality);
        }else if(message.startsWith("remove")){
            int index = Integer.parseInt(message.substring(6));
            player.remove(index);
        }
    }
    
}
