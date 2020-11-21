package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.client.gui.Tessellator;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.FontRenderer;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import gg.codie.mineonline.gui.sound.ClickSound;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class GuiMainMenu extends AbstractGuiScreen
{
    public GuiMainMenu()
    {
        splashText = "missingno";
        try
        {
            ArrayList arraylist = new ArrayList();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader((GuiMainMenu.class).getResourceAsStream("/title/splashes.txt"), Charset.forName("UTF-8")));
            String s = "";
            do
            {
                String s1;
                if((s1 = bufferedreader.readLine()) == null)
                {
                    break;
                }
                s1 = s1.trim();
                if(s1.length() > 0)
                {
                    arraylist.add(s1);
                }
            } while(true);
            do
            {
                splashText = (String)arraylist.get(rand.nextInt(arraylist.size()));
            } while(splashText.hashCode() == 0x77f432f);
        }
        catch(Exception exception) { }

        initGui();
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        if (MenuManager.isUpdateAvailable() && j > getHeight() - 20 && j < getHeight() - 10 && i < FontRenderer.minecraftFontRenderer.getStringWidth("Update Available!")) {
            ClickSound.play();
            try {
                if (Globals.BRANCH.equalsIgnoreCase("release"))
                    Desktop.getDesktop().browse(new URI(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/download"));
                else
                    Desktop.getDesktop().browse(new URI("https://github.com/codieradical/MineOnline/releases"));
            } catch (Exception ex) {

            }
        }

        if (j > getHeight() - 10 && j < getHeight() && i > getWidth() - FontRenderer.minecraftFontRenderer.getStringWidth("Made by @codieradical <3")) {
            ClickSound.play();
            try {
                Desktop.getDesktop().browse(new URI("https://twitter.com/codieradical"));
            } catch (Exception ex) {

            }
        }
    }

    public void initGui()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if(calendar.get(2) + 1 == 11 && calendar.get(5) == 9)
        {
            splashText = "Happy birthday, ez!";
        } else
        if(calendar.get(2) + 1 == 6 && calendar.get(5) == 1)
        {
            splashText = "Happy birthday, Notch!";
        } else
        if(calendar.get(2) + 1 == 12 && calendar.get(5) == 24)
        {
            splashText = "Merry X-mas!";
        } else
        if(calendar.get(2) + 1 == 1 && calendar.get(5) == 1)
        {
            splashText = "Happy new year!";
        }
        int i = getHeight() / 4 + 48;

        AbstractGuiScreen thisScreen = this;

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
                    false));
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

    public void resizeGui() {
        controlList.get(0).resize(getWidth() / 2 - 100, getHeight() / 4 + 48);
        controlList.get(1).resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 24);
        controlList.get(2).resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 48);
        controlList.get(3).resize(getWidth() / 2 - 100, getHeight() / 4 + 48 + 72);
    }

    public void drawScreen(int i, int j)
    {
        resizeGui();

        Tessellator tessellator = Tessellator.instance;
        char c = '\u0112';
        int k = getWidth() / 2 - c / 2;
        byte byte0 = 30;
//        drawGradientRect(0, 0, getWidth(), getHeight(), 0x55ffffff, 0xffffff);
//        drawGradientRect(0, 0, getWidth(), getHeight(), 0, 0x55000000);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, Loader.singleton.getGuiTexture(EGUITexture.MINEONLINE_LOGO));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, 10241, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, 10240, GL11.GL_NEAREST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(k + 0, byte0 + 0, 0, 0, 155, 44);
        drawTexturedModalRect(k + 155, byte0 + 0, 0, 45, 155, 44);
        tessellator.setColorOpaque_I(0xffffff);
        GL11.glPushMatrix();
        GL11.glTranslatef(getWidth() / 2 + 90, 70F, 0.0F);
        GL11.glRotatef(-20F, 0.0F, 0.0F, 1.0F);
        float f1 = 1.8F - Math.abs((float)Math.sin(((float)(System.currentTimeMillis() % 1000L) / 1000F) * 3.141593F * 2.0F) * 0.1F);
        f1 = (f1 * 100F) / (float)(FontRenderer.minecraftFontRenderer.getStringWidth(splashText) + 32);
        GL11.glScalef(f1, f1, f1);
        drawCenteredString(splashText, 0, -8, 0xffff00);
        GL11.glPopMatrix();
        if (MenuManager.isUpdateAvailable())
            drawString("Update Available!", 2, getHeight() - 20, 0xffff00);
        drawString("MineOnline " + (Globals.DEV ? "Dev " : "") + Globals.LAUNCHER_VERSION + (!Globals.BRANCH.equalsIgnoreCase("release") ? " (" + Globals.BRANCH + ")" : ""), 2, getHeight() - 10, 0xffffff);
        String s = "Made by @codieradical <3";
        drawString(s, getWidth() - FontRenderer.minecraftFontRenderer.getStringWidth(s) - 2, getHeight() - 10, 0xffffff);
        super.drawScreen(i, j);
    }

    private static final Random rand = new Random();
    private String splashText;
    private GuiButton multiplayerButton;
}
