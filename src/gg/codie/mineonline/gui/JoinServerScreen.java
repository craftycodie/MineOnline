package gg.codie.mineonline.gui;

import gg.codie.mineonline.MinecraftLauncher;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.components.InputField;
import gg.codie.mineonline.gui.rendering.components.LargeButton;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
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
    GUIText label;


    public JoinServerScreen() {
        serverIPField = new InputField("Server IP Input", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) - 40), Properties.properties.has("lastServer") ? Properties.properties.getString("lastServer") : "", new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    Properties.properties.put("lastServer", serverIPField.getValue());
                    Properties.saveProperties();
                    String[] split = serverIPField.getValue().split(":");
                    String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), split[0], split.length > 1 ? split[1] : "25565");

                    //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\b1.7.3-modded.jar", null, null, null).startMinecraft();
                    new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\b1.7.3.jar", split[0], split.length > 1 ? split[1] : "25565", mppass).startMinecraft();

                    //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\c0.0.11a-launcher.jar", null, null, null).startMinecraft();
                } catch (Exception ex) {}
            }
        });

        aboutButton = new LargeButton("Version: b1.7.3", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 8), null);

        logoutButton = new LargeButton("Connect", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 66), new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    Properties.properties.put("lastServer", serverIPField.getValue());
                    Properties.saveProperties();
                    String[] split = serverIPField.getValue().split(":");
                    String mppass = MinecraftAPI.getMpPass(Session.session.getSessionToken(), split[0], split.length > 1 ? split[1] : "25565");

                    //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\b1.7.3-modded.jar", null, null, null).startMinecraft();
                    new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\b1.7.3.jar", split[0], split.length > 1 ? split[1] : "25565", mppass).startMinecraft();

                    //new MinecraftLauncher("D:\\Projects\\GitHub\\MineOnline\\jars\\c0.0.11a-launcher.jar", null, null, null).startMinecraft();
                } catch (Exception ex) {}
            }
        });

        doneButton = new LargeButton("Cancel", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, DisplayManager.getDefaultHeight() - 20), new IOnClickListener() {
            @Override
            public void onClick() {
                PlayerRendererTest.setMenuScreen(new MainMenuScreen());
            }
        });

        label = new GUIText("Play Multiplayer", 1.5f, TextMaster.minecraftFont, new Vector2f(0, 40), DisplayManager.getDefaultWidth(), true);
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
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        serverIPField.resize();
        logoutButton.resize();
        aboutButton.resize();
        doneButton.resize();
    }

    @Override
    public void cleanUp() {
        label.remove();
        serverIPField.cleanUp();
        logoutButton.cleanUp();
        aboutButton.cleanUp();
        doneButton.cleanUp();
    }
}
