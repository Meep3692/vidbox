package ca.awoo;

public class PlayerOption {
    private final String name;
    private final String value;
    private final boolean option;

    public PlayerOption(String name, String value){
        this.name = name;
        this.value = value;
        option = false;
    }

    public PlayerOption(String name, String value, boolean option){
        this.name = name;
        this.value = value;
        this.option = option;
    }

    public String name(){
        return name;
    }

    public String value(){
        return value;
    }

    public boolean option(){
        return option;
    }

    @Override
    public String toString(){
        return "--" + name + "=" + value;
    }
}
