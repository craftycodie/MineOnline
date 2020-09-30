package gg.codie.mineonline.utils;

import gg.codie.mineonline.Settings;
import gg.codie.utils.OSUtils;

import java.io.File;

public class JREUtils {
    public static String getRunningJavaExecutable() {
        switch(OSUtils.getPlatform()) {
            case windows:
                return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe";
            default:
                return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        }
    }

    public static String getJavaExecutable() {
        Settings.loadSettings();
        String javaHome = Settings.settings.has(Settings.JAVA_HOME) ? Settings.settings.getString(Settings.JAVA_HOME) : null;
        if (javaHome == null || javaHome.isEmpty())
            javaHome = System.getProperty("java.home");

        switch(OSUtils.getPlatform()) {
            case windows:
                return javaHome + File.separator + "bin" + File.separator + "java.exe";
            default:
                return javaHome + File.separator + "bin" + File.separator + "java";
        }
    }
}
