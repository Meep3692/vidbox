package ca.awoo;

public class MpvException extends Exception{
    private final MpvError cause;

    public MpvException(MpvError cause, String message){
        super(cause + " " + message);
        this.cause = cause;
    }

    public MpvException(int errorId, String message){
        this(MpvError.fromValue(errorId), message);
    }

    public MpvError cause(){
        return cause;
    }
}
