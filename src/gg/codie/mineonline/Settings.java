package gg.codie.mineonline;

import gg.codie.minecraft.client.*;
import org.json.JSONObject;
import org.lwjgl.input.Keyboard;
import sun.plugin2.message.LaunchJVMAppletMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;

public class Settings implements IMinecraftOptionsHandler {

    public static Settings singleton;
    private JSONObject settings;

    public static final String SETTINGS_VERSION = "settingsVersion";
    public static final String MINECRAFT_UPDATE_URL = "minecraftUpdateURL";
    public static final String JAVA_HOME = "javaHome";
    public static final String CLIENT_LAUNCH_ARGS = "clientLaunchArgs";
    public static final String HIDE_VERSION_STRING = "hideVersionString";
    public static final String GAME_WIDTH = "gameWidth";
    public static final String GAME_HEIGHT = "gameHeight";
    public static final String CUSTOM_CAPES = "customCapes";

    public static final String SAMPLE_COUNT = "sampleCount";
    public static final String STENCIL_COUNT = "stencilCount";
    public static final String COVERAGE_SAMPLE_COUNT = "coverageSampleCount";

    public static final String FULLSCREEN = "fullscreen";
    public static final String GUI_SCALE = "guiScale";
    public static final String FOV = "fov";
    public static final String MAIN_HAND = "mainHand";
    public static final String TEXTURE_PACK = "texturePack";
    public static final String MUSIC = "music";
    public static final String SOUND = "sound";
    public static final String INVERT_Y_MOUSE = "invertYMouse";
    public static final String MOUSE_SENSITIVITY = "mouseSensitivity";
    public static final String RENDER_DISTANCE = "viewDistance";
    public static final String VIEW_BOBBING = "viewBobbing";
    public static final String ANAGLYPH_3D = "anaglyph3d";
    public static final String ADVANCED_OPEN_GL = "advancedOpengl";
    public static final String PERFORMANCE = "performance";
    public static final String DIFFICULTY = "difficulty";
    public static final String FANCY_GRAPHICS = "fancyGraphics";
    public static final String SMOOTH_LIGHTING = "smoothLighting";
    public static final String LAST_SERVER = "lastServer";
    public static final String KEY_CODE_FORWARD = "keyCodeForward";
    public static final String KEY_CODE_LEFT = "keyCodeLeft";
    public static final String KEY_CODE_BACK = "keyCodeBack";
    public static final String KEY_CODE_RIGHT = "keyCodeRight";
    public static final String KEY_CODE_JUMP = "keyCodeJump";
    public static final String KEY_CODE_SNEAK = "keyCodeSneak";
    public static final String KEY_CODE_DROP = "keyCodeDrop";
    public static final String KEY_CODE_INVENTORY = "keyCodeInventory";
    public static final String KEY_CODE_CHAT = "keyCodeChat";
    public static final String KEY_CODE_FOG = "keyCodeFog";
    public static final String SKIN_LAYER_HEAD = "skinLayerHead";
    public static final String SKIN_LAYER_TORSO = "skinLayerTorso";
    public static final String SKIN_LAYER_LEFT_ARM = "skinLayerLeftArm";
    public static final String SKIN_LAYER_RIGHT_ARM = "skinLayerRightArm";
    public static final String SKIN_LAYER_LEFT_LEG = "skinLayerLeftLeg";
    public static final String SKIN_LAYER_RIGHT_LEG = "skinLayerRightLeg";


    private static final int SETTINGS_VERSION_NUMBER = 10;

    private static boolean readonly = true;

    private Settings() {

    }

