package com.ahnewark.mineonline.client;

import com.ahnewark.common.utils.FileChangeListener;
import com.ahnewark.mineonline.LauncherFiles;
import com.ahnewark.mineonline.MinecraftVersion;
import com.ahnewark.mineonline.Settings;
import com.ahnewark.mineonline.gui.GUIScale;
import com.ahnewark.minecraft.client.options.EMinecraftGUIScale;
import com.ahnewark.minecraft.client.options.EMinecraftMainHand;
import com.ahnewark.minecraft.client.options.EMinecraftOptionsVersion;
import com.ahnewark.minecraft.client.options.MinecraftOptions;
import com.ahnewark.mineonline.discord.DiscordRPCHandler;
import com.ahnewark.mineonline.gui.components.GuiToast;
import com.ahnewark.mineonline.gui.rendering.DisplayManager;
import com.ahnewark.mineonline.gui.rendering.Font;
import com.ahnewark.mineonline.gui.rendering.Loader;
import com.ahnewark.mineonline.gui.screens.AbstractGuiScreen;
import com.ahnewark.mineonline.gui.textures.EGUITexture;
import com.ahnewark.mineonline.patches.ByteBufferPatch;
import com.ahnewark.mineonline.patches.ClassPatch;
import com.ahnewark.mineonline.patches.HashMapPatch;
import com.ahnewark.mineonline.patches.minecraft.*;
import com.ahnewark.mineonline.patches.*;
import com.ahnewark.mineonline.patches.lwjgl.LWJGLGL11GLOrthoAdvice;
import com.ahnewark.mineonline.patches.lwjgl.LWJGLGL11Patch;
import com.ahnewark.mineonline.patches.lwjgl.LWJGLGLUPatch;
import com.ahnewark.mineonline.patches.lwjgl.LWJGLGLUPerspectiveAdvice;
import com.ahnewark.mineonline.patches.minecraft.*;
import com.ahnewark.mineonline.patches.paulscode.PaulscodePatch;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.awt.*;

public class LegacyGameManager {

    private static LegacyGameManager singleton;
    public static AbstractGuiScreen guiScreen;
    public static boolean thirdPersonView;

    private MinecraftVersion version;
    private IMinecraftAppletWrapper appletWrapper;
    private FileChangeListener optionsListener;

    private GuiToast guiToast = new GuiToast();

    private LegacyGameManager(MinecraftVersion version, IMinecraftAppletWrapper appletWrapper) {
        this.version = version;
        this.appletWrapper = appletWrapper;

        optionsListener = new FileChangeListener(LauncherFiles.MINEONLINE_OPTIONS_PATH, new FileChangeListener.FileChangeEvent() {
            @Override
            public void onFileChange(String filePath) {
                // Let the LWJGL thread handle this.
                optionsFileChanged = true;
            }
        });

        new Thread(optionsListener).start();
    }

    private static boolean f5wasDown;
    public static void update() {
        readOptionChanges();

        if (Mouse.isGrabbed() && Keyboard.getEventKey() == Keyboard.KEY_F5 && !Keyboard.isRepeatEvent() && Keyboard.getEventKeyState() && !f5wasDown) {
            thirdPersonView = !thirdPersonView;
            f5wasDown = true;
        } else if (Keyboard.getEventKey() == Keyboard.KEY_F5 && !Keyboard.getEventKeyState()) {
            f5wasDown = false;
        }
    }

