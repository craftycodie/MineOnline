package gg.codie.utils;

import java.util.Locale;

public class OSUtils {
    public static boolean isWindows() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        return OS.indexOf("win") >= 0;
    }
}
