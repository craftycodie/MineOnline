package gg.codie.minecraft.client.options;

public interface IMinecraftOptionsHandler {
    float getMusicVolume() throws NoSuchFieldException;
    void setMusicVolume(float volume);

    float getSoundVolume() throws NoSuchFieldException;
    void setSoundVolume(float volume);

    boolean getShowFPS() throws NoSuchFieldException;
    void setShowFPS(boolean showFPS);

    boolean getInvertYMouse() throws NoSuchFieldException;
    void setInvertYMouse(boolean invertYMouse);

    float getMouseSensitivity() throws NoSuchFieldException;
    void setMouseSensitivity(float sensitivity);

    ELegacyMinecraftRenderDistance getRenderDistance() throws NoSuchFieldException;
    void setRenderDistance(ELegacyMinecraftRenderDistance renderDistance);

    EMinecraftGUIScale getGUIScale() throws NoSuchFieldException;
    void setGUIScale(EMinecraftGUIScale guiScale);

    boolean getViewBobbing() throws NoSuchFieldException;
    void setViewBobbing(boolean viewBobbing);

    boolean get3DAnaglyph() throws NoSuchFieldException;
    void set3DAnalyhph(boolean analyhph);

    boolean getAdvancedOpenGL() throws NoSuchFieldException;
    void setAdvancedOpenGL(boolean advancedOpenGL);

    boolean getLimitFramerate() throws NoSuchFieldException;
    void setLimitFramerate(boolean limitFramerate);

    EMinecraftPerformance getPerformance() throws NoSuchFieldException;
    void setPerformance(EMinecraftPerformance performance);

    EMinecraftDifficulty getDifficulty() throws NoSuchFieldException;
    void setDifficulty(EMinecraftDifficulty difficulty);

    boolean getFancyGraphics() throws NoSuchFieldException;
    void setFancyGraphics(boolean fancyGraphics);

    boolean getSmoothLighting() throws NoSuchFieldException;
    void setSmoothLighting(boolean smoothLighting);

    String getTexturePack() throws NoSuchFieldException;
    void setTexturePack(String texturePack);

    String getLastServer() throws NoSuchFieldException;
    void setLastServer(String lastServer);

    EMinecraftMainHand getMainHand() throws NoSuchFieldException;
    void setMainHand(EMinecraftMainHand mainHand);

    boolean getFullscreen() throws NoSuchFieldException;
    void setFullscreen(boolean fullscreen);

    float getFOV() throws NoSuchFieldException;
    void setFOV(float fov);

    boolean getShowHat() throws NoSuchFieldException;
    void setShowHat(boolean showHat);

    boolean getShowJacket() throws NoSuchFieldException;
    void setShowJacket(boolean showJacket);

    boolean getShowLeftSleeve() throws NoSuchFieldException;
    void setShowLeftSleeve(boolean showLeftSleeve);

    boolean getShowRightSleeve() throws NoSuchFieldException;
    void setShowRightSleeve(boolean showRightSleeve);

    boolean getShowLeftPantsLeg() throws NoSuchFieldException;
    void setShowLeftPantsLeg(boolean showLeftPantsLeg);

    boolean getShowRightPantsLeg() throws NoSuchFieldException;
    void setShowRightPantsLeg(boolean showRightPantsLeg);

    // Key Binds
    int getForwardKeyCode() throws NoSuchFieldException;
    int getLeftKeyCode() throws NoSuchFieldException;
    int getBackKeyCode() throws NoSuchFieldException;
    int getRightKeyCode() throws NoSuchFieldException;
    int getJumpKeyCode() throws NoSuchFieldException;
    int getSneakKeyCode() throws NoSuchFieldException;
    int getDropKeyCode() throws NoSuchFieldException;
    int getInventoryKeyCode() throws NoSuchFieldException;
    int getChatKeyCode() throws NoSuchFieldException;
    int getFogKeyCode() throws NoSuchFieldException;
    int getSaveLocationKeyCode() throws NoSuchFieldException;
    int getLoadLocationKeyCode() throws NoSuchFieldException;
    int getBuildMenuKeyCode() throws NoSuchFieldException;

    void setForwardKeyCode(int keyCode);
    void setLeftKeyCode(int keyCode);
    void setBackKeyCode(int keyCode);
    void setRightKeyCode(int keyCode);
    void setJumpKeyCode(int keyCode);
    void setSneakKeyCode(int keyCode);
    void setDropKeyCode(int keyCode);
    void setInventoryKeyCode(int keyCode);
    void setChatKeyCode(int keyCode);
    void setFogKeyCode(int keyCode);
    void setSaveLocationKeyCode(int keyCode);
    void setLoadLocationKeyCode(int keyCode);
    void setBuildMenuKeyCode(int keyCode);

    // RELEASE

    boolean getAutoJump() throws NoSuchFieldException;
    void setAutoJump(boolean autoJump);
}
