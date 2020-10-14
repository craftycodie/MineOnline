package gg.codie.mineonline.gui;

import gg.codie.minecraft.api.AuthServer;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.gui.components.LargeButton;
import gg.codie.mineonline.gui.components.PasswordInputField;
import gg.codie.mineonline.gui.components.SmallInputField;
import gg.codie.mineonline.gui.components.InputField;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.utils.LastLogin;
import org.json.JSONObject;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.net.URI;

public class LoginMenuScreen implements IMenuScreen {
    GUIObject logo;
    InputField usernameInput;
    PasswordInputField passwordInput;
    LargeButton loginButton;
    LargeButton playOfflineButton;
    GUIText errorText;
    GUIText needAccount;
    GUIText usernameLabel;
    GUIText passwordLabel;
    boolean offline;

    public LoginMenuScreen(boolean _offline) {
        this.offline = _offline;

        String username = "";
        String password = "";
        LastLogin lastLogin = LastLogin.readLastLogin();
        if(lastLogin != null) {
            username = lastLogin.username;
            password = lastLogin.password;
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
                    JSONObject login = Globals.USE_MOJANG_API
                            ? AuthServer.authenticate(usernameInput.getValue(), passwordInput.getValue())
                            : MineOnlineAPI.authenticate(usernameInput.getValue(), passwordInput.getValue());

                    if (login.has("error"))
                        throw new Exception(login.getString("error"));
                    if (!login.has("accessToken") || !login.has("selectedProfile"))
                        throw new Exception("Failed to authenticate!");
                    if (login.has("selectedProfile") && !login.getJSONObject("selectedProfile").optBoolean("paid", false))
                        throw new Exception("Please buy Minecraft to use MineOnline.");

                    String sessionToken = login.getString("accessToken");
                    String uuid = login.getJSONObject("selectedProfile").getString("id");

                    if (sessionToken != null) {
                        new Session(login.getJSONObject("selectedProfile").getString("name"), sessionToken, uuid, true);
                        LastLogin.writeLastLogin(usernameInput.getValue(), Globals.USE_MOJANG_API ? null : passwordInput.getValue(), uuid);
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
                    if (errorMessage.startsWith("Bad authenticate")) {
                        errorMessage = "Incorrect username or password.";
                    }

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


        needAccount = new GUIText("Need Account?", 1.5f, TextMaster.minecraftFont, new Vector2f(0, DisplayManager.getDefaultHeight() - 40), DisplayManager.getDefaultWidth(), true, true);
        needAccount.setColour(0.33f, 0.33f, 0.66f);
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

        if(MouseHandler.didClick() && mouseIsOver) {
            try {
                Desktop.getDesktop().browse(new URI(Globals.USE_MOJANG_API
                        ? "https://checkout.minecraft.net/en-us/store/minecraft/#register"
                        : Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/register"));
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
        needAccount.remove();
        usernameInput.cleanUp();
        passwordInput.cleanUp();
        loginButton.cleanUp();
        usernameLabel.remove();
        passwordLabel.remove();
        needAccount.remove();
        if(playOfflineButton != null)
            playOfflineButton.cleanUp();
    }
}
