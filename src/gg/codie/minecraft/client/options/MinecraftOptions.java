package gg.codie.minecraft.client.options;

import java.io.*;
import java.util.LinkedList;

public class MinecraftOptions implements IMinecraftOptionsHandler {
    String path;
    EMinecraftOptionsVersion optionsVersion;

    public MinecraftOptions(String path, String optionsVersionStr) throws IOException, IllegalArgumentException {
        EMinecraftOptionsVersion optionsVersion = EMinecraftOptionsVersion.valueOf(optionsVersionStr);

        if(!new File(path).exists()) {
            new File(path).createNewFile();
        }
        this.path = path;
        this.optionsVersion = optionsVersion;
    }

    public MinecraftOptions(String path, EMinecraftOptionsVersion optionsVersion) throws IOException {
        if(!new File(path).exists()) {
            new File(path).createNewFile();
        }
        this.path = path;
        this.optionsVersion = optionsVersion;
    }

    private void setOption(String name, String value) throws IOException {
        LinkedList<String> lines = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, false))) {
            boolean foundExisting = false;
            for (String line : lines) {
                if (line.startsWith(name + ":")) {
                    line = name + ":" + value;
                    foundExisting = true;
                }
                bw.write(line);
                bw.newLine();
            }
            if(!foundExisting) {
                bw.write(name + ":" + value);
                bw.newLine();
            }
        }
    }

    private String getOption(String name) throws NoSuchFieldException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith(name + ":")) {
                    return line.replace(name + ":", "");
                }
            }
        }
        throw new NoSuchFieldException(name + " not found.");
    }

    @Override
    public float getMusicVolume() throws NoSuchFieldException {
        try {
            switch (optionsVersion) {
                case CLASSIC:
                    return getOption("music").equalsIgnoreCase("false") ? 0f : 1f;
                default:
                case DEFAULT:
                    return Float.parseFloat(getOption("music"));
            }
        } catch (IOException | NumberFormatException ex) {
            return 1;
        }
    }

    @Override
    public void setMusicVolume(float volume) {
        try {
            switch (optionsVersion) {
                case CLASSIC:
                    setOption("music", volume > 0 ? "true" : "false");
                    break;
                default:
                case DEFAULT:
                    setOption("music", "" + volume);
            }
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public float getSoundVolume() throws NoSuchFieldException {
        try {
            switch (optionsVersion) {
                case CLASSIC:
                    return getOption("sound").equalsIgnoreCase("false") ? 0f : 1f;
                default:
                case DEFAULT:
                    return Float.parseFloat(getOption("sound"));
            }
        } catch (IOException | NumberFormatException ex) {
            return 1;
        }
    }

    @Override
    public void setSoundVolume(float volume) {
        try {
            switch (optionsVersion) {
                case CLASSIC:
                    setOption("sound", volume > 0 ? "true" : "false");
                    break;
                default:
                case DEFAULT:
                    setOption("sound", "" + volume);
            }        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getShowFPS() throws NoSuchFieldException {
        try {
            return getOption("showFrameRate").equalsIgnoreCase("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setShowFPS(boolean showFrameRate) {
        try {
            setOption("showFrameRate", showFrameRate ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getLimitFramerate() throws NoSuchFieldException {
        try {
            return getOption("limitFramerate").equalsIgnoreCase("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setLimitFramerate(boolean limitFramerate) {
        try {
            setOption("limitFramerate", limitFramerate ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getInvertYMouse() throws NoSuchFieldException {
        try {
            return getOption("invertYMouse").equalsIgnoreCase("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setInvertYMouse(boolean invertYMouse) {
        try {
            setOption("invertYMouse", invertYMouse ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public float getMouseSensitivity() throws NoSuchFieldException {
        try {
            return Float.parseFloat(getOption("mouseSensitivity"));
        } catch (IOException | NumberFormatException ex) {
            return 1;
        }
    }

    @Override
    public void setMouseSensitivity(float sensitivity) {
        try {
            setOption("mouseSensitivity", "" + sensitivity);
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public ELegacyMinecraftRenderDistance getRenderDistance() throws NoSuchFieldException {
        try {
            return ELegacyMinecraftRenderDistance.values()[Integer.parseInt(getOption("viewDistance"))];
        } catch (IOException | NumberFormatException ex) {
            return ELegacyMinecraftRenderDistance.FAR;
        }
    }

    @Override
    public void setRenderDistance(ELegacyMinecraftRenderDistance renderDistance) {
        try {
            setOption("viewDistance", "" + renderDistance.getIntValue());
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public EMinecraftGUIScale getGUIScale() throws NoSuchFieldException {
        try {
            return EMinecraftGUIScale.values()[Integer.parseInt(getOption("guiScale"))];
        } catch (IOException | NumberFormatException ex) {
            return EMinecraftGUIScale.AUTO;
        }
    }

    @Override
    public void setGUIScale(EMinecraftGUIScale guiScale) {
        try {
            setOption("guiScale", "" + guiScale.getIntValue());
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getViewBobbing() throws NoSuchFieldException {
        try {
            return getOption("bobView").equalsIgnoreCase("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setViewBobbing(boolean viewBobbing) {
        try {
            setOption("bobView", viewBobbing ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean get3DAnaglyph() throws NoSuchFieldException {
        try {
            return getOption("anaglyph3d").equalsIgnoreCase("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void set3DAnalyhph(boolean analyhph) {
        try {
            setOption("anaglyph3d", analyhph ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getAdvancedOpenGL() throws NoSuchFieldException {
        try {
            return getOption("advancedOpengl").equalsIgnoreCase("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setAdvancedOpenGL(boolean advancedOpenGL) {
        try {
            setOption("advancedOpengl", advancedOpenGL ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public EMinecraftPerformance getPerformance() throws NoSuchFieldException {
        try {
            return EMinecraftPerformance.values()[Integer.parseInt(getOption("fpsLimit"))];
        } catch (IOException | NumberFormatException ex) {
            return EMinecraftPerformance.BALANCED;
        }
    }

    @Override
    public void setPerformance(EMinecraftPerformance performance) {
        try {
            setOption("fpsLimit", "" + performance.getIntValue());
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public EMinecraftDifficulty getDifficulty() throws NoSuchFieldException {
        try {
            return EMinecraftDifficulty.values()[Integer.parseInt(getOption("difficulty"))];
        } catch (IOException | NumberFormatException ex) {
            return EMinecraftDifficulty.NORMAL;
        }
    }

    @Override
    public void setDifficulty(EMinecraftDifficulty difficulty) {
        try {
            setOption("difficulty", "" + difficulty.getIntValue());
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getFancyGraphics() throws NoSuchFieldException {
        try {
            return getOption("fancyGraphics").equals("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setFancyGraphics(boolean fancyGraphics) {
        try {
            setOption("fancyGraphics", fancyGraphics ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getSmoothLighting() throws NoSuchFieldException {
        try {
            return getOption("ao").equals("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setSmoothLighting(boolean smoothLighting) {
        try {
            setOption("ao", smoothLighting ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public String getTexturePack() throws NoSuchFieldException {
        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
                throw new NoSuchFieldException("No skin in this version");
            default:
        }

        try {
            return getOption("skin").isEmpty() ? "Default" : getOption("skin");
        } catch (IOException ex) {
            return "";
        }
    }

    @Override
    public void setTexturePack(String texturePack) {
        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
                return;
            default:
        }

        if (texturePack.equals("Default"))
            texturePack = "";

        try {
            setOption("skin", texturePack);
        } catch (IOException ex) {
            ex.printStackTrace();
            // ignore
        }
    }

    @Override
    public String getLastServer() throws NoSuchFieldException {
        try {
            return getOption("lastServer").replace("_", ":");
        } catch (IOException ex) {
            return "";
        }
    }

    @Override
    public void setLastServer(String lastServer) {
        if (lastServer == null || lastServer.isEmpty())
            return;

        try {
            setOption("lastServer", lastServer.replace(":", "_"));
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public EMinecraftMainHand getMainHand() throws NoSuchFieldException {
        try {
            return EMinecraftMainHand.fromString(getOption("mainHand"));
        } catch (IOException ex) {
            return EMinecraftMainHand.RIGHT;
        }
    }

    @Override
    public void setMainHand(EMinecraftMainHand mainHand) {
        try {
            setOption("mainHand", mainHand.getStringValue());
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getFullscreen() throws NoSuchFieldException {
        try {
            return getOption("fullscreen").equals("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        try {
            setOption("fullscreen", fullscreen ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public float getFOV() throws NoSuchFieldException {
        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
            case PREFOV:
                throw new NoSuchFieldException("No fov in this version");
            default:
        }

        try {
            return 70 + (40 * Float.parseFloat(getOption("fov")));
        } catch (IOException ex) {
            return 70;
        }
    }

    @Override
    public void setFOV(float fov) {
        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
            case PREFOV:
                return;
            default:
        }

        try {
            setOption("fov", "" + ((fov - 70) / 40));
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getShowHat() throws NoSuchFieldException {
        try {
            return getOption("modelPart_hat").equals("true");
        } catch (IOException ex) {
            return true;
        }
    }

    @Override
    public void setShowHat(boolean showHat) {
        try {
            setOption("modelPart_hat", showHat ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getShowJacket() throws NoSuchFieldException {
        try {
            return getOption("modelPart_jacket").equals("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setShowJacket(boolean showJacket) {
        try {
            setOption("modelPart_jacket", showJacket ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getShowLeftSleeve() throws NoSuchFieldException {
        try {
            return getOption("modelPart_left_sleeve").equals("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setShowLeftSleeve(boolean showLeftSleeve) {
        try {
            setOption("modelPart_left_sleeve", showLeftSleeve ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getShowRightSleeve() throws NoSuchFieldException {
        try {
            return getOption("modelPart_right_sleeve").equals("true");
        } catch (IOException ex) {
            return false;
        }        }

    @Override
    public void setShowRightSleeve(boolean showRightSleeve) {
        try {
            setOption("modelPart_right_sleeve", showRightSleeve ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getShowLeftPantsLeg() throws NoSuchFieldException {
        try {
            return getOption("modelPart_left_pants_leg").equals("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setShowLeftPantsLeg(boolean showLeftPantsLeg) {
        try {
            setOption("modelPart_left_pants_leg", showLeftPantsLeg ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public boolean getShowRightPantsLeg() throws NoSuchFieldException {
        try {
            return getOption("modelPart_right_pants_leg").equals("true");
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void setShowRightPantsLeg(boolean showRightPantsLeg) {
        try {
            setOption("modelPart_right_pants_leg", showRightPantsLeg ? "true" : "false");
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public int getForwardKeyCode() throws NoSuchFieldException {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Forward";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.forward";
        }

        try {
            return Integer.parseInt(getOption(keyName));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getLeftKeyCode() throws NoSuchFieldException {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Left";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.left";
        }

        try {
            return Integer.parseInt(getOption(keyName));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getBackKeyCode() throws NoSuchFieldException {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Back";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.back";
        }

        try {
            return Integer.parseInt(getOption(keyName));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getRightKeyCode() throws NoSuchFieldException {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Right";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.right";
        }

        try {
            return Integer.parseInt(getOption(keyName));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getJumpKeyCode() throws NoSuchFieldException {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Jump";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.jump";
        }

        try {
            return Integer.parseInt(getOption(keyName));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getSneakKeyCode() throws NoSuchFieldException {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Sneak";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.sneak";
        }

        try {
            return Integer.parseInt(getOption(keyName));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getDropKeyCode() throws NoSuchFieldException {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Drop";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.drop";
        }

        try {
            return Integer.parseInt(getOption(keyName));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getInventoryKeyCode() throws NoSuchFieldException {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Inventory";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.inventory";
        }

        try {
            return Integer.parseInt(getOption(keyName));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getChatKeyCode() throws NoSuchFieldException {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Chat";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.chat";
        }

        try {
            return Integer.parseInt(getOption(keyName));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getFogKeyCode() throws NoSuchFieldException {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Toggle fog";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.fog";
        }

        try {
            return Integer.parseInt(getOption(keyName));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getSaveLocationKeyCode() throws NoSuchFieldException {
        try {
            return Integer.parseInt(getOption("key_Save location"));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getLoadLocationKeyCode() throws NoSuchFieldException {
        try {
            return Integer.parseInt(getOption("key_Load location"));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int getBuildMenuKeyCode() throws NoSuchFieldException {
        try {
            return Integer.parseInt(getOption("key_Build"));
        } catch (IOException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public void setForwardKeyCode(int keyCode) {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Forward";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.forward";
        }

        try {
            setOption(keyName, "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public void setLeftKeyCode(int keyCode) {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Left";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.left";
        }

        try {
            setOption(keyName, "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public void setBackKeyCode(int keyCode) {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Back";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.back";
        }

        try {
            setOption(keyName, "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public void setRightKeyCode(int keyCode) {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Right";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.right";
        }

        try {
            setOption(keyName, "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public void setJumpKeyCode(int keyCode) {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Jump";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.jump";
        }

        try {
            setOption(keyName, "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public void setSneakKeyCode(int keyCode) {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Sneak";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.sneak";
        }

        try {
            setOption(keyName, "" + keyCode);
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public void setDropKeyCode(int keyCode) {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Drop";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.drop";
        }

        try {
            setOption(keyName, "" + keyCode);
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public void setInventoryKeyCode(int keyCode) {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Inventory";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.inventory";
        }

        try {
            setOption(keyName, "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public void setChatKeyCode(int keyCode) {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Chat";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.chat";
        }

        try {
            setOption(keyName, "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public void setFogKeyCode(int keyCode) {
        String keyName;

        switch (optionsVersion) {
            case CLASSIC:
            case PRESKINS:
            case ALPHA2:
                keyName = "key_Toggle fog";
                break;
            case DEFAULT:
            default:
                keyName = "key_key.fog";
        }

        try {
            setOption(keyName, "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public void setSaveLocationKeyCode(int keyCode) {
        try {
            setOption("key_Save location", "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public void setLoadLocationKeyCode(int keyCode) {
        try {
            setOption("key_Load location", "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public void setBuildMenuKeyCode(int keyCode) {
        try {
            setOption("key_Build", "" + keyCode);
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }

    @Override
    public boolean getAutoJump() throws NoSuchFieldException {
        try {
            return Boolean.parseBoolean(getOption("autoJump"));
        } catch (IOException | NumberFormatException ex) {
            return false;
        }
    }

    @Override
    public void setAutoJump(boolean autoJump) {
        try {
            setOption("autoJump", autoJump ? "true": "false");
        } catch (IOException | NumberFormatException ex) {
            // ignore
        }
    }
}
