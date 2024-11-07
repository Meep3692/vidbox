package ca.awoo;

public enum MpvError {

    /**
     * No error happened (used to signal successful operation).
     * Keep in mind that many API functions returning error codes can also
     * return positive values, which also indicate success. API users can
     * hardcode the fact that ">= 0" means success.
     */
    MPV_ERROR_SUCCESS(0),
    /**
     * The event ringbuffer is full. This means the client is choked, and can't
     * receive any events. This can happen when too many asynchronous requests
     * have been made, but not answered. Probably never happens in practice,
     * unless the mpv core is frozen for some reason, and the client keeps
     * making asynchronous requests. (Bugs in the client API implementation
     * could also trigger this, e.g. if events become "lost".)
     */
    MPV_ERROR_EVENT_QUEUE_FULL(-1),
    /**
     * Memory allocation failed.
     */
    MPV_ERROR_NOMEM(-2),
    /**
     * The mpv core wasn't configured and initialized yet. See the notes in
     * mpv_create().
     */
    MPV_ERROR_UNINITIALIZED(-3),
    /**
     * Generic catch-all error if a parameter is set to an invalid or
     * unsupported value. This is used if there is no better error code.
     */
    MPV_ERROR_INVALID_PARAMETER(-4),
    /**
     * Trying to set an option that doesn't exist.
     */
    MPV_ERROR_OPTION_NOT_FOUND(-5),
    /**
     * Trying to set an option using an unsupported MPV_FORMAT.
     */
    MPV_ERROR_OPTION_FORMAT(-6),
    /**
     * Setting the option failed. Typically this happens if the provided option
     * value could not be parsed.
     */
    MPV_ERROR_OPTION_ERROR(-7),
    /**
     * The accessed property doesn't exist.
     */
    MPV_ERROR_PROPERTY_NOT_FOUND(-8),
    /**
     * Trying to set or get a property using an unsupported MPV_FORMAT.
     */
    MPV_ERROR_PROPERTY_FORMAT(-9),
    /**
     * The property exists, but is not available. This usually happens when the
     * associated subsystem is not active, e.g. querying audio parameters while
     * audio is disabled.
     */
    MPV_ERROR_PROPERTY_UNAVAILABLE(-10),
    /**
     * Error setting or getting a property.
     */
    MPV_ERROR_PROPERTY_ERROR(-11),
    /**
     * General error when running a command with mpv_command and similar.
     */
    MPV_ERROR_COMMAND(-12),
    /**
     * Generic error on loading (usually used with mpv_event_end_file.error).
     */
    MPV_ERROR_LOADING_FAILED(-13),
    /**
     * Initializing the audio output failed.
     */
    MPV_ERROR_AO_INIT_FAILED(-14),
    /**
     * Initializing the video output failed.
     */
    MPV_ERROR_VO_INIT_FAILED(-15),
    /**
     * There was no audio or video data to play. This also happens if the
     * file was recognized, but did not contain any audio or video streams,
     * or no streams were selected.
     */
    MPV_ERROR_NOTHING_TO_PLAY(-16),
    /**
     * When trying to load the file, the file format could not be determined,
     * or the file was too broken to open it.
     */
    MPV_ERROR_UNKNOWN_FORMAT(-17),
    /**
     * Generic error for signaling that certain system requirements are not
     * fulfilled.
     */
    MPV_ERROR_UNSUPPORTED(-18),
    /**
     * The API function which was called is a stub only.
     */
    MPV_ERROR_NOT_IMPLEMENTED(-19),
    /**
     * Unspecified error.
     */
    MPV_ERROR_GENERIC(-20);

    private int val;

    MpvError(int val){
        this.val = val;
    }

    public int getValue(){
        return val;
    }

    public static MpvError fromValue(int val){
        MpvError[] errors = MpvError.values();
        for(MpvError error : errors){
            if(error.getValue() == val){
                return error;
            }
        }
        return null;
    }
}
