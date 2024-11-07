package ca.awoo;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Interface to native mpv library
 */
public interface MPV extends StdCallLibrary {
    /**
     * Instance of mpv native library
     */
    MPV INSTANCE = Native.load("lib/libmpv-2.dll", MPV.class);
    
    /*
    * Event ID's
    */
    /**
     * End of file event id
     */
    int MPV_EVENT_END_FILE = 7;
    /**
     * File loaded event id
     */
    int MPV_EVENT_FILE_LOADED = 8;
    /**
     * Idle event id
     */
    int MPV_EVENT_IDLE = 11;
    /**
     * Tick event id
     */
    int MPV_EVENT_TICK = 14;
    
    /**
     * Return the MPV_CLIENT_API_VERSION the mpv source has been compiled with.
     * @return the MPV_CLIENT_API_VERSION
     */
    long mpv_client_api_version();
    /**
     * Create a new mpv instance and an associated client API handle to control
     * the mpv instance. This instance is in a pre-initialized state,
     * and needs to be initialized to be actually used with most other API
     * functions.
     *
     * Some API functions will return MPV_ERROR_UNINITIALIZED in the uninitialized
     * state. You can call mpv_set_property() (or mpv_set_property_string() and
     * other variants, and before mpv 0.21.0 mpv_set_option() etc.) to set initial
     * options. After this, call mpv_initialize() to start the player, and then use
     * e.g. mpv_command() to start playback of a file.
     *
     * The point of separating handle creation and actual initialization is that
     * you can configure things which can't be changed during runtime.
     *
     * Unlike the command line player, this will have initial settings suitable
     * for embedding in applications. The following settings are different:
     * - stdin/stdout/stderr and the terminal will never be accessed. This is
     *   equivalent to setting the --no-terminal option.
     *   (Technically, this also suppresses C signal handling.)
     * - No config files will be loaded. This is roughly equivalent to using
     *   --config=no. Since libmpv 1.15, you can actually re-enable this option,
     *   which will make libmpv load config files during mpv_initialize(). If you
     *   do this, you are strongly encouraged to set the "config-dir" option too.
     *   (Otherwise it will load the mpv command line player's config.)
     *   For example:
     *      mpv_set_option_string(mpv, "config-dir", "/my/path"); // set config root
     *      mpv_set_option_string(mpv, "config", "yes"); // enable config loading
     *      (call mpv_initialize() _after_ this)
     * - Idle mode is enabled, which means the playback core will enter idle mode
     *   if there are no more files to play on the internal playlist, instead of
     *   exiting. This is equivalent to the --idle option.
     * - Disable parts of input handling.
     * - Most of the different settings can be viewed with the command line player
     *   by running "mpv --show-profile=libmpv".
     *
     * All this assumes that API users want a mpv instance that is strictly
     * isolated from the command line player's configuration, user settings, and
     * so on. You can re-enable disabled features by setting the appropriate
     * options.
     *
     * The mpv command line parser is not available through this API, but you can
     * set individual options with mpv_set_property(). Files for playback must be
     * loaded with mpv_command() or others.
     *
     * Note that you should avoid doing concurrent accesses on the uninitialized
     * client handle. (Whether concurrent access is definitely allowed or not has
     * yet to be decided.)
     *
     * @return a new mpv client API handle. Returns NULL on error. Currently, this
     *         can happen in the following situations:
     *         - out of memory
     *         - LC_NUMERIC is not set to "C" (see general remarks)
     */
    long mpv_create();

    /**
     * Initialize an uninitialized mpv instance. If the mpv instance is already
     * running, an error is returned.
     *
     * This function needs to be called to make full use of the client API if the
     * client API handle was created with mpv_create().
     *
     * Only the following options are required to be set _before_ mpv_initialize():
     *      - options which are only read at initialization time:
     *        - config
     *        - config-dir
     *        - input-conf
     *        - load-scripts
     *        - script
     *        - player-operation-mode
     *        - input-app-events (macOS)
     *      - all encoding mode options
     *
     * @param handle the mpv client API handle returned from mpv_create()
     * @return error code
     */
    int mpv_initialize(long handle);

    /**
     * Send a command to the player. Commands are the same as those used in
     * input.conf, except that this function takes parameters in a pre-split
     * form.
     *
     * The commands and their parameters are documented in input.rst.
     *
     * Does not use OSD and string expansion by default (unlike mpv_command_string()
     * and input.conf).
     *
     * @param handle the mpv client API handle returned from mpv_create()
     * @param args List of strings. Usually, the first item
     *                 is the command, and the following items are arguments.
     * @return error code
     */
    int mpv_command(long handle, String[] args);

    /**
     * Same as mpv_command, but use input.conf parsing for splitting arguments.
     * This is slightly simpler, but also more error prone, since arguments may
     * need quoting/escaping.
     *
     * This also has OSD and string expansion enabled by default.
     * @param handle the mpv client API handle returned from mpv_create()
     * @param args List of strings. Usually, the first item is the command, and the following items are arguments.
     * @return error code
     */
    int mpv_command_string(long handle, String args);

