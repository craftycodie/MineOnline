package gg.codie.mineonline.gui;

import gg.codie.mineonline.MinecraftLauncher;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.components.InputField;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;

public class JoinServerScreen implements IMenuScreen {
    InputField serverIPField;
    LargeButton aboutButton;
    LargeButton logoutButton;
    LargeButton doneButton;


    public JoinServerScreen() {
        serverIPField = new InputField("Server IP Input", new Vector2f((Display.getWidth() / 2) - 200, (Display.getHeight() / 2) - 40), Properties.properties.has("lastServer") ? Properties.properties.getString("lastServer") : "");

        aboutButton = new LargeButton("Version: b1.7.3", new Vector2f((Display.getWidth() / 2) - 200, (Display.getHeight() / 2) + 8), null);

        logoutButton = new LargeButton("Connect", new Vector2f((Display.getWidth() / 2) - 200, Display.getHeight() - 66), new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    String[] split = serverIPField.getValue().split(":");
                    String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), split[0], split.length > 1 ? split[1] : "25565");

                    //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\b1.7.3-modded.jar", null, null, null).startMinecraft();
                    new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\b1.7.3.jar", split[0], split.length > 1 ? split[1] : "25565", mppass).startMinecraft();

                    //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\c0.0.11a-launcher.jar", null, null, null).startMinecraft();
                } catch (Exception ex) {}
            }
        });

        doneButton = new LargeButton("Done", new Vector2f((Display.getWidth() / 2) - 200, Display.getHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
            }
        });
    }

    public void update() {
        serverIPField.update();
        aboutButton.update();
        logoutButton.update();
        doneButton.update();
    }

    public void render(Renderer renderer) {
        GUIShader guiShader = new GUIShader();
        guiShader.start();
        guiShader.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        serverIPField.render(renderer, guiShader);
        aboutButton.render(renderer, guiShader);
        logoutButton.render(renderer, guiShader);
        doneButton.render(renderer, guiShader);
        guiShader.stop();

        renderer.renderCenteredString(new Vector2f(Display.getWidth() / 2, 50), "Play Multiplayer", Color.white); //x, y, string to draw, color
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        //serverIPField.resize();
        logoutButton.resize();
        aboutButton.resize();
        doneButton.resize();
    }
}
