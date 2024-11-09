package ca.awoo;

import java.util.Collections;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface CLib extends Library {
    CLib INSTANCE = Native.load("c", CLib.class, Collections.singletonMap(Library.OPTION_FUNCTION_MAPPER, StdCallLibrary.FUNCTION_MAPPER));
    public String setlocale(int category, String locale);
    public static int LC_NUMERIC = 1;
}
