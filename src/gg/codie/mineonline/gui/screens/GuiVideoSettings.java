package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.client.options.EMinecraftGUIScale;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.client.options.EMineOnlineAntiAliasing;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiSlider;
import gg.codie.mineonline.gui.components.GuiSmallButton;
import gg.codie.mineonline.gui.rendering.FontRenderer;

public class GuiVideoSettings extends AbstractGuiScreen
{

    public GuiVideoSettings(AbstractGuiScreen guiscreen)
    {
        screenName = "Options";
        parent = guiscreen;
        initGui();
    }

    public void initGui()
    {
        AbstractGuiScreen thisScreen = this;

        controlList.add(doneButton = new GuiButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, "Done", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                Settings.singleton.saveSettings();
                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parent);
                else
                    MenuManager.setMenuScreen(parent);
            }
        }));

        controlList.add(antiAliasingButton = new GuiSmallButton(0, getWidth() / 2 - 155, getHeight() / 6, "Anti-Aliasing: " + Settings.singleton.getAntiAliasing().getName(), new GuiSlider.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                EMineOnlineAntiAliasing newAntiAliasing;
                if (Settings.singleton.getAntiAliasing().ordinal() == EMinecraftGUIScale.values().length) {
                    newAntiAliasing = EMineOnlineAntiAliasing.values()[0];
                } else {
                    newAntiAliasing = EMineOnlineAntiAliasing.values()[Settings.singleton.getAntiAliasing().ordinal() + 1];
                }

                Settings.singleton.setAntiAliasing(newAntiAliasing);
                antiAliasingButton.displayString = "Anti-Aliasing: " + Settings.singleton.getAntiAliasing().getName();
            }
        }));

        controlList.add(mipmapsButton = new GuiSmallButton(0, getWidth() / 2 + 5, getHeight() / 6, "Mipmaps: " + (Settings.singleton.getMipmaps() ? "ON" : "OFF"), new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                Settings.singleton.setMipmaps(!Settings.singleton.getMipmaps());
                mipmapsButton.displayString = "Mipmaps: " + (Settings.singleton.getMipmaps() ? "ON" : "OFF");
            }
        }));

        controlList.add(anisotropicFilteringButton = new GuiSmallButton(0, getWidth() / 2 - 155, getHeight() / 6 + 24, "Anisotropic Filtering: " + (Settings.singleton.getAnisotropicFiltering() + "x"), new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                int anisotropicFiltering = Settings.singleton.getAnisotropicFiltering();
                if (anisotropicFiltering < 4) {
                    anisotropicFiltering += 1;
                } else {
                    anisotropicFiltering = 0;
                }
                Settings.singleton.setAnisotropicFiltering(anisotropicFiltering);
                anisotropicFilteringButton.displayString = "Anisotropic Filtering: " + Settings.singleton.getAnisotropicFiltering() + "x";
            }
        }));
    }

    public void resize() {
        doneButton.resize(getWidth() / 2 - 100, getHeight() / 6 + 168);
        antiAliasingButton.resize(getWidth() / 2 - 155, getHeight() / 6);
        mipmapsButton.resize(getWidth() / 2 + 5, getHeight() / 6);
        anisotropicFilteringButton.resize(getWidth() / 2 - 155, getHeight() / 6 + 24);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY)
    {
        resize();

        drawDefaultBackground();

        FontRenderer.minecraftFontRenderer.drawString("These settings are experimental, and not compatible with all GPUs.", getWidth() / 2 - 160, 128, 0xffffff);
//        FontRenderer.minecraftFontRenderer.drawString("", getWidth() / 2 - 140, 140, 0xffffff);

        FontRenderer.minecraftFontRenderer.drawCenteredString(screenName, getWidth() / 2, 20, 0xffffff);
        super.drawScreen(mouseX, mouseY);
    }

    private GuiButton doneButton;
    private GuiButton antiAliasingButton;
    private GuiButton mipmapsButton;
    private GuiButton anisotropicFilteringButton;

    private AbstractGuiScreen parent;
    private String screenName;
}
