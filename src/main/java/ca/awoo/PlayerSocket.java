package ca.awoo;

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

    @OnOpen
    public void onOpen(WebSocketSession session){
        broadcaster.broadcastSync(player.getState());
    }

    @OnMessage
    public void onMessage(String message, WebSocketSession session){

    }
    
}
