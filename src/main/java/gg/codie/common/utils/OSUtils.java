package gg.codie.common.utils;

import java.util.Locale;

public class OSUtils {
    public enum OS {
        linux, solaris, windows, macosx, macosxm1, unknown;
    }

    public static boolean isWindows() {
        return getPlatform() == OS.windows;
    }

    public static boolean isMac() {
        return getPlatform() == OS.macosx || getPlatform() == OS.macosxm1;
    }

    public static boolean isM1Mac() {
        return getPlatform() == OS.macosxm1;
    }

    public static boolean isLinux() {
        return getPlatform() == OS.linux;
    }

    public static OS getPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        if (osName.contains("win")) return OS.windows;
        if (osName.contains("mac") && (osArch.contains("arm64") || osArch.contains("aarch64"))) return OS.macosxm1;
        if (osName.contains("mac")) return OS.macosx;
        if (osName.contains("solaris")) return OS.solaris;
        if (osName.contains("sunos")) return OS.solaris;
        if (osName.contains("linux")) return OS.linux;
        if (osName.contains("unix")) return OS.linux;
        return OS.unknown;
    }
}
