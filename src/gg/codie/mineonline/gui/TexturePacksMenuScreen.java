package gg.codie.mineonline.gui;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.components.MediumButton;
import gg.codie.mineonline.gui.components.SelectableTexturePackList;
import gg.codie.mineonline.gui.components.TinyButton;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.Camera;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class TexturePacksMenuScreen implements IMenuScreen {
    MediumButton doneButton;
    MediumButton browseButtonBig;
    TinyButton browseButtonSmall;
    TinyButton backButton;
    GUIText label;
    GUIText browseInfoLabel;

    SelectableTexturePackList selectableTexturePackList;

    public TexturePacksMenuScreen() {
        doneButton = new MediumButton("Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                Settings.singleton.setTexturePack(selectableTexturePackList.getSelected());
                Settings.singleton.saveSettings();
                MenuManager.setMenuScreen(new MainMenuScreen());
            }
        });

        IOnClickListener browseListener = new IOnClickListener() {
            @Override
            public void onClick() {
                Sys.openURL((new StringBuilder()).append("file://").append(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH).toString());
            }
        };

        browseButtonBig = new MediumButton("Open texture pack folder", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 308, DisplayManager.getDefaultHeight() - 20), browseListener);

        selectableTexturePackList = new SelectableTexturePackList("texture packs list", new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1), new IOnClickListener() {
            @Override
            public void onClick() {
                Settings.singleton.setTexturePack(selectableTexturePackList.getSelected());
                Settings.singleton.saveSettings();
                MenuManager.setMenuScreen(new MainMenuScreen());
            }
        });

        label = new GUIText("Select Texture Pack", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true, true);
        browseInfoLabel = new GUIText("(Place texture pack files here)", 1.5f, TextMaster.minecraftFont, new Vector2f((DisplayManager.getDefaultWidth() / 2) - 358, DisplayManager.getDefaultHeight() - 22), DisplayManager.scaledWidth(400), true, true);
        browseInfoLabel.setColour(0.7f, 0.7f, 0.7f);
    }

    public void update() {
        doneButton.update();
        if(browseButtonBig != null)
            browseButtonBig.update();
        if(browseButtonSmall != null)
            browseButtonSmall.update();
        if(backButton != null)
            backButton.update();
        selectableTexturePackList.update();
    }

    public void render(Renderer renderer) {
        GUIShader.singleton.start();
        GUIShader.singleton.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();

        doneButton.render(renderer, GUIShader.singleton);
        if(browseButtonBig != null)
            browseButtonBig.render(renderer, GUIShader.singleton);
        if(browseButtonSmall != null)
            browseButtonSmall.render(renderer, GUIShader.singleton);
        if(backButton != null)
            backButton.render(renderer, GUIShader.singleton);
        selectableTexturePackList.render(renderer, GUIShader.singleton);
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        doneButton.resize();
        if(browseButtonBig != null)
            browseButtonBig.resize();
        if(browseButtonSmall != null)
            browseButtonSmall.resize();
        if(backButton != null)
            backButton.resize();
        selectableTexturePackList.resize();
    }

    @Override
    public void cleanUp() {
        doneButton.cleanUp();
        if(browseButtonBig != null)
            browseButtonBig.cleanUp();
        if(browseButtonSmall != null)
            browseButtonSmall.cleanUp();
        if(backButton != null)
            backButton.cleanUp();
        selectableTexturePackList.cleanUp();
        label.remove();
        browseInfoLabel.remove();
    }
}
