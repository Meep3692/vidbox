package ca.awoo.vidbox;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.websocket.Session;

@Singleton
public class ControllerNotifier {
    @Inject
    private Player player;

    Logger log = Logger.getLogger(getClass().getName());

    // Fancy thread-safe set lets me commit crimes
    private Set<Session> sessions = new CopyOnWriteArraySet<>();

    public void addSession(Session session){
        log.info("Adding session to notifier");
        sessions.add(session);
    }

    @Schedule(second = "*", minute = "*", hour = "*")
    public void broadcastState(){
        synchronized(sessions){
            log.finer("Broadcasting state");
            for(Session session : sessions){
                log.finer("Considering session " + session);
                if(session.isOpen()){
                    session.getAsyncRemote().sendObject(player.getState(true));
                }else{
                    log.finer("Closed connection, removing from list");
                    //Aforementioned crime I can commit
                    sessions.remove(session);
                }
            }
        }
    }
}
