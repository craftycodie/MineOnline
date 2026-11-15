package gg.codie.mineonline.utils;

import gg.codie.common.utils.OSUtils;
import gg.codie.mineonline.Settings;

import java.io.File;

public class JREUtils {
    public static String getRunningJavaExecutable() {
        return getJavaExecutable(System.getProperty("java.home"));
    }

    public static String getJavaExecutable() {
        Settings.singleton.loadSettings();
        String javaHome = Settings.singleton.getJavaHome();
        if (javaHome == null || javaHome.isEmpty())
            return getRunningJavaExecutable();

        String javaExecutable = getJavaExecutable(javaHome);
        if (!new File(javaExecutable).isFile()) {
            System.out.println("Configured Java home \"" + javaHome + "\" has no Java executable at " + javaExecutable + ", using the running Java instead.");
            return getRunningJavaExecutable();
        }

        return javaExecutable;
    }

    private static String getJavaExecutable(String javaHome) {
        switch(OSUtils.getPlatform()) {
            case windows:
                return javaHome + File.separator + "bin" + File.separator + "java.exe";
            default:
                return javaHome + File.separator + "bin" + File.separator + "java";
        }
    }
}
