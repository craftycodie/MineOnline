package gg.codie.mineonline.client;

import gg.codie.minecraft.client.EMinecraftGUIScale;
import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.patches.ClassGetResourceAdvice;
import gg.codie.mineonline.patches.StringCharAtAdvice;
import gg.codie.mineonline.patches.StringPatch;
import gg.codie.mineonline.patches.StringToCharArrayAdvice;
import gg.codie.mineonline.patches.lwjgl.LWJGLGL11GLOrthoAdvice;
import gg.codie.mineonline.patches.lwjgl.LWJGLPerspectiveAdvice;
import gg.codie.mineonline.patches.minecraft.GuiScreenOpenAdvice;
import gg.codie.mineonline.patches.minecraft.ScaledResolutionConstructorAdvice;

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

        preparePatches();
    }

    private static void preparePatches() {
        if (getVersion() != null && getVersion().ingameVersionString != null) {
            StringPatch.hideVersionNames(getVersion().ingameVersionString);
            StringPatch.enable = Settings.singleton.getHideVersionString();
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

    public static void setHideVersionString(boolean hideVersionString) {
        Settings.singleton.setHideVersionString(hideVersionString);
        Settings.singleton.saveSettings();
        StringPatch.enable = hideVersionString;
    }

    public static void setGUIScale(EMinecraftGUIScale guiScale) {
        Settings.singleton.setGUIScale(guiScale);
        Settings.singleton.saveSettings();
        // TODO: Put these in one place.
        GuiScreenOpenAdvice.guiScale = guiScale.getIntValue();
        LWJGLGL11GLOrthoAdvice.guiScale = guiScale.getIntValue();
        ScaledResolutionConstructorAdvice.guiScale = guiScale.getIntValue();
        // TODO: Fake Resize
        DisplayManager.getFrame().setSize(DisplayManager.getFrame().getSize());
        //getAppletWrapper().resize();
    }

    public static boolean isInGame() {
        return getSingleton() != null;
    }
}