    static {
        try {
            Class.forName("org.json.JSONObject");

            readonly = false;

            singleton = new Settings();

            if (new File(LauncherFiles.MINEONLINE_SETTINGS_FILE).exists()) {
                singleton.loadSettings();
            } else {
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
        settings.put(MINECRAFT_UPDATE_URL, "");
        settings.put(JAVA_HOME, "");
        settings.put(CLIENT_LAUNCH_ARGS, "");
        settings.put(FOV, 70);
        settings.put(GUI_SCALE, 3);
        settings.put(TEXTURE_PACK, "");
        settings.put(HIDE_VERSION_STRING, false);
        settings.put(GAME_WIDTH, 854);
        settings.put(GAME_HEIGHT, 480);
        settings.put(CUSTOM_CAPES, true);
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
        settings.put(RENDER_DISTANCE, ELegacyMinecraftRenderDistance.FAR.getIntValue());
        settings.put(MAIN_HAND, EMinecraftMainHand.RIGHT.getStringValue());
        settings.put(VIEW_BOBBING, true);
        settings.put(ANAGLYPH_3D, false);
        settings.put(ADVANCED_OPEN_GL, true);
        settings.put(PERFORMANCE, EMinecraftPerformance.BALANCED.getIntValue());
        settings.put(DIFFICULTY, EMinecraftDifficulty.NORMAL.getIntValue());
        settings.put(FANCY_GRAPHICS, true);
        settings.put(SMOOTH_LIGHTING, true);
        settings.put(LAST_SERVER, "");
        settings.put(SKIN_LAYER_RIGHT_LEG, true);
        settings.put(SKIN_LAYER_LEFT_LEG, true);
        settings.put(SKIN_LAYER_RIGHT_ARM, true);
        settings.put(SKIN_LAYER_LEFT_ARM, true);
        settings.put(SKIN_LAYER_TORSO, true);
        settings.put(SKIN_LAYER_HEAD, true);

        saveSettings();
        loadSettings();
    }

    public void loadMinecraftOptions() {
        try {
            File optionsFile = new File(LauncherFiles.MINEONLINE_OPTIONS_PATH);
            if (!optionsFile.exists())
                return;

            Options options = new Options(LauncherFiles.MINEONLINE_OPTIONS_PATH);

            try {
                setMusicVolume(options.getMusicVolume());
            } catch (NoSuchFieldException ex) {
                // ignore.
            }
            try {
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
        } catch (IOException ex) {
            // ignore
        }
    }

    public void saveMinecraftOptions() {
        try {
            File optionsFile = new File(LauncherFiles.MINEONLINE_OPTIONS_PATH);
            if (!optionsFile.exists())
                optionsFile.createNewFile();

            Options options = new Options(LauncherFiles.MINEONLINE_OPTIONS_PATH);

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
        } catch (Exception ex) {
            // ignore
        }
    }

    public void loadSettings() {
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

            loadMinecraftOptions();

            // Assume V1, reset settings.
            if (!settings.has(SETTINGS_VERSION)) {
                resetSettings();
                saveSettings();
            } else {
                switch (settings.getInt(SETTINGS_VERSION)) {
                    case 3:
                        settings.put(MINECRAFT_UPDATE_URL, "");
                        settings.put(JAVA_HOME, "");
                        settings.put(CLIENT_LAUNCH_ARGS, "");
                    case 4:
                        settings.put(FOV, 70);
                        settings.put(GUI_SCALE, 3);
                    case 5:
                        settings.put(TEXTURE_PACK, "");
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
                        settings.put(RENDER_DISTANCE, ELegacyMinecraftRenderDistance.FAR.getIntValue());
                        settings.put(MAIN_HAND, EMinecraftMainHand.RIGHT.getStringValue());
                        settings.put(VIEW_BOBBING, true);
                        settings.put(ANAGLYPH_3D, false);
                        settings.put(ADVANCED_OPEN_GL, true);
                        settings.put(PERFORMANCE, EMinecraftPerformance.BALANCED.getIntValue());
                        settings.put(DIFFICULTY, EMinecraftDifficulty.NORMAL.getIntValue());
                        settings.put(FANCY_GRAPHICS, true);
                        settings.put(SMOOTH_LIGHTING, true);
                        settings.put(LAST_SERVER, "");
                        settings.put(SKIN_LAYER_RIGHT_LEG, true);
                        settings.put(SKIN_LAYER_LEFT_LEG, true);
                        settings.put(SKIN_LAYER_RIGHT_ARM, true);
                        settings.put(SKIN_LAYER_LEFT_ARM, true);
                        settings.put(SKIN_LAYER_TORSO, true);
                        settings.put(SKIN_LAYER_HEAD, true);
                }
                settings.put(SETTINGS_VERSION, SETTINGS_VERSION_NUMBER);
            }

            if (settings.has("redirectedDomains"))
                settings.remove("redirectedDomains");

            saveSettings();
        } catch (IOException ex) {
            saveSettings();
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
                for (int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char) buffer[i]);
                }
            }

            input.close();

            settings = new JSONObject(stringBuffer.toString());
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public String getMinecraftUpdateURL() {
        return settings.optString(MINECRAFT_UPDATE_URL, null);
    }

    public String getJavaHome() {
        return settings.optString(JAVA_HOME, null);
    }

    public String getClientLaunchArgs() {
        return settings.optString(CLIENT_LAUNCH_ARGS, null);
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
        return ELegacyMinecraftRenderDistance.values()[settings.optInt(RENDER_DISTANCE, 0)];
    }

    @Override
    public void setRenderDistance(ELegacyMinecraftRenderDistance renderDistance) {
        settings.put(RENDER_DISTANCE, renderDistance.getIntValue());
    }

    @Override
    public EMinecraftGUIScale getGUIScale() {
        return EMinecraftGUIScale.values()[settings.optInt(GUI_SCALE, 0)];
    }

    @Override
    public void setGUIScale(EMinecraftGUIScale guiScale) {
        settings.put(GUI_SCALE, guiScale.getIntValue());
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
    public EMinecraftPerformance getPerformance() {
        return EMinecraftPerformance.values()[settings.optInt(PERFORMANCE, 0)];
    }

    @Override
    public void setPerformance(EMinecraftPerformance performance) {
        settings.put(PERFORMANCE, performance.getIntValue());
    }

    @Override
    public EMinecraftDifficulty getDifficulty() {
        return EMinecraftDifficulty.values()[settings.optInt(DIFFICULTY, 0)];
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
        return EMinecraftMainHand.fromString(settings.getString(MAIN_HAND));
    }

    @Override
    public void setMainHand(EMinecraftMainHand mainHand) {
        settings.put(MAIN_HAND, mainHand.getStringValue());
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
        return settings.optInt(KEY_CODE_FORWARD, 0);
    }

    @Override
    public int getLeftKeyCode() {
        return settings.optInt(KEY_CODE_LEFT, 0);
    }

    @Override
    public int getBackKeyCode() {
        return settings.optInt(KEY_CODE_BACK, 0);
    }

    @Override
    public int getRightKeyCode() {
        return settings.optInt(KEY_CODE_RIGHT, 0);
    }

    @Override
    public int getJumpKeyCode() {
        return settings.optInt(KEY_CODE_JUMP, 0);
    }

    @Override
    public int getSneakKeyCode() {
        return settings.optInt(KEY_CODE_SNEAK, 0);
    }

    @Override
    public int getDropKeyCode() {
        return settings.optInt(KEY_CODE_DROP, 0);
    }

    @Override
    public int getInventoryKeyCode() {
        return settings.optInt(KEY_CODE_INVENTORY, 0);
    }

    @Override
    public int getChatKeyCode() {
        return settings.optInt(KEY_CODE_CHAT, 0);
    }

    @Override
    public int getFogKeyCode() {
        return settings.optInt(KEY_CODE_FOG, 0);
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
}
