package gg.codie.mineonline.gui;

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
    }

    String jarPath;

    public MinecraftInstall(String name, String mainClass, String appletClass, String jarPath) {
        this.name = name;
        this.mainClass = mainClass;
        this.appletClass = appletClass;
        this.jarPath = jarPath;
    }
}
