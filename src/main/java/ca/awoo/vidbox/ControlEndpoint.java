package ca.awoo.vidbox;

import java.io.IOException;

import ca.awoo.vidbox.Renderer.Whence;
import jakarta.inject.Inject;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/controls")
public class ControlEndpoint {

    @Inject
    private Renderer renderer;

    @OnMessage
    public void onMessage(Session session, String msg){
        try {
            session.getBasicRemote().sendText(msg);
            renderer.seek(0, Whence.BEGGINING);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
