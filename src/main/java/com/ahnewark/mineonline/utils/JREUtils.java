package com.ahnewark.mineonline.utils;

import com.ahnewark.common.utils.OSUtils;
import com.ahnewark.mineonline.Settings;

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
        Settings.singleton.loadSettings();
        String javaHome = Settings.singleton.getJavaHome();
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
