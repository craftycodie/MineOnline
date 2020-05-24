package gg.codie.utils;

import java.util.Locale;

public class OSUtils {
    public enum OS {
        linux, solaris, windows, macos, unknown;
    }

    public static boolean isWindows() {
        return getPlatform() == OS.windows;
    }

    public static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) return OS.windows;
        if (osName.contains("mac")) return OS.macos;
        if (osName.contains("solaris")) return OS.solaris;
        if (osName.contains("sunos")) return OS.solaris;
        if (osName.contains("linux")) return OS.linux;
        if (osName.contains("unix")) return OS.linux;
        return OS.unknown;
    }
}
