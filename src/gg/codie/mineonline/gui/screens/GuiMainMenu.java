package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.SplashMessage;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.sound.ClickSound;
import gg.codie.mineonline.gui.textures.EGUITexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class GuiMainMenu extends AbstractGuiScreen
{
    public GuiMainMenu()
    {
        splashText = SplashMessage.getSplashMessage();
        initGui();
    }

    protected void mouseClicked(int x, int y, int button)
    {
        super.mouseClicked(x, y, button);
        if (MenuManager.isUpdateAvailable() && y > getHeight() - 20 && y < getHeight() - 10 && x < Font.minecraftFont.width("Update Available!")) {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/craftycodie/MineOnline/releases/latest"));
            } catch (Exception ex) {

            }
        }

        if (y > getHeight() - 10 && y < getHeight() && x > getWidth() - Font.minecraftFont.width("Made by @craftycodie <3")) {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI("https://twitter.com/craftycodie"));
            } catch (Exception ex) {

            }
        }
    }

    public void initGui()
    {
        int i = getHeight() / 4 + 48;

        AbstractGuiScreen thisScreen = this;

        MinecraftVersion selectedVersion = MinecraftVersionRepository.getSingleton().getVersion(MinecraftVersionRepository.getSingleton().getLastSelectedJarPath());
        boolean selectedVersionIsRelease = selectedVersion == null || selectedVersion.name.startsWith("Release") || selectedVersion.name.startsWith("Snapshot");

        controlList.add(new GuiButton(1, getWidth() / 2 - 100, i, "Singleplayer", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                MenuManager.setMenuScreen(new GuiVersions(thisScreen, null, new GuiVersions.IVersionSelectListener() {
                    @Override
                    public void onSelect(String path) {
                        try {
                            MinecraftVersionRepository.getSingleton().selectJar(path);
                            MinecraftVersion.launchMinecraft(path, null, null, null);
                            Display.destroy();
                            DisplayManager.getFrame().dispose();
                            System.exit(0);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            // ignore for now
                        }
                }
                }, new GuiSlotVersion.ISelectableVersionCompare() {
                    @Override
                    public boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion) {
                        return MinecraftVersionRepository.getSingleton().getLastSelectedJarPath() != null && MinecraftVersionRepository.getSingleton().getLastSelectedJarPath().equals(selectableVersion.path);
                    }
                },
                    false, selectedVersionIsRelease));
            }
        }));
        controlList.add(multiplayerButton = new GuiButton(2, getWidth() / 2 - 100, i + 24, "Multiplayer", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                MenuManager.setMenuScreen(new GuiMultiplayer(thisScreen));
            }
        }));

        controlList.add(new GuiButton(3, getWidth() / 2 - 100, i + 48, "Mods and Texture Packs", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                MenuManager.setMenuScreen(new GuiTexturePacks(thisScreen));
            }
        }));
        controlList.add(new GuiButton(0, getWidth() / 2 - 100, i + 72, "Options...", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                MenuManager.setMenuScreen(new GuiOptions(thisScreen));
            }
        }));
    }

    public void resize() {
        controlList.get(0).resize(getWidth() / 2 - 100, getHeight() / 4 + 48);
        controlList.get(1).resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 24);
        controlList.get(2).resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 48);
        controlList.get(3).resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 72);
    }

    public void drawScreen(int mouseX, int mouseY)
    {
        resize();

        Renderer tessellator = Renderer.singleton;
        int k = (int)(((getWidth() / 2) - (90 * 1.6)) / 1.6f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.MINEONLINE_LOGO));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, 10241, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, 10240, GL11.GL_NEAREST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glScalef(1.6f, 1.6f, 1);
        Renderer.singleton.drawSprite(k, 22, 0, 0, 180, 180);
        GL11.glScalef(0.625f, 0.625f, 1);
        tessellator.setColorRGBA(255, 255, 255, 255);
        GL11.glPushMatrix();
        GL11.glTranslatef(getWidth() / 2 + 90, 70F, 0.0F);
        GL11.glRotatef(-20F, 0.0F, 0.0F, 1.0F);
        float f1 = 1.8F - Math.abs((float)Math.sin(((float)(System.currentTimeMillis() % 1000L) / 1000F) * 3.141593F * 2.0F) * 0.1F);
        f1 = (f1 * 100F) / (float)(Font.minecraftFont.width(splashText) + 32);
        GL11.glScalef(f1, f1, f1);
        Font.minecraftFont.drawCenteredStringWithShadow(splashText, 0, -8, 0xffff00);
        GL11.glPopMatrix();
        if (MenuManager.isUpdateAvailable())
            Font.minecraftFont.drawString("Update Available!", 2, getHeight() - 20, 0xffff00);
        Font.minecraftFont.drawString("MineOnline " + (Globals.DEV ? "Dev " : "") + Globals.LAUNCHER_VERSION + (!Globals.BRANCH.equalsIgnoreCase("release") ? " (" + Globals.BRANCH + ")" : ""), 2, getHeight() - 10, 0xffffff);
        String s = "Made by @craftycodie <3";
        Font.minecraftFont.drawString(s, getWidth() - Font.minecraftFont.width(s) - 2, getHeight() - 10, 0xffffff);
        super.drawScreen(mouseX, mouseY);
    }

    private static final Random rand = new Random();
    private String splashText;
    private GuiButton multiplayerButton;
}
