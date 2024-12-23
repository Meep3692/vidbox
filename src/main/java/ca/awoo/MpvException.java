package ca.awoo;

public class MpvException extends RuntimeException{
    private final MpvError cause;

    public MpvException(int error){
        this(MpvError.fromValue(error));
    }

    public MpvException(MpvError cause){
        super(cause.toString());
        this.cause = cause;
    }

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
