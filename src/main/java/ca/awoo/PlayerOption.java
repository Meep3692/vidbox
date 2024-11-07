package ca.awoo;

public class PlayerOption {
    private final String name;
    private final String value;

    public PlayerOption(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String name(){
        return name;
    }

    public String value(){
        return value;
    }

    @Override
    public String toString(){
        return "--" + name + "=" + value;
    }
}
