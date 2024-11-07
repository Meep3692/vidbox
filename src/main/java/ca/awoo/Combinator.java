package ca.awoo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Create variations of a string sequence
 */
public class Combinator {
    List<List<String>> components;

    /**
     * Create a new Combinator with no components
     */
    public Combinator(){
        components = new LinkedList<>();
    }

    /**
     * Add an optional component to the combinator
     * @param s The string to optionally add to the final string
     * @return this
     */
    public Combinator optional(String s){
        components.add(List.of(s, ""));
        return this;
    }

    /**
     * Add an or component to the combinator
     * @param s The strings the add to the string
     * @return this
     */
    public Combinator or(String... s){
        components.add(List.of(s));
        return this;
    }

    /**
     * Add a fixed component to the combinator
     * @param s The string that will always be present in the final string
     * @return this
     */
    public Combinator fixed(String s){
        components.add(List.of(s));
        return this;
    }

    /**
     * Create all variants of the string in a "combinatorial explosion"
     * @return A list of strings for each possible variation
     */
    public List<String> explode(){
        return explode(0);
    }

    private List<String> explode(int from){
        if(from == components.size() - 1){
            return components.get(from);
        }else{
            List<String> strings = new ArrayList<>();
            for(String option : components.get(from)){
                List<String> ends = explode(from + 1);
                for(String end : ends){
                    strings.add(option + end);
                }
            }
            return strings;
        }
    }
}
