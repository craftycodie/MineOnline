package gg.codie.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class OSUtils {
    public enum OS {
        linux, solaris, windows, macosx, macosxm1, unknown;
    }

    static boolean checkedUnderlyingArch = false;
    static boolean underlyingM1 = false;
    private static void getUnderlyingArch() throws IOException, InterruptedException {
        // On Mac OS, we can't trust the os.arch, as this returns the JVM architecture.
        // With Rosetta 2, the JVM architecture may not match the system architecture.
        // So, if the user is on a mac, we use this command to check the underlying arch, then cache the result.
        if (!isMac()) return;

        System.out.println("Checking underlying architecture...");

        String command = "sysctl -n sysctl.proc_translated";

        Process proc = Runtime.getRuntime().exec(command);

        // Read the output
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        StringBuilder outputBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            outputBuilder.append(line);
        }

        proc.waitFor();

        String output = outputBuilder.toString();

        System.out.println("M1 Check Complete... Output Below");
        System.out.println(output);
        System.out.println(output.contains("1"));

        checkedUnderlyingArch = true;

        if (output.contains("1"))
            underlyingM1 = true;
    }



    public static boolean isWindows() {
        return getPlatform() == OS.windows;
    }

    public static boolean isMac() {
        return getPlatform() == OS.macosx || getPlatform() == OS.macosxm1;
    }

    public static boolean isM1JVM() {
        return getPlatform() == OS.macosxm1;
    }

    public static boolean isM1System() {
        if (!checkedUnderlyingArch) {
            try {
                getUnderlyingArch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return underlyingM1;
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
