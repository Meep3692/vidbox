package ca.awoo.vidbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class PlaylistTest {

    private Video vid(String name){
        return new Video(name, name, name, null);
    }

    @Test
    public void addTest(){
        Playlist playlist = new Playlist();
        Video a = vid("a");
        playlist.enqueue(a);
        assertEquals(1, playlist.getVideos().size());
        assertEquals(a, playlist.getVideos().get(0));
        assertEquals(a, playlist.getVideo().get());
    }

    @Test
    public void nextTest(){
        Playlist playlist = new Playlist();
        Video a = vid("a");
        Video b = vid("b");
        Video c = vid("c");
        playlist.enqueue(a);
        playlist.enqueue(b);
        playlist.enqueue(c);
        playlist.next();
        assertEquals(b, playlist.getVideo().get());
    }

    @Test
    public void nextTestEnd(){
        Playlist playlist = new Playlist();
        Video a = vid("a");
        Video b = vid("b");
        Video c = vid("c");
        playlist.enqueue(a);
        playlist.enqueue(b);
        playlist.enqueue(c);
        playlist.next();
        playlist.next();
        Optional<Video> playing = playlist.next();
        assertTrue(playing.isEmpty());
        assertTrue(playlist.getVideo().isEmpty());
    }

    @Test
    public void nextTestEmpty(){
        Playlist playlist = new Playlist();
        Optional<Video> playing = playlist.next();
        assertTrue(playing.isEmpty());
        assertTrue(playlist.getVideo().isEmpty());
    }

    @Test
    public void skipToTest(){
        Playlist playlist = new Playlist();
        Video a = vid("a");
        Video b = vid("b");
        Video c = vid("c");
        playlist.enqueue(a);
        playlist.enqueue(b);
        playlist.enqueue(c);
        playlist.skipTo(1);
        assertEquals(b, playlist.getVideo().get());
    }

    @Test
    public void endTest(){
        Playlist playlist = new Playlist();
        Video a = vid("a");
        Video b = vid("b");
        Video c = vid("c");
        playlist.enqueue(a);
        playlist.enqueue(b);
        playlist.enqueue(c);
        assertEquals(c, playlist.end().get());
    }

    @Test
    public void prevTest(){
        Playlist playlist = new Playlist();
        Video a = vid("a");
        Video b = vid("b");
        Video c = vid("c");
        playlist.enqueue(a);
        playlist.enqueue(b);
        playlist.enqueue(c);
        playlist.next();
        playlist.prev();
        assertEquals(a, playlist.getVideo().get());
    }

    @Test
    public void removePast(){
        Playlist playlist = new Playlist();
        Video a = vid("a");
        Video b = vid("b");
        Video c = vid("c");
        playlist.enqueue(a);
        playlist.enqueue(b);
        playlist.enqueue(c);
        playlist.next();
        playlist.remove(0);
        assertEquals(2, playlist.getVideos().size());
        assertEquals(b, playlist.getVideos().get(0));
        assertEquals(c, playlist.getVideos().get(1));
        assertEquals(b, playlist.getVideo().get());
    }

    @Test
    public void removePresent(){
        Playlist playlist = new Playlist();
        Video a = vid("a");
        Video b = vid("b");
        Video c = vid("c");
        playlist.enqueue(a);
        playlist.enqueue(b);
        playlist.enqueue(c);
        playlist.next();
        playlist.remove(1);
        assertEquals(2, playlist.getVideos().size());
        assertEquals(a, playlist.getVideos().get(0));
        assertEquals(c, playlist.getVideos().get(1));
        assertEquals(c, playlist.getVideo().get());
    }

    @Test
    public void removeFuture(){
        Playlist playlist = new Playlist();
        Video a = vid("a");
        Video b = vid("b");
        Video c = vid("c");
        playlist.enqueue(a);
        playlist.enqueue(b);
        playlist.enqueue(c);
        playlist.next();
        playlist.remove(2);
        assertEquals(2, playlist.getVideos().size());
        assertEquals(a, playlist.getVideos().get(0));
        assertEquals(b, playlist.getVideos().get(1));
        assertEquals(b, playlist.getVideo().get());
    }
}
