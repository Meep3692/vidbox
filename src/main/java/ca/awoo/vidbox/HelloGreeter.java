package ca.awoo.vidbox;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

@Default
@ApplicationScoped
public class HelloGreeter implements Greeter{

    @Override
    public String greet(String name) {
        return "Hello, " + name;
    }
    
}