    /**
     * Return the value of the property with the given name as string. This is
     * equivalent to mpv_get_property() with MPV_FORMAT_STRING.
     *
     * See MPV_FORMAT_STRING for character encoding issues.
     *
     * On error, NULL is returned. Use mpv_get_property() if you want fine-grained
     * error reporting.
     *
     * @param handle the mpv client API handle returned from mpv_create()
     * @param name The property name.
     * @return Property value, or NULL if the property can't be retrieved. Free
     *         the string with mpv_free().
     */
    Pointer mpv_get_property_string(long handle, String name);

    /**
     * Convenience function to set a property to a string value.
     *
     * This is like calling mpv_set_property() with MPV_FORMAT_STRING.
     * @param handle the mpv client API handle returned from mpv_create()
     * @param name The property name.
     * @param data New property string value.
     * @return error code
     */
    int mpv_set_property_string(long handle, String name, String data);

    /**
     * Convenience function to set an option to a string value. This is like
     * calling mpv_set_option() with MPV_FORMAT_STRING.
     *
     * @param handle the mpv client API handle returned from mpv_create()
     * @param name The property name.
     * @param data New property string value.
     * @return error code
     */
    int mpv_set_option_string(long handle, String name, String data);

    /**
     * Free data allocated by mpv
     * @param data Pointer allocated by mpv
     */
    void mpv_free(Pointer data);

    /**
     * Set an option. Note that you can't normally set options during runtime. It
     * works in uninitialized state (see mpv_create()), and in some cases in at
     * runtime.
     *
     * Using a format other than MPV_FORMAT_NODE is equivalent to constructing a
     * mpv_node with the given format and data, and passing the mpv_node to this
     * function.
     *
     * Note: this is semi-deprecated. For most purposes, this is not needed anymore.
     *       Starting with mpv version 0.21.0 (version 1.23) most options can be set
     *       with mpv_set_property() (and related functions), and even before
     *       mpv_initialize(). In some obscure corner cases, using this function
     *       to set options might still be required (see
     *       "Inconsistencies between options and properties" in the manpage). Once
     *       these are resolved, the option setting functions might be fully
     *       deprecated.
     *
     * @param handle the mpv client API handle returned from mpv_create()
     * @param name Option name. This is the same as on the mpv command line, but
     *             without the leading "--".
     * @param format see enum mpv_format.
     * @param data Option value (according to the format).
     * @return error code
     */
    int mpv_set_option(long handle, String name, int format, Pointer data);
    
    /**
     * Wait for the next event, or until the timeout expires, or if another thread
     * makes a call to mpv_wakeup(). Passing 0 as timeout will never wait, and
     * is suitable for polling.
     *
     * The internal event queue has a limited size (per client handle). If you
     * don't empty the event queue quickly enough with mpv_wait_event(), it will
     * overflow and silently discard further events. If this happens, making
     * asynchronous requests will fail as well (with MPV_ERROR_EVENT_QUEUE_FULL).
     *
     * Only one thread is allowed to call this on the same mpv_handle at a time.
     * The API won't complain if more than one thread calls this, but it will cause
     * race conditions in the client when accessing the shared mpv_event struct.
     * Note that most other API functions are not restricted by this, and no API
     * function internally calls mpv_wait_event(). Additionally, concurrent calls
     * to different mpv_handles are always safe.
     *
     * As long as the timeout is 0, this is safe to be called from mpv render API
     * threads.
     *
     * @param handle the mpv client API handle returned from mpv_create()
     * @param timeOut Timeout in seconds, after which the function returns even if
     *                no event was received. A MPV_EVENT_NONE is returned on
     *                timeout. A value of 0 will disable waiting. Negative values
     *                will wait with an infinite timeout.
     * @return A struct containing the event ID and other data. The pointer (and
     *         fields in the struct) stay valid until the next mpv_wait_event()
     *         call, or until the mpv_handle is destroyed. You must not write to
     *         the struct, and all memory referenced by it will be automatically
     *         released by the API on the next mpv_wait_event() call, or when the
     *         context is destroyed. The return value is never NULL.
     */
    mpv_event mpv_wait_event(long handle, double timeOut);

    /**
     * Enable or disable the given event.
     *
     * Some events are enabled by default. Some events can't be disabled.
     *
     * (Informational note: currently, all events are enabled by default, except
     *  MPV_EVENT_TICK.)
     *
     * Safe to be called from mpv render API threads.
     *
     * @param handle the mpv client API handle returned from mpv_create()
     * @param event_id See enum mpv_event_id.
     * @param enable 1 to enable receiving this event, 0 to disable it.
     * @return error code
     */
    int mpv_request_event(long handle, int event_id, int enable);
    
    /**
     * Event structure returned by mpv_wait_event
     */
    class mpv_event extends Structure {
        public int event_id;
        public int error;
        public long reply_userdata;
        public Pointer data;
        
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"event_id", "error", "reply_userdata", "data"});
        }
    }
}
