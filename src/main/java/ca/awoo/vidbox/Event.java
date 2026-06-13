package ca.awoo.vidbox;

import java.util.WeakHashMap;
import java.util.function.Consumer;

public class Event<T> {
    //Who needs a WeakSet when you can have a WeakHashMap that stores nothin
    private WeakHashMap<Consumer<T>, Object> listeners = new WeakHashMap<>();

    public void add(Consumer<T> listener){
        listeners.put(listener, null);
    }

    public void fire(T arg){
        for(Consumer<T> listener : listeners.keySet()){
            listener.accept(arg);
        }
    }
}
