package gg.codie.mineonline;

import gg.codie.minecraft.client.options.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Settings implements IMinecraftOptionsHandler {

    public static Settings singleton;
    private JSONObject settings;

    private static final String SETTINGS_VERSION = "settingsVersion";
    private static final String JAVA_HOME = "javaHome";
    private static final String CLIENT_LAUNCH_ARGS = "clientLaunchArgs";
    private static final String HIDE_VERSION_STRING = "hideVersionString";
    private static final String GAME_WIDTH = "gameWidth";
    private static final String GAME_HEIGHT = "gameHeight";
    private static final String CUSTOM_CAPES = "customCapes";
    private static final String KEY_CODE_ZOOM = "keyCodeZoom";
    private static final String SAMPLE_COUNT = "sampleCount";
    private static final String STENCIL_COUNT = "stencilCount";
    private static final String COVERAGE_SAMPLE_COUNT = "coverageSampleCount";
    private static final String LAST_LAUNCHED_OPTIONS_VERSION = "lastLaunchedOptionsVersion";
    @Deprecated
    private static final String USE_CUSTOM_FONTS = "useCustomFonts";

    private static final String SHOW_MENU_TOAST = "showMenuToast";
    private static final String SHOW_SCREENSHOT_TOAST = "showScreenshotToast";
    private static final String SHOW_ZOOM_TOAST = "showZoomToast";
    private static final String SHOW_PLAYERLIST_TOAST = "showPlayerListToast";

    private static final String FULLSCREEN = "fullscreen";
    private static final String GUI_SCALE = "guiScale";
    private static final String FOV = "fov";
    private static final String MAIN_HAND = "mainHand";
    private static final String TEXTURE_PACK = "texturePack";
    private static final String MUSIC = "music";
    private static final String SOUND = "sound";
    private static final String INVERT_Y_MOUSE = "invertYMouse";
    private static final String MOUSE_SENSITIVITY = "mouseSensitivity";
    private static final String RENDER_DISTANCE = "viewDistance";
    private static final String VIEW_BOBBING = "viewBobbing";
    private static final String ANAGLYPH_3D = "anaglyph3d";
    private static final String ADVANCED_OPEN_GL = "advancedOpengl";
    private static final String PERFORMANCE = "performance";
    private static final String DIFFICULTY = "difficulty";
    private static final String FANCY_GRAPHICS = "fancyGraphics";
    private static final String SMOOTH_LIGHTING = "smoothLighting";
    private static final String LAST_SERVER = "lastServer";
    private static final String SHOW_FPS = "showFPS";
    private static final String KEY_CODE_FORWARD = "keyCodeForward";
    private static final String KEY_CODE_LEFT = "keyCodeLeft";
    private static final String KEY_CODE_BACK = "keyCodeBack";
    private static final String KEY_CODE_RIGHT = "keyCodeRight";
    private static final String KEY_CODE_JUMP = "keyCodeJump";
    private static final String KEY_CODE_SNEAK = "keyCodeSneak";
    private static final String KEY_CODE_DROP = "keyCodeDrop";
    private static final String KEY_CODE_INVENTORY = "keyCodeInventory";
    private static final String KEY_CODE_CHAT = "keyCodeChat";
    private static final String KEY_CODE_FOG = "keyCodeFog";
    private static final String KEY_CODE_SAVE_LOCATION = "keyCodeSaveLocation";
    private static final String KEY_CODE_LOAD_LOCATION = "keyCodeLoadLocation";
    private static final String KEY_CODE_BUILD_MENU = "keyCodeBuildMenu";
    private static final String SKIN_LAYER_HEAD = "skinLayerHead";
    private static final String SKIN_LAYER_TORSO = "skinLayerTorso";
    private static final String SKIN_LAYER_LEFT_ARM = "skinLayerLeftArm";
    private static final String SKIN_LAYER_RIGHT_ARM = "skinLayerRightArm";
    private static final String SKIN_LAYER_LEFT_LEG = "skinLayerLeftLeg";
    private static final String SKIN_LAYER_RIGHT_LEG = "skinLayerRightLeg";
    private static final String AUTO_JUMP = "autoJump";
    private static final String KEY_CODE_INGAME_MENU = "keyCodeIngameMenu";
    private static final String LIMIT_FRAMERATE = "limitFramerate";
    private static final String LANGUAGE = "language";
    private static final String BRIGHTNESS = "brightness";
    private static final String PARTICLES = "particles";
    private static final String CLOUDS = "clouds";
    private static final String CHAT_VISIBILITY = "chatVisibility";
    private static final String CHAT_COLORS = "chatColors";
    private static final String CHAT_LINKS = "chatLinks";
    private static final String CHAT_LINKS_PROMPT = "chatLinksPrompt";
    private static final String CHAT_OPACITY = "chatOpacity";
    private static final String SERVER_TEXTURES = "serverTextures";
    private static final String SNOOPER_ENABLED = "snooperEnabled";
    private static final String HIDE_SERVER_ADDRESS = "hideServerAddress";
    private static final String ADVANCED_ITEM_TOOLTIPS = "advancedItemTooltips";
    private static final String PAUSE_ON_FOCUS_LOSS = "pauseOnFocusLoss";
    private static final String SHOW_CAPE = "showCape";
    private static final String TOUCHSCREEN = "touchscreen";
    private static final String VSYNC_ENABLED = "vsync";
    private static final String HELD_ITEM_TOOLTIPS = "heldItemTooltips";
    private static final String CHAT_HEIGHT_FOCUSED = "chatHeightFocused";
    private static final String CHAT_HEIGHT_UNFOCUSED = "chatHeightUnfocused";
    private static final String CHAT_SCALE = "chatScale";
    private static final String CHAT_WIDTH = "chatWidth";
    private static final String KEY_CODE_ATTACK = "keyCodeAttack";
    private static final String KEY_CODE_USE = "keyCodeUse";
    private static final String KEY_CODE_COMMAND = "keyCodeCommand";
    private static final String KEY_CODE_PICK_ITEM = "keyCodePickItem";
    private static final String KEY_CODE_PLAYER_LIST = "keyCodePlayerList";

    private static final int SETTINGS_VERSION_NUMBER = 16;

    private static boolean readonly = true;

    private Settings() {

    }

    static {
        try {
            Class.forName("org.json.JSONObject");

            readonly = false;

            singleton = new Settings();

            try {
                if (new File(LauncherFiles.MINEONLINE_SETTINGS_FILE).exists()) {
                    singleton.loadSettings();
                } else {
                    singleton.resetSettings();
                }
            } catch (JSONException ex) {
                System.err.println("Bad settings file, resetting.");
                singleton.resetSettings();
            }
        } catch (ClassNotFoundException ex) {
            singleton.resetSettings();
        }
    }

    public void resetSettings() {
        settings = new JSONObject();
        settings.put(SETTINGS_VERSION, SETTINGS_VERSION_NUMBER);
        settings.put(FULLSCREEN, false);
        settings.put(JAVA_HOME, "");
        settings.put(CLIENT_LAUNCH_ARGS, "");
        settings.put(FOV, 70);
        settings.put(GUI_SCALE, 3);
        settings.put(TEXTURE_PACK, "Default");
        settings.put(HIDE_VERSION_STRING, false);
        settings.put(GAME_WIDTH, 854);
        settings.put(GAME_HEIGHT, 480);
        settings.put(CUSTOM_CAPES, true);
        settings.put(SAMPLE_COUNT, 0);
        settings.put(STENCIL_COUNT, 0);
        settings.put(COVERAGE_SAMPLE_COUNT, 0);
        settings.put(USE_CUSTOM_FONTS, false);

        settings.put(KEY_CODE_INVENTORY, 18);
        settings.put(KEY_CODE_CHAT, 20);
        settings.put(KEY_CODE_DROP, 16);
        settings.put(KEY_CODE_SNEAK, 42);
        settings.put(KEY_CODE_JUMP, 57);
        settings.put(KEY_CODE_FOG, 33);
        settings.put(KEY_CODE_FORWARD, 17);
        settings.put(KEY_CODE_RIGHT, 32);
        settings.put(KEY_CODE_BACK, 31);
        settings.put(KEY_CODE_LEFT, 30);
        settings.put(KEY_CODE_SAVE_LOCATION, 28);
        settings.put(KEY_CODE_LOAD_LOCATION, 19);
        settings.put(KEY_CODE_BUILD_MENU, 48);
        settings.put(SHOW_FPS, false);
        settings.put(MUSIC, 1);
        settings.put(SOUND, 1);
        settings.put(INVERT_Y_MOUSE, false);
        settings.put(MOUSE_SENSITIVITY, 0.5f);
        settings.put(RENDER_DISTANCE, ELegacyMinecraftRenderDistance.FAR);
        settings.put(MAIN_HAND, EMinecraftMainHand.RIGHT);
        settings.put(VIEW_BOBBING, true);
        settings.put(ANAGLYPH_3D, false);
        settings.put(ADVANCED_OPEN_GL, true);
        settings.put(PERFORMANCE, EMinecraftPerformance.BALANCED);
        settings.put(DIFFICULTY, EMinecraftDifficulty.NORMAL);
        settings.put(FANCY_GRAPHICS, true);
        settings.put(SMOOTH_LIGHTING, true);
        settings.put(LAST_SERVER, "");
        settings.put(SKIN_LAYER_RIGHT_LEG, true);
        settings.put(SKIN_LAYER_LEFT_LEG, true);
        settings.put(SKIN_LAYER_RIGHT_ARM, true);
        settings.put(SKIN_LAYER_LEFT_ARM, true);
        settings.put(SKIN_LAYER_TORSO, true);
        settings.put(SKIN_LAYER_HEAD, true);
        settings.put(KEY_CODE_ZOOM, 0);
        settings.put(LAST_LAUNCHED_OPTIONS_VERSION, "DEFAULT");
        settings.put(AUTO_JUMP, false);
        settings.put(KEY_CODE_INGAME_MENU, 19);
        settings.put(LIMIT_FRAMERATE, false);

        saveSettings();
        loadSettings();
    }

    public void loadMinecraftOptions() {
        try {
            File optionsFile = new File(LauncherFiles.MINEONLINE_OPTIONS_PATH);
            if (!optionsFile.exists())
                return;

            EMinecraftOptionsVersion lastLaunchedOptionsVersion = getLastLaunchedOptionsVersion();

            MinecraftOptions options = new MinecraftOptions(LauncherFiles.MINEONLINE_OPTIONS_PATH, lastLaunchedOptionsVersion);

            try {
                // If the player used the classic music toggle, keep volume where possible.
                if (getLastLaunchedOptionsVersion() == EMinecraftOptionsVersion.CLASSIC) {
                   if (getMusicVolume() > 0 && options.getMusicVolume() == 0)
                       setMusicVolume(0);
                   else if (getMusicVolume() == 0 && options.getMusicVolume() == 1)
                       setMusicVolume(1);
                }
                else
                    setMusicVolume(options.getMusicVolume());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                // If the player used the classic music toggle, keep volume where possible.
                if (getLastLaunchedOptionsVersion() == EMinecraftOptionsVersion.CLASSIC) {
                    if (getSoundVolume() > 0 && options.getSoundVolume() == 0)
                        setSoundVolume(0);
                    else if (getSoundVolume() == 0 && options.getSoundVolume() == 1)
                        setSoundVolume(1);
                }
                else
                    setSoundVolume(options.getSoundVolume());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setInvertYMouse(options.getInvertYMouse());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setMouseSensitivity(options.getMouseSensitivity());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setRenderDistance(options.getRenderDistance());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setGUIScale(options.getGUIScale());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setViewBobbing(options.getViewBobbing());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                set3DAnalyhph(options.get3DAnaglyph());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setAdvancedOpenGL(options.getAdvancedOpenGL());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setLimitFramerate(options.getLimitFramerate());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setPerformance(options.getPerformance());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setDifficulty(options.getDifficulty());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setFancyGraphics(options.getFancyGraphics());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setSmoothLighting(options.getSmoothLighting());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setTexturePack(options.getTexturePack());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setLastServer(options.getLastServer());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setForwardKeyCode(options.getForwardKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setLeftKeyCode(options.getLeftKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setBackKeyCode(options.getBackKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setRightKeyCode(options.getRightKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setJumpKeyCode(options.getJumpKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setSneakKeyCode(options.getSneakKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setDropKeyCode(options.getDropKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setInventoryKeyCode(options.getInventoryKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setChatKeyCode(options.getChatKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setFogKeyCode(options.getFogKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setFOV(options.getFOV());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setFullscreen(options.getFullscreen());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setMainHand(options.getMainHand());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setShowHat(options.getShowHat());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setShowJacket(options.getShowJacket());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setShowLeftSleeve(options.getShowLeftSleeve());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setShowRightSleeve(options.getShowRightSleeve());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setShowLeftPantsLeg(options.getShowLeftPantsLeg());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
                setShowRightPantsLeg(options.getShowRightPantsLeg());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }

            try {
                setShowFPS(options.getShowFPS());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }

            try {
                setBuildMenuKeyCode(options.getBuildMenuKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }

            try {
                setLoadLocationKeyCode(options.getLoadLocationKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }

            try {
                setSaveLocationKeyCode(options.getSaveLocationKeyCode());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }

            try {
                setAutoJump(options.getAutoJump());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }

            new File(LauncherFiles.MINEONLINE_OPTIONS_PATH).delete();
        } catch (Exception ex) {
            // ignore
        }
    }

    public void saveMinecraftOptions(EMinecraftOptionsVersion optionsVersion) {
        try {
            File optionsFile = new File(LauncherFiles.MINEONLINE_OPTIONS_PATH);
            if (!optionsFile.exists())
                optionsFile.createNewFile();

            MinecraftOptions options = new MinecraftOptions(LauncherFiles.MINEONLINE_OPTIONS_PATH, optionsVersion);

            setLastLaunchedOptionsVersion(optionsVersion);
            saveSettings();

            options.setMusicVolume(getMusicVolume());
            options.setSoundVolume(getSoundVolume());
            options.setInvertYMouse(getInvertYMouse());
            options.setMouseSensitivity(getMouseSensitivity());
            options.setRenderDistance(getRenderDistance());
            options.setGUIScale(getGUIScale());
            options.setViewBobbing(getViewBobbing());
            options.set3DAnalyhph(get3DAnaglyph());
            options.setAdvancedOpenGL(getAdvancedOpenGL());
            options.setPerformance(getPerformance());
            options.setLimitFramerate(getLimitFramerate());
            options.setDifficulty(getDifficulty());
            options.setFancyGraphics(getFancyGraphics());
            options.setSmoothLighting(getSmoothLighting());
            options.setTexturePack(getTexturePack());
            options.setLastServer(getLastServer());
            options.setForwardKeyCode(getForwardKeyCode());
            options.setLeftKeyCode(getLeftKeyCode());
            options.setBackKeyCode(getBackKeyCode());
            options.setRightKeyCode(getRightKeyCode());
            options.setJumpKeyCode(getJumpKeyCode());
            options.setSneakKeyCode(getSneakKeyCode());
            options.setDropKeyCode(getDropKeyCode());
            options.setInventoryKeyCode(getInventoryKeyCode());
            options.setChatKeyCode(getChatKeyCode());
            options.setFogKeyCode(getFogKeyCode());
            options.setFOV(getFOV());
            options.setFullscreen(getFullscreen());
            options.setMainHand(getMainHand());
            options.setShowHat(getShowHat());
            options.setShowJacket(getShowJacket());
            options.setShowLeftSleeve(getShowLeftSleeve());
            options.setShowRightSleeve(getShowRightSleeve());
            options.setShowLeftPantsLeg(getShowLeftPantsLeg());
            options.setShowRightPantsLeg(getShowRightPantsLeg());
            options.setAutoJump(getAutoJump());
            options.setCommandKey(getCommandKey());
            options.setPlayerListKey(getPlayerListKey());
            options.setPickItemKey(getPickItemKey());
            options.setUseKey(getUseKey());
            options.setAttackKey(getAttackKey());
            options.setChatOpacity(getChatOpacity());
            options.setChatColors(getChatColors());
            options.setChatLinks(getChatLinks());
            options.setChatLinksPrompt(getChatLinksPrompt());
            options.setChatWidth(getChatWidth());
            options.setChatHeightFocused(getChatHeightFocused());
            options.setChatHeightUnfocused(getChatHeightUnfocused());
            options.setShowCape(getShowCape());
            options.setPauseOnLostFocus(getPauseOnLostFocus());
            options.setVsync(getVsync());
            options.setHeldItemTooltips(getHeldItemTooltips());
            options.setHideServerAddress(getHideServerAddress());
            options.setAdvancedItemTooltips(getAdvancedItemTooltips());
            options.setChatVisibility(getChatVisibility());
            options.setTouchscreen(getTouchscreen());
            options.setSnooperEnabled(getSnooperEnabled());
            options.setLanguage(getLanguage());
            options.setParticles(getParticles());
            options.setServerTextures(getServerTextures());
            options.setClouds(getClouds());
            options.setGamma(getGamma());
        } catch (Exception ex) {
            // ignore
        }
    }

    public void loadSettings() {
        loadSettings(false);
    }

    public void loadSettings(boolean reloadOptionsTxt) {
        try (FileInputStream input = new FileInputStream(LauncherFiles.MINEONLINE_SETTINGS_FILE)) {
            // load a settings file
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for (int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char) buffer[i]);
                }
            }

            settings = new JSONObject(stringBuffer.toString());

            if (reloadOptionsTxt)
                loadMinecraftOptions();

            // Assume V1, reset settings.
            if (!settings.has(SETTINGS_VERSION)) {
                resetSettings();
                saveSettings();
            } else {
                switch (settings.getInt(SETTINGS_VERSION)) {
                    case 3:
                        settings.put(JAVA_HOME, "");
                        settings.put(CLIENT_LAUNCH_ARGS, "");
                    case 4:
                        settings.put(FOV, 70);
                        settings.put(GUI_SCALE, 3);
                    case 5:
                        settings.put(TEXTURE_PACK, "Default");
                    case 6:
                        settings.put(HIDE_VERSION_STRING, false);
                    case 7:
                        settings.put(GAME_WIDTH, 854);
                        settings.put(GAME_HEIGHT, 480);
                        settings.put(CUSTOM_CAPES, true);
                    case 9:
                        settings.put(SAMPLE_COUNT, 0);
                        settings.put(STENCIL_COUNT, 0);
                        settings.put(COVERAGE_SAMPLE_COUNT, 0);
                        settings.put(SAMPLE_COUNT, 0);
                        settings.put(STENCIL_COUNT, 0);
                        settings.put(COVERAGE_SAMPLE_COUNT, 0);
                        settings.put(KEY_CODE_INVENTORY, 18);
                        settings.put(KEY_CODE_CHAT, 20);
                        settings.put(KEY_CODE_DROP, 16);
                        settings.put(KEY_CODE_SNEAK, 42);
                        settings.put(KEY_CODE_JUMP, 57);
                        settings.put(KEY_CODE_FOG, 33);
                        settings.put(KEY_CODE_FORWARD, 17);
                        settings.put(KEY_CODE_RIGHT, 32);
                        settings.put(KEY_CODE_BACK, 31);
                        settings.put(KEY_CODE_LEFT, 30);
                        settings.put(MUSIC, 1);
                        settings.put(SOUND, 1);
                        settings.put(INVERT_Y_MOUSE, false);
                        settings.put(MOUSE_SENSITIVITY, 0.5f);
                        settings.put(RENDER_DISTANCE, ELegacyMinecraftRenderDistance.FAR);
                        settings.put(MAIN_HAND, EMinecraftMainHand.RIGHT);
                        settings.put(VIEW_BOBBING, true);
                        settings.put(ANAGLYPH_3D, false);
                        settings.put(ADVANCED_OPEN_GL, true);
                        settings.put(PERFORMANCE, EMinecraftPerformance.BALANCED);
                        settings.put(DIFFICULTY, EMinecraftDifficulty.NORMAL);
                        settings.put(FANCY_GRAPHICS, true);
                        settings.put(SMOOTH_LIGHTING, true);
                        settings.put(LAST_SERVER, "");
                        settings.put(SKIN_LAYER_RIGHT_LEG, true);
                        settings.put(SKIN_LAYER_LEFT_LEG, true);
                        settings.put(SKIN_LAYER_RIGHT_ARM, true);
                        settings.put(SKIN_LAYER_LEFT_ARM, true);
                        settings.put(SKIN_LAYER_TORSO, true);
                        settings.put(SKIN_LAYER_HEAD, true);
                        settings.put(KEY_CODE_ZOOM, 0);
                        settings.put(LAST_LAUNCHED_OPTIONS_VERSION, EMinecraftOptionsVersion.DEFAULT);
                    case 10:
                        settings.put(AUTO_JUMP, false);
                    case 11:
                        settings.put(KEY_CODE_INGAME_MENU, 19);
                    case 12:
                        settings.put(USE_CUSTOM_FONTS, false);
                    case 13:
                        if (settings.has(USE_CUSTOM_FONTS))
                            settings.remove(USE_CUSTOM_FONTS);
                    case 15:
                        if (getPickItemKey() == -99)
                            setPickItemKey(-98);
                }
                settings.put(SETTINGS_VERSION, SETTINGS_VERSION_NUMBER);
            }

            if (settings.has("redirectedDomains"))
                settings.remove("redirectedDomains");

            saveSettings();
        } catch (IOException ex) {
            saveSettings();
        } catch (JSONException ex) {
            resetSettings();
        }
    }

    public void saveSettings() {
        if (readonly)
            return;

        try {
            FileWriter fileWriter = new FileWriter(LauncherFiles.MINEONLINE_SETTINGS_FILE, false);
            fileWriter.write(settings.toString(2));
            fileWriter.close();

            FileInputStream input = new FileInputStream(LauncherFiles.MINEONLINE_SETTINGS_FILE);
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char)buffer[i]);
                }
            }

            input.close();

            settings = new JSONObject(stringBuffer.toString());
        } catch (IOException | JSONException io) {
            io.printStackTrace();
        }
    }

    public String getJavaHome() {
        return settings.optString(JAVA_HOME, "");
    }

    public String getClientLaunchArgs() {
        return settings.optString(CLIENT_LAUNCH_ARGS, "");
    }

    public boolean getHideVersionString() {
        return settings.optBoolean(HIDE_VERSION_STRING, false);
    }

    public void setHideVersionString(boolean hideVersionString) {
        settings.put(HIDE_VERSION_STRING, hideVersionString);
    }

    public int getGameWidth() {
        return settings.optInt(GAME_WIDTH, 854);
    }

    public int getGameHeight() {
        return settings.optInt(GAME_HEIGHT, 480);
    }

    public boolean getCustomCapes() {
        return settings.optBoolean(CUSTOM_CAPES, true);
    }

    public void setCustomCapes(boolean customCapes) {
        settings.put(CUSTOM_CAPES, customCapes);
    }

    public int getSampleCount() {
        return settings.optInt(SAMPLE_COUNT, 0);
    }

    public int getStencilCount() {
        return settings.optInt(STENCIL_COUNT, 0);
    }

    public int getCoverageSampleCount() {
        return settings.optInt(COVERAGE_SAMPLE_COUNT, 0);
    }

    public int getZoomKeyCode() {
        return settings.optInt(KEY_CODE_ZOOM, 0);
    }

    public int getMineonlineMenuKeyCode() {
        return settings.optInt(KEY_CODE_INGAME_MENU, 19);
    }

    public void setZoomKeyCode(int keyCode) {
        settings.put(KEY_CODE_ZOOM, keyCode);
    }

    public void setMineonlineMenuKeyCode(int keyCode) {
        settings.put(KEY_CODE_INGAME_MENU, keyCode);
    }

    public EMinecraftOptionsVersion getLastLaunchedOptionsVersion() {
        return settings.optEnum(EMinecraftOptionsVersion.class, LAST_LAUNCHED_OPTIONS_VERSION, EMinecraftOptionsVersion.DEFAULT);
    }

    public void setLastLaunchedOptionsVersion(EMinecraftOptionsVersion optionsVersion) {
        settings.put(LAST_LAUNCHED_OPTIONS_VERSION, optionsVersion);
    }

    public boolean getMenuToast() {
        return settings.optBoolean(SHOW_MENU_TOAST, true);
    }
    public void setMenuToast(boolean show) {
        settings.put(SHOW_MENU_TOAST, show);
    }

    public boolean getScreenshotToast() {
        return settings.optBoolean(SHOW_SCREENSHOT_TOAST, true);
    }
    public void setScreenshotToast(boolean show) {
        settings.put(SHOW_SCREENSHOT_TOAST, show);
    }

    public boolean getZoomToast() {
        return settings.optBoolean(SHOW_ZOOM_TOAST, true);
    }
    public void setZoomToast(boolean show) {
        settings.put(SHOW_ZOOM_TOAST, show);
    }

    public boolean getPlayerListToast() {
        return settings.optBoolean(SHOW_PLAYERLIST_TOAST, true);
    }
    public void setPlayerListToast(boolean show) {
        settings.put(SHOW_PLAYERLIST_TOAST, show);
    }

    @Override
    public float getMusicVolume() {
        return settings.optFloat(MUSIC, 1);
    }

    @Override
    public void setMusicVolume(float volume) {
        settings.put(MUSIC, volume);
    }

    @Override
    public float getSoundVolume() {
        return settings.optFloat(SOUND, 1);
    }

    @Override
    public void setSoundVolume(float volume) {
        settings.put(SOUND, volume);
    }

    @Override
    public boolean getShowFPS() {
        return settings.optBoolean(SHOW_FPS, false);
    }

    @Override
    public void setShowFPS(boolean showFPS) {
        settings.put(SHOW_FPS, showFPS);
    }

    @Override
    public boolean getInvertYMouse() {
        return settings.optBoolean(INVERT_Y_MOUSE, false);
    }

    @Override
    public void setInvertYMouse(boolean invertYMouse) {
        settings.put(INVERT_Y_MOUSE, invertYMouse);
    }

    @Override
    public float getMouseSensitivity() {
        return settings.optFloat(MOUSE_SENSITIVITY, 1);
    }

    @Override
    public void setMouseSensitivity(float sensitivity) {
        settings.put(MOUSE_SENSITIVITY, sensitivity);
    }

    @Override
    public ELegacyMinecraftRenderDistance getRenderDistance() {
        return settings.optEnum(ELegacyMinecraftRenderDistance.class, RENDER_DISTANCE, ELegacyMinecraftRenderDistance.FAR);
    }

    @Override
    public void setRenderDistance(ELegacyMinecraftRenderDistance renderDistance) {
        settings.put(RENDER_DISTANCE, renderDistance);
    }

    @Override
    public EMinecraftGUIScale getGUIScale() {
        return settings.optEnum(EMinecraftGUIScale.class, GUI_SCALE, EMinecraftGUIScale.AUTO);
    }

    @Override
    public void setGUIScale(EMinecraftGUIScale guiScale) {
        settings.put(GUI_SCALE, guiScale);
    }

    @Override
    public boolean getViewBobbing() {
        return settings.optBoolean(VIEW_BOBBING, true);
    }

    @Override
    public void setViewBobbing(boolean viewBobbing) {
        settings.put(VIEW_BOBBING, viewBobbing);
    }

    @Override
    public boolean get3DAnaglyph() {
        return settings.optBoolean(ANAGLYPH_3D, true);
    }

    @Override
    public void set3DAnalyhph(boolean analyhph) {
        settings.put(ANAGLYPH_3D, analyhph);
    }

    @Override
    public boolean getAdvancedOpenGL() {
        return settings.optBoolean(ADVANCED_OPEN_GL, true);
    }

    @Override
    public void setAdvancedOpenGL(boolean advancedOpenGL) {
        settings.put(ADVANCED_OPEN_GL, advancedOpenGL);
    }

    @Override
    public boolean getLimitFramerate() {
        return settings.optBoolean(LIMIT_FRAMERATE, false);
    }

    @Override
    public void setLimitFramerate(boolean limitFramerate) {
        settings.put(LIMIT_FRAMERATE, limitFramerate);
    }

    @Override
    public EMinecraftPerformance getPerformance() {
        return settings.optEnum(EMinecraftPerformance.class, PERFORMANCE, EMinecraftPerformance.BALANCED);
    }

    @Override
    public void setPerformance(EMinecraftPerformance performance) {
        settings.put(PERFORMANCE, performance);
    }

    @Override
    public EMinecraftDifficulty getDifficulty() {
        return settings.optEnum(EMinecraftDifficulty.class, DIFFICULTY, EMinecraftDifficulty.NORMAL);
    }

    @Override
    public void setDifficulty(EMinecraftDifficulty difficulty) {
        settings.put(DIFFICULTY, difficulty.getIntValue());
    }

    @Override
    public boolean getFancyGraphics() {
        return settings.optBoolean(FANCY_GRAPHICS, true);
    }

    @Override
    public void setFancyGraphics(boolean fancyGraphics) {
        settings.put(FANCY_GRAPHICS, fancyGraphics);
    }

    @Override
    public boolean getSmoothLighting() {
        return settings.optBoolean(SMOOTH_LIGHTING, true);
    }

    @Override
    public void setSmoothLighting(boolean smoothLighting) {
        settings.put(SMOOTH_LIGHTING, smoothLighting);
    }

    @Override
    public String getTexturePack() {
        return settings.optString(TEXTURE_PACK, "");
    }

    @Override
    public void setTexturePack(String texturePack) {
        settings.put(TEXTURE_PACK, texturePack);
    }

    @Override
    public String getLastServer() {
        return settings.optString(LAST_SERVER, "");
    }

    @Override
    public void setLastServer(String lastServer) {
        settings.put(LAST_SERVER, lastServer);
    }

    @Override
    public EMinecraftMainHand getMainHand() {
        return settings.optEnum(EMinecraftMainHand.class, MAIN_HAND, EMinecraftMainHand.RIGHT);
    }

    @Override
    public void setMainHand(EMinecraftMainHand mainHand) {
        settings.put(MAIN_HAND, mainHand);
    }

    @Override
    public boolean getFullscreen() {
        return settings.optBoolean(FULLSCREEN, false);
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        settings.put(FULLSCREEN, fullscreen);
    }

    @Override
    public float getFOV() {
        return settings.optFloat(FOV, 70f);
    }

    @Override
    public void setFOV(float fov) {
        settings.put(FOV, fov);
    }

    @Override
    public boolean getShowHat() {
        return settings.optBoolean(SKIN_LAYER_HEAD, true);
    }

    @Override
    public void setShowHat(boolean showHat) {
        settings.put(SKIN_LAYER_HEAD, showHat);
    }

    @Override
    public boolean getShowJacket() {
        return settings.optBoolean(SKIN_LAYER_TORSO, true);
    }

    @Override
    public void setShowJacket(boolean showJacket) {
        settings.put(SKIN_LAYER_TORSO, showJacket);
    }

    @Override
    public boolean getShowLeftSleeve() {
        return settings.optBoolean(SKIN_LAYER_LEFT_ARM, true);
    }

    @Override
    public void setShowLeftSleeve(boolean showLeftSleeve) {
        settings.put(SKIN_LAYER_LEFT_ARM, showLeftSleeve);
    }

    @Override
    public boolean getShowRightSleeve() {
        return settings.optBoolean(SKIN_LAYER_RIGHT_ARM, true);
    }

    @Override
    public void setShowRightSleeve(boolean showRightSleeve) {
        settings.put(SKIN_LAYER_RIGHT_ARM, showRightSleeve);
    }

    @Override
    public boolean getShowLeftPantsLeg() {
        return settings.optBoolean(SKIN_LAYER_LEFT_LEG, true);
    }

    @Override
    public void setShowLeftPantsLeg(boolean showLeftPantsLeg) {
        settings.put(SKIN_LAYER_LEFT_LEG, showLeftPantsLeg);
    }

    @Override
    public boolean getShowRightPantsLeg() {
        return settings.optBoolean(SKIN_LAYER_RIGHT_LEG, true);
    }

    @Override
    public void setShowRightPantsLeg(boolean showRightPantsLeg) {
        settings.put(SKIN_LAYER_RIGHT_LEG, showRightPantsLeg);
    }

    @Override
    public int getForwardKeyCode() {
        return settings.optInt(KEY_CODE_FORWARD, 17);
    }

    @Override
    public int getLeftKeyCode() {
        return settings.optInt(KEY_CODE_LEFT, 30);
    }

    @Override
    public int getBackKeyCode() {
        return settings.optInt(KEY_CODE_BACK, 31);
    }

    @Override
    public int getRightKeyCode() {
        return settings.optInt(KEY_CODE_RIGHT, 32);
    }

    @Override
    public int getJumpKeyCode() {
        return settings.optInt(KEY_CODE_JUMP, 57);
    }

    @Override
    public int getSneakKeyCode() {
        return settings.optInt(KEY_CODE_SNEAK, 42);
    }

    @Override
    public int getDropKeyCode() {
        return settings.optInt(KEY_CODE_DROP, 16);
    }

    @Override
    public int getInventoryKeyCode() {
        return settings.optInt(KEY_CODE_INVENTORY, 18);
    }

    @Override
    public int getChatKeyCode() {
        return settings.optInt(KEY_CODE_CHAT, 20);
    }

    @Override
    public int getFogKeyCode() {
        return settings.optInt(KEY_CODE_FOG, 33);
    }

    @Override
    public int getSaveLocationKeyCode() {
        return settings.optInt(KEY_CODE_SAVE_LOCATION, 0);
    }

    @Override
    public int getLoadLocationKeyCode() {
        return settings.optInt(KEY_CODE_LOAD_LOCATION, 0);
    }

    @Override
    public int getBuildMenuKeyCode() {
        return settings.optInt(KEY_CODE_BUILD_MENU, 48);
    }

    @Override
    public void setForwardKeyCode(int keyCode) {
        settings.put(KEY_CODE_FORWARD, keyCode);
    }

    @Override
    public void setLeftKeyCode(int keyCode) {
        settings.put(KEY_CODE_LEFT, keyCode);
    }

    @Override
    public void setBackKeyCode(int keyCode) {
        settings.put(KEY_CODE_BACK, keyCode);
    }

    @Override
    public void setRightKeyCode(int keyCode) {
        settings.put(KEY_CODE_RIGHT, keyCode);
    }

    @Override
    public void setJumpKeyCode(int keyCode) {
        settings.put(KEY_CODE_JUMP, keyCode);
    }

    @Override
    public void setSneakKeyCode(int keyCode) {
        settings.put(KEY_CODE_SNEAK, keyCode);
    }

    @Override
    public void setDropKeyCode(int keyCode) {
        settings.put(KEY_CODE_DROP, keyCode);
    }

    @Override
    public void setInventoryKeyCode(int keyCode) {
        settings.put(KEY_CODE_INVENTORY, keyCode);
    }

    @Override
    public void setChatKeyCode(int keyCode) {
        settings.put(KEY_CODE_CHAT, keyCode);
    }

    @Override
    public void setFogKeyCode(int keyCode) {
        settings.put(KEY_CODE_FOG, keyCode);
    }

    @Override
    public void setSaveLocationKeyCode(int keyCode) {
        settings.put(KEY_CODE_SAVE_LOCATION, keyCode);
    }

    @Override
    public void setLoadLocationKeyCode(int keyCode) {
        settings.put(KEY_CODE_LOAD_LOCATION, keyCode);
    }

    @Override
    public void setBuildMenuKeyCode(int keyCode) {
        settings.put(KEY_CODE_BUILD_MENU, keyCode);
    }

    @Override
    public boolean getAutoJump() {
        return settings.optBoolean(AUTO_JUMP, false);
    }

    @Override
    public void setAutoJump(boolean autoJump) {
        settings.put(AUTO_JUMP, autoJump);
    }

    @Override
    public float getGamma() {
        return settings.optFloat(BRIGHTNESS, 70f);
    }

    @Override
    public void setGamma(float gamma) {
        settings.put(BRIGHTNESS, gamma);
    }

    @Override
    public boolean getClouds() {
        return settings.optBoolean(CLOUDS, true);
    }

    @Override
    public void setClouds(boolean clouds) {
        settings.put(CLOUDS, clouds);
    }

    @Override
    public boolean getServerTextures() {
        return settings.optBoolean(SERVER_TEXTURES, true);
    }

    @Override
    public void setServerTextures(boolean serverTextures) {
        settings.put(SERVER_TEXTURES, serverTextures);
    }

    @Override
    public EMinecraftParticles getParticles() {
        return settings.optEnum(EMinecraftParticles.class, PARTICLES, EMinecraftParticles.ALL);
    }

    @Override
    public void setParticles(EMinecraftParticles particles) {
        settings.put(PARTICLES, particles);
    }

    @Override
    public String getLanguage() {
        return settings.optString(LANGUAGE, "en_US");
    }

    @Override
    public void setLanguage(String language) {
        settings.put(LANGUAGE, language);
    }

    @Override
    public boolean getChatColors() {
        return settings.optBoolean(CHAT_COLORS, true);
    }

    @Override
    public void setChatColors(boolean chatColors) {
        settings.put(CHAT_COLORS, chatColors);
    }

    @Override
    public boolean getChatLinks() {
        return settings.optBoolean(CHAT_LINKS, true);
    }

    @Override
    public void setChatLinks(boolean chatLinks) {
        settings.put(CHAT_LINKS, chatLinks);
    }

    @Override
    public boolean getChatLinksPrompt() {
        return settings.optBoolean(CHAT_LINKS_PROMPT, true);
    }

    @Override
    public void setChatLinksPrompt(boolean chatLinksPrompt) {
        settings.put(CHAT_LINKS_PROMPT, chatLinksPrompt);
    }

    @Override
    public boolean getSnooperEnabled() {
        return settings.optBoolean(SNOOPER_ENABLED, false);
    }

    @Override
    public void setSnooperEnabled(boolean snooperEnabled) {
        settings.put(SNOOPER_ENABLED, snooperEnabled);
    }

    @Override
    public boolean getVsync() {
        return settings.optBoolean(VSYNC_ENABLED, false);
    }

    @Override
    public void setVsync(boolean enableVsync) {
        settings.put(VSYNC_ENABLED, enableVsync);
    }

    @Override
    public boolean getHideServerAddress() {
        return settings.optBoolean(HIDE_SERVER_ADDRESS, false);
    }

    @Override
    public void setHideServerAddress(boolean hideServerAddress) {
        settings.put(HIDE_SERVER_ADDRESS, hideServerAddress);
    }

    @Override
    public boolean getAdvancedItemTooltips() {
        return settings.optBoolean(ADVANCED_ITEM_TOOLTIPS, false);
    }

    @Override
    public void setAdvancedItemTooltips(boolean advancedItemTooltips) {
        settings.put(ADVANCED_ITEM_TOOLTIPS, advancedItemTooltips);
    }

    @Override
    public boolean getPauseOnLostFocus() {
        return settings.optBoolean(PAUSE_ON_FOCUS_LOSS, true);
    }

    @Override
    public void setPauseOnLostFocus(boolean pauseOnLostFocus) {
        settings.put(PAUSE_ON_FOCUS_LOSS, pauseOnLostFocus);
    }

    @Override
    public boolean getShowCape() {
        return settings.optBoolean(SHOW_CAPE, true);
    }

    @Override
    public void setShowCape(boolean showCape) {
        settings.put(SHOW_CAPE, showCape);
    }

    @Override
    public boolean getTouchscreen() {
        return settings.optBoolean(TOUCHSCREEN, false);
    }

    @Override
    public void setTouchscreen(boolean touchscreen) {
        settings.put(TOUCHSCREEN, touchscreen);
    }

    @Override
    public boolean getHeldItemTooltips() {
        return settings.optBoolean(HELD_ITEM_TOOLTIPS, true);
    }

    @Override
    public void setHeldItemTooltips(boolean heldItemTooltips) {
        settings.put(HELD_ITEM_TOOLTIPS, heldItemTooltips);
    }

    @Override
    public EMinecraftChatVisibility getChatVisibility() {
        return settings.optEnum(EMinecraftChatVisibility.class, CHAT_VISIBILITY, EMinecraftChatVisibility.SHOWN);
    }

    @Override
    public void setChatVisibility(EMinecraftChatVisibility chatVisibility) {
        settings.put(CHAT_VISIBILITY, chatVisibility);
    }

    @Override
    public float getChatHeightFocused() {
        return settings.optFloat(CHAT_HEIGHT_FOCUSED, 1);
    }

    @Override
    public void setChatHeightFocused(float chatHeightFocused) {
        settings.put(CHAT_HEIGHT_FOCUSED, chatHeightFocused);
    }

    @Override
    public float getChatHeightUnfocused() {
        return settings.optFloat(CHAT_HEIGHT_UNFOCUSED, 0.44366196f);
    }

    @Override
    public void setChatHeightUnfocused(float chatHeightUnfocused) {
        settings.put(CHAT_HEIGHT_UNFOCUSED, chatHeightUnfocused);
    }

    @Override
    public float getChatScale() {
        return settings.optFloat(CHAT_HEIGHT_UNFOCUSED, 1);
    }

    @Override
    public void setChatScale(float chatScale) {
        settings.put(CHAT_SCALE, chatScale);
    }

    @Override
    public float getChatWidth() {
        return settings.optFloat(CHAT_WIDTH, 1);
    }

    @Override
    public void setChatWidth(float chatWidth) {
        settings.put(CHAT_WIDTH, chatWidth);
    }

    @Override
    public void setAttackKey(int keyCode) {
        settings.put(KEY_CODE_ATTACK, keyCode);
    }

    @Override
    public int getAttackKey() {
        return settings.optInt(KEY_CODE_ATTACK, -100);
    }

    @Override
    public void setUseKey(int keyCode) {
        settings.put(KEY_CODE_USE, keyCode);
    }

    @Override
    public int getUseKey() {
        return settings.optInt(KEY_CODE_USE, -99);
    }

    @Override
    public float getChatOpacity() {
        return settings.optFloat(CHAT_OPACITY, 1);
    }

    @Override
    public void setChatOpacity(float chatOpacity) {
        settings.put(CHAT_OPACITY, chatOpacity);
    }

    @Override
    public void setPickItemKey(int keyCode) {
        settings.put(KEY_CODE_PICK_ITEM, keyCode);
    }

    @Override
    public int getPickItemKey() {
        return settings.optInt(KEY_CODE_PICK_ITEM, -98);
    }

    @Override
    public void setPlayerListKey(int keyCode) {
        settings.put(KEY_CODE_PLAYER_LIST, keyCode);
    }

    @Override
    public int getPlayerListKey() {
        return settings.optInt(KEY_CODE_PLAYER_LIST, 15);
    }

    @Override
    public void setCommandKey(int keyCode) {
        settings.put(KEY_CODE_COMMAND, keyCode);
    }

    @Override
    public int getCommandKey() {
        return settings.optInt(KEY_CODE_COMMAND, 53);
    }
}
