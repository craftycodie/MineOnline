package gg.codie.utils;

import java.io.File;

public class OSUtils {
    public enum OS {
        linux, solaris, windows, macosx, unknown;
    }

    public static boolean isWindows() {
        return getPlatform() == OS.windows;
    }

    public static boolean isMac() {
        return getPlatform() == OS.macosx;
    }

    public static boolean isLinux() {
        return getPlatform() == OS.linux;
    }

    public static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) return OS.windows;
        if (osName.contains("mac")) return OS.macosx;
        if (osName.contains("solaris")) return OS.solaris;
        if (osName.contains("sunos")) return OS.solaris;
        if (osName.contains("linux")) return OS.linux;
        if (osName.contains("unix")) return OS.linux;
        return OS.unknown;
    }
}
