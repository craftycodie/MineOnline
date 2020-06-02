package gg.codie.mineonline.gui;

import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.animation.IdlePlayerAnimation;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
import gg.codie.mineonline.gui.rendering.components.MediumButton;
import gg.codie.mineonline.gui.rendering.components.SelectableVersion;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

public class SelectVersionMenuScreen implements IMenuScreen {
    GUIObject background;
    MediumButton doneButton;
    MediumButton browseButton;

    SelectableVersion betaTest;

    public SelectVersionMenuScreen() {
        RawModel backgroundModel = Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1)));
        ModelTexture backgroundTexture = new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")));
        TexturedModel texuredBackgroundModel =  new TexturedModel(backgroundModel, backgroundTexture);
        background = new GUIObject("background", texuredBackgroundModel, new Vector3f(0, 10, 0), new Vector3f(), new Vector3f(1, 1, 1));

        doneButton = new MediumButton("Done", new Vector2f((DisplayManager.getDefaultWidth() / 2) + 8, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
            }
        });

        browseButton = new MediumButton("Browse...", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 8 - 300, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
//                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
            }
        });

        betaTest = new SelectableVersion("beta test", new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) - 220) + DisplayManager.getXBuffer(), 180), "b1.7.3", "D:\\Projects\\GitHub\\MineOnline\\jars\\b1.7.3.jar" );
    }

    public void update() {
        doneButton.update();
        browseButton.update();
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        renderer.renderGUI(background, guiShader);
        guiShader.stop();

        doneButton.render(renderer, guiShader);
        browseButton.render(renderer, guiShader);
        betaTest.render(renderer, guiShader);

        renderer.renderCenteredString(new Vector2f(DisplayManager.getDefaultWidth() / 2, 50), "Select Version", Color.white); //x, y, string to draw, color
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        background.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));
        doneButton.resize();
        browseButton.resize();
        betaTest.resize();
    }
}
