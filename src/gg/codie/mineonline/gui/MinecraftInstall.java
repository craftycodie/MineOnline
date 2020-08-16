package gg.codie.mineonline.gui;

import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.utils.MD5Checksum;

import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public class MinecraftInstall {
    public String getName() {
        return name;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getAppletClass() {
        return appletClass;
    }

    public String getJarPath() {
        return jarPath;
    }

    String name;
    String mainClass;
    String appletClass;
    String jarMD5;
    String versionName;

    public void setName(String name) {
        this.name = name;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void setAppletClass(String appletClass) {
        this.appletClass = appletClass;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;

        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
                public String run() throws Exception {
                    return jarPath;
                }
            });

            this.jarMD5 = MD5Checksum.getMD5ChecksumForFile(jarPath);

            MinecraftVersion version = MinecraftVersionRepository.getSingleton().getVersionByMD5(jarMD5);

            if(version != null) {
                versionName = version.name;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String jarPath;

    public MinecraftInstall(String name, String mainClass, String appletClass, String jarPath) {
        this.name = name;
        this.mainClass = mainClass;
        this.appletClass = appletClass;
        this.jarPath = jarPath;

        try {
            this.jarMD5 = MD5Checksum.getMD5ChecksumForFile(jarPath);

            MinecraftVersion version = MinecraftVersionRepository.getSingleton().getVersionByMD5(jarMD5);

            if(version != null) {
                versionName = version.name;
            }
        } catch (Exception e) {}
    }
}
