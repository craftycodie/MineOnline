package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.utils.JREUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.Map;

public class GuiIngameMenu extends AbstractGuiScreen
{
    public void initGui()
    {
        controlList.clear();
        byte byte0 = -16;
        controlList.add(new GuiButton(1, getWidth() / 2 - 100, getHeight() / 4 + 120 + byte0, "Save and quit to launcher", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                try {
                    LinkedList<String> launchArgs = new LinkedList();
                    launchArgs.add(JREUtils.getRunningJavaExecutable());
                    launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
                    launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
                    launchArgs.add("-cp");
                    launchArgs.add(LibraryManager.getClasspath(true, true, new String[]{new File(MenuManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(), LauncherFiles.DISCORD_RPC_JAR}));
                    launchArgs.add(MenuManager.class.getCanonicalName());

                    java.util.Properties props = System.getProperties();
                    ProcessBuilder processBuilder = new ProcessBuilder(launchArgs);

                    Map<String, String> env = processBuilder.environment();
                    for (String prop : props.stringPropertyNames()) {
                        env.put(prop, props.getProperty(prop));
                    }
                    processBuilder.directory(new File(System.getProperty("user.dir")));

                    Process launcherProcess = processBuilder.inheritIO().start();

                    LegacyGameManager.closeGame();
                } catch (Exception ex) {
                    // ignore for now.
                }
            }
        }));

        controlList.add(new GuiButton(4, getWidth() / 2 - 100, getHeight() / 4 + 24 + byte0, "Back to game", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                LegacyGameManager.setGUIScreen(null);
            }
        }));

        AbstractGuiScreen thisScreen = this;

        controlList.add(new GuiButton(0, getWidth() / 2 - 100, getHeight() / 4 + 96 + byte0, "Options...", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                LegacyGameManager.setGUIScreen(new GuiOptions(thisScreen));
            }
        }));

        if (LegacyGameManager.getVersion() == null || LegacyGameManager.getVersion().useTexturepackPatch) {
            controlList.add(new GuiButton(5, getWidth() / 2 - 100, getHeight() / 4 + 48 + byte0, 98, 20, "Multiplayer", new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    LegacyGameManager.setGUIScreen(new GuiMultiplayer(thisScreen));
                }
            }));
            controlList.add(new GuiButton(6, getWidth() / 2 + 2, getHeight() / 4 + 48 + byte0, 98, 20, "Texture Packs", new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    LegacyGameManager.setGUIScreen(new GuiTexturePacks(thisScreen));
                }
            }));
        } else {
            controlList.add(new GuiButton(5, getWidth() / 2 - 100, getHeight() / 4 + 48 + byte0, "Multiplayer", new GuiButton.GuiButtonListener() {
                @Override
                public void OnButtonPress() {
                    LegacyGameManager.setGUIScreen(new GuiMultiplayer(thisScreen));
                }
            }));
        }

    }

    public void updateScreen()
    {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        controlList.clear();
        initGui();

        drawDefaultBackground();
        Font.minecraftFont.drawCenteredStringWithShadow("MineOnline menu", getWidth() / 2, 40, 0xffffff);
        super.drawScreen(mouseX, mouseY);
    }
}
