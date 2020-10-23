package gg.codie.mineonline.gui;

import gg.codie.minecraft.api.AuthServer;
import gg.codie.minecraft.api.MojangAPI;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.components.LargeButton;
import gg.codie.mineonline.gui.components.PasswordInputField;
import gg.codie.mineonline.gui.components.InputField;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.mineonline.utils.LastLogin;
import org.json.JSONObject;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class LoginMenuScreen implements IMenuScreen {
    GUIObject logo;
    InputField usernameInput;
    PasswordInputField passwordInput;
    LargeButton loginButton;
    LargeButton playOfflineButton;
    GUIText errorText;
    GUIText needAccount;
    GUIText updateAvailableText;
    GUIText usernameLabel;
    GUIText passwordLabel;
    boolean offline;

    public LoginMenuScreen(boolean _offline) {
        this.offline = _offline;

        String username = "";
        String password = "";
        LastLogin lastLogin = LastLogin.readLastLogin();
        if(lastLogin != null) {
            username = lastLogin.loginUsername;
        }
        usernameInput = new InputField("Username Input", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 204, (DisplayManager.getDefaultHeight() / 2) + 2), username, null);
        passwordInput = new PasswordInputField("Password Input", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 204, (DisplayManager.getDefaultHeight() / 2) + 74 ), password, null);

        RawModel logoModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) -200) + DisplayManager.getXBuffer(), Display.getHeight() - DisplayManager.scaledHeight(69)), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(49)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 40), new Vector2f(400, 49)));
        ModelTexture logoTexture = new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")));
        TexturedModel texuredLogoModel =  new TexturedModel(logoModel, logoTexture);
        logo = new GUIObject("logo", texuredLogoModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        if(offline) {
            playOfflineButton = new LargeButton("Play Offline", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 198, (DisplayManager.getDefaultHeight() / 2) + 172), new IOnClickListener() {
                @Override
                public void onClick() {
                    try {
                        if (usernameInput.getValue().contains("@")) {
                            EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    JOptionPane.showMessageDialog(null, "Enter a username to play in offline-mode.");
                                }
                            });
                            return;
                        }

                        new Session(usernameInput.getValue());
                        MenuManager.setMenuScreen(new MainMenuScreen());
                    } catch (Exception ex) {
                    }
                }
            });
        }

        loginButton = new LargeButton("Login", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 198, (DisplayManager.getDefaultHeight() / 2) + 126), new IOnClickListener() {
            @Override
            public void onClick() {
                try {
                    String clientSecret = UUID.randomUUID().toString();
                    JSONObject login = AuthServer.authenticate(usernameInput.getValue(), passwordInput.getValue(), clientSecret);

                    if (login.has("error"))
                        throw new Exception(login.getString("error"));
                    if (!login.has("accessToken") || !login.has("selectedProfile"))
                        throw new Exception("Failed to authenticate!");
                    if (MojangAPI.minecraftProfile(login.getJSONObject("selectedProfile").getString("name")).optBoolean("demo", false))
                        throw new Exception("Please buy Minecraft to use MineOnline.");

                    String sessionToken = login.getString("accessToken");
                    String uuid = login.getJSONObject("selectedProfile").getString("id");

                    if (sessionToken != null) {
                        new Session(login.getJSONObject("selectedProfile").getString("name"), sessionToken, clientSecret, uuid, true);
                        LastLogin.writeLastLogin(Session.session.getAccessToken(), clientSecret, usernameInput.getValue(), Session.session.getUsername(), Session.session.getUuid());
                        MenuManager.setMenuScreen(new MainMenuScreen());
                    } else {
                        if (errorText != null)
                            errorText.remove();

                        errorText = new GUIText("Incorrect username or password.", 1.5f, TextMaster.minecraftFont, new Vector2f(0, (DisplayManager.getDefaultHeight() / 2) - 60), DisplayManager.getDefaultWidth(), true, true);
                        errorText.setColour(1, 0.33f, 0.33f);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();

                    String errorMessage = ex.getMessage();
                    if (errorMessage.startsWith("Bad login")) {
                        errorMessage = "Incorrect username or password.";
                    }

                    if (ex instanceof IOException)
                        errorMessage = "Bad login.";

                    if (errorText != null)
                        errorText.remove();

                    errorText = new GUIText(errorMessage, 1.5f, TextMaster.minecraftFont, new Vector2f(0, (DisplayManager.getDefaultHeight() / 2) - 100), DisplayManager.getDefaultWidth(), true, true);
                    errorText.setColour(1, 0.33f, 0.33f);

                    offline = true;
                    if(playOfflineButton == null)
                        playOfflineButton = new LargeButton("Play Offline", new Vector2f((DisplayManager.getDefaultWidth() / 2) - 200, (DisplayManager.getDefaultHeight() / 2) + 172), new IOnClickListener() {
                            @Override
                            public void onClick() {
                                try {
                                    if(usernameInput.getValue().contains("@")) {
                                        EventQueue.invokeLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                JOptionPane.showMessageDialog(null, "Enter a username to play in offline-mode.");
                                            }
                                        });
                                        return;
                                    }

                                    new Session(usernameInput.getValue());
                                    MenuManager.setMenuScreen(new MainMenuScreen());
                                } catch (Exception ex) {
                                }
                            }
                        });
                }
            }
        });

        usernameLabel = new GUIText("Username or E-Mail:", 1.5f, TextMaster.minecraftFont, new Vector2f((DisplayManager.getDefaultWidth() / 2) - 202, (DisplayManager.getDefaultHeight() / 2) - 64), 200, false, true);
        passwordLabel = new GUIText("Password:", 1.5f, TextMaster.minecraftFont, new Vector2f((DisplayManager.getDefaultWidth() / 2) - 202, (DisplayManager.getDefaultHeight() / 2) + 4), 200, false, true);

        if(MenuManager.isUpdateAvailable() && !Globals.DEV) {
            updateAvailableText = new GUIText("Update available!", 1.5f, TextMaster.minecraftFont, new Vector2f(0, DisplayManager.getDefaultHeight() - 32), DisplayManager.getDefaultWidth(), true, true);
            updateAvailableText.setColour(1f, 1f, 0f);
        } else {
            needAccount = new GUIText("Need Account?", 1.5f, TextMaster.minecraftFont, new Vector2f(0, DisplayManager.getDefaultHeight() - 40), DisplayManager.getDefaultWidth(), true, true);
            needAccount.setColour(0.33f, 0.33f, 0.66f);
        }
    }

    public void update() {
        if ((usernameInput.getValue().isEmpty() || passwordInput.getValue().isEmpty()) && !loginButton.getDisabled()) {
            loginButton.setDisabled(true);
        } else if (!(usernameInput.getValue().isEmpty() || passwordInput.getValue().isEmpty()) && loginButton.getDisabled()) {
            loginButton.setDisabled(false);
        }

        usernameInput.update();
        passwordInput.update();
        loginButton.update();
        if(playOfflineButton != null)
            playOfflineButton.update();

        int x = Mouse.getX();
        int y = Mouse.getY();

        boolean mouseIsOver =
                x - ((Display.getWidth() / 2) - DisplayManager.scaledWidth(75)) <= DisplayManager.scaledWidth(150)
                        && x - ((Display.getWidth() / 2) - DisplayManager.scaledWidth(75)) >= 0
                        && y - DisplayManager.scaledHeight(18) - DisplayManager.getYBuffer() <= DisplayManager.scaledHeight(22)
                        && y - DisplayManager.scaledHeight(18) - DisplayManager.getYBuffer() >= 0;

        if (MouseHandler.didClick() && mouseIsOver) {
            try {
                if (MenuManager.isUpdateAvailable()) {
                    Desktop.getDesktop().browse(new URI(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/download"));
                } else {
                    Desktop.getDesktop().browse(new URI("https://checkout.minecraft.net/en-us/store/minecraft/#register"));
                }
            } catch (Exception ex) {

            }
        }
    }

    public void render(Renderer renderer) {
        GUIShader.singleton.start();
        GUIShader.singleton.loadViewMatrix(Camera.singleton);
        renderer.prepareGUI();
        renderer.renderGUI(logo, GUIShader.singleton);
        usernameInput.render(renderer, GUIShader.singleton);
        passwordInput.render(renderer, GUIShader.singleton);
        loginButton.render(renderer, GUIShader.singleton);
        if(offline && playOfflineButton != null)
            playOfflineButton.render(renderer, GUIShader.singleton);
        GUIShader.singleton.stop();
    }

    public boolean showPlayer() {
        return false;
    }

    public void resize() {
        usernameInput.resize();
        passwordInput.resize();
        loginButton.resize();
        if(playOfflineButton != null)
            playOfflineButton.resize();
        logo.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) -200) + DisplayManager.getXBuffer(), Display.getHeight() - DisplayManager.scaledHeight(69)), new Vector2f(DisplayManager.scaledWidth(400), DisplayManager.scaledHeight(49)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 40), new Vector2f(400, 49))));
    }

    @Override
    public void cleanUp() {
        if(errorText != null)
            errorText.remove();
        if (needAccount != null)
            needAccount.remove();
        usernameInput.cleanUp();
        passwordInput.cleanUp();
        loginButton.cleanUp();
        usernameLabel.remove();
        passwordLabel.remove();
        needAccount.remove();
        if(playOfflineButton != null)
            playOfflineButton.cleanUp();
        if(updateAvailableText != null)
            updateAvailableText.remove();
    }
}
