package ca.awoo;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;

public class Application {

    public static void main(String[] args) {
        ApplicationContext context = Micronaut.run(Application.class, args);
        Player player = context.getBean(Player.class);
        player.enqueue("https://www.youtube.com/watch?v=wTuhDxQk5fI");
        context.close();
    }
}