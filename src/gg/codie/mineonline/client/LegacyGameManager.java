package gg.codie.mineonline.client;

import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.patches.ClassGetResourceAdvice;
import gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice;

import java.io.IOException;

public class LegacyGameManager {

    private static LegacyGameManager singleton;

    private MinecraftVersion version;
    private IMinecraftAppletWrapper appletWrapper;

    private Options minecraftOptions = null;

    private LegacyGameManager(MinecraftVersion version, IMinecraftAppletWrapper appletWrapper) {
        this.version = version;
        this.appletWrapper = appletWrapper;
        try {
            minecraftOptions = new Options(LauncherFiles.MINEONLINE_OPTIONS_PATH, version.optionsVersion);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void createGameManager(MinecraftVersion version, IMinecraftAppletWrapper appletWrapper) {
        if (singleton != null) {
            System.err.println("Legacy game manager already exists!");
        } else {
            singleton = new LegacyGameManager(version, appletWrapper);
        }
    }

    private static LegacyGameManager getSingleton() {
        return singleton;
    }

    public static MinecraftVersion getVersion() {
        return getSingleton().version;
    }

    public static IMinecraftAppletWrapper getAppletWrapper() {
        return getSingleton().appletWrapper;
    }

    public static void closeGame() {
        getAppletWrapper().closeApplet();
    }

    public static void setTexturePack(String texturePack) {
        Settings.singleton.setTexturePack(texturePack);
        Settings.singleton.saveSettings();
        ClassGetResourceAdvice.texturePack = texturePack;
        Loader.reloadMinecraftTextures(getAppletWrapper().getMinecraftAppletClass(), texturePack);
    }

    public static void setFOV(int fov) {
        Settings.singleton.setFOV(fov);
        Settings.singleton.saveSettings();
        LWJGLPerspectiveAdvice.customFOV = fov;
    }

    public static boolean isInGame() {
        return getSingleton() != null;
    }
}