    static boolean optionsFileChanged = false;
    private static void readOptionChanges() {
        if (!optionsFileChanged)
            return;
        else
            optionsFileChanged = false;

        try {
            MinecraftOptions minecraftOptions = new MinecraftOptions(LauncherFiles.MINEONLINE_OPTIONS_PATH, getVersion() != null ? getVersion().optionsVersion : EMinecraftOptionsVersion.DEFAULT);

            try {
                setTexturePack(minecraftOptions.getTexturePack());
            } catch (NoSuchFieldException ne) {
                // ignore.
            }

            try {
                setGUIScale(minecraftOptions.getGUIScale());
            } catch (NoSuchFieldException ne) {
                // ignore
            }

            try {
                if (getVersion() == null || getVersion().useFOVPatch)
                    setFOV((int) minecraftOptions.getFOV());
            } catch (NoSuchFieldException ne) {
                // ignore
            }

            try {
                Settings.singleton.setSoundVolume(minecraftOptions.getSoundVolume());
            } catch (NoSuchFieldException ne) {
                // ignore
            }
        } catch (Exception ex) {
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

    public static void renderToast() {
        if (singleton == null) return;
        singleton.guiToast.renderToast();
    }

    private static void preparePatches() {
        System.out.println("Preparing Patches!");

        MinecraftVersion version = getVersion();

        // Allow the MineOnline menu to freeze game input.
        InputPatch.init();

        HashMapPatch.init();
        ClassPatch.init();
        LWJGLGL11Patch.init();
        LWJGLGLUPatch.useCustomFOV();
        ByteBufferPatch.init();
        ColorizerPatch.init();
        FontPatch.init();
        CompassFXPatch.init();
        ClockFXPatch.init();

        if (version != null) {
            if (version.name.equals("Beta 1.3 Demo"))
                PCGamerDemoPatch.login();

            if (version.useIndevSoundPatch)
                PaulscodePatch.fixIndevAudio();
            if (version.itemRendererClass != null)
                ItemRendererPatch.useHDItems(version.itemRendererClass);
            if (version.useFOVPatch && version.entityRendererClass != null)
                FOVViewmodelPatch.fixViewmodelFOV(version.entityRendererClass, version.viewModelFunction, version.hurtEffectFunction, Settings.singleton.getMainHand() == EMinecraftMainHand.LEFT);

            // Fixes various input issues with classic - infdev versions.
            InputPatch.enableClassicFixes = version.enableCursorPatch;

            if (getVersion().scaledResolutionClass != null) {
                ScaledResolutionConstructorPatch.useGUIScale(getVersion().scaledResolutionClass);
                LWJGLGL11GLOrthoAdvice.enable = true;
            } else if (getVersion().guiClass != null && getVersion().guiScreenClass != null) {
                GuiScreenPatch.useGUIScale(getVersion().guiScreenClass);
                LWJGLGL11GLOrthoAdvice.enable = true;
            }
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
        singleton.optionsListener.stop();
        DiscordRPCHandler.stop();
        getAppletWrapper().closeApplet();
    }

    public static void setTexturePack(String texturePack) {
        Settings.singleton.setTexturePack(texturePack);
        Settings.singleton.saveSettings();
        Loader.reloadMinecraftTextures();
        if (Loader.singleton != null) {
            for (EGUITexture texture : EGUITexture.values()) {
                if (texture.useTexturePack) {
                    Loader.singleton.unloadTexture(texture);
                }
            }
        }
        ColorizerPatch.updateColorizers();

        if (Loader.singleton != null)
            Font.reloadFont();
    }

    public static void setFOV(int fov) {
        Settings.singleton.setFOV(fov);
        Settings.singleton.saveSettings();

        if (getVersion() != null && getVersion().useFOVPatch)
            LWJGLGLUPerspectiveAdvice.customFOV = fov;
    }

    public static void setHideVersionString(boolean hideVersionString) {
        Settings.singleton.setHideVersionString(hideVersionString);
        Settings.singleton.saveSettings();
    }

    public static void setGUIScale(EMinecraftGUIScale guiScale) {
        Settings.singleton.setGUIScale(guiScale);
        Settings.singleton.saveSettings();
        // TODO: Put these in one place.
        GuiScreenOpenAdvice.guiScale = guiScale.getIntValue();
        LWJGLGL11GLOrthoAdvice.guiScale = guiScale.getIntValue();
        ScaledResolutionConstructorAdvice.guiScale = guiScale.getIntValue();
        if (getGuiScreen() != null)
            getGuiScreen().resize(GUIScale.lastScaledWidth(), GUIScale.lastScaledHeight());
    }

    public static void setMainHand(EMinecraftMainHand mainHand) {
        Settings.singleton.setMainHand(mainHand);
        Settings.singleton.saveSettings();
        FOVViewmodelAdvice.leftHanded = mainHand == EMinecraftMainHand.LEFT;
    }

    public static boolean isInGame() {
        return getSingleton() != null;
    }

    public static int getWidth() {
        return getAppletWrapper().getWidth();
    }

    public static int getHeight() {
        return getAppletWrapper().getHeight();
    }

    public static boolean mineonlineMenuOpen() {
        return getGuiScreen() != null;
    }

    public static void setGUIScreen(AbstractGuiScreen guiScreen) {
        if (getVersion() != null && !getVersion().useMineOnlineMenu)
            return;

        if (LegacyGameManager.guiScreen == null) {
            InputPatch.isFocused = false;

            Canvas mcCanvas = Display.getParent();
            Mouse.setCursorPosition((mcCanvas.getWidth() / 2) + DisplayManager.getFrame().getInsets().left, mcCanvas.getHeight() / 2);
            Mouse.setGrabbed(false);
        } else {
            LegacyGameManager.getGuiScreen().onGuiClosed();
        }

        if (guiScreen == null) {
            if (Display.isActive()) {
                InputPatch.isFocused = true;
                Mouse.setGrabbed(true);
            }
        }

        LegacyGameManager.guiScreen = guiScreen;

        if (LegacyGameManager.guiScreen != null && Settings.singleton.getMenuToast()) {
            Settings.singleton.setMenuToast(false);
            Settings.singleton.saveSettings();
        }
    }

    public static AbstractGuiScreen getGuiScreen() {
        return guiScreen;
    }
}
