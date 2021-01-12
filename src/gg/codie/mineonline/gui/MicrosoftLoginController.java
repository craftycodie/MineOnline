package gg.codie.mineonline.gui;

import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.screens.GuiMainMenu;
import gg.codie.mineonline.utils.LastLogin;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MicrosoftLoginController extends VBox {

    private static final String loginUrl = "https://login.live.com/oauth20_authorize.srf" +
            "?client_id=00000000402b5328" +
            "&response_type=code" +
            "&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL" +
            "&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";

    private static final String redirectUrlSuffix = "https://login.live.com/oauth20_desktop.srf?code=";

    private static final String authTokenUrl = "https://login.live.com/oauth20_token.srf";

    private static final String xblAuthUrl = "https://user.auth.xboxlive.com/user/authenticate";

    private static final String xstsAuthUrl = "https://xsts.auth.xboxlive.com/xsts/authorize";

    private static final String mcLoginUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";

    private static final String mcStoreUrl = "https://api.minecraftservices.com/entitlements/mcstore";

    private static final String mcProfileUrl = "https://api.minecraftservices.com/minecraft/profile";

    void reset() {
        if (webView != null)
            this.getChildren().remove(webView);

        MicrosoftLoginController thisController = this;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView = new WebView();
                thisController.getChildren().add(webView);

                java.net.CookieHandler.setDefault(new java.net.CookieManager());

                webView.getEngine().load(loginUrl);
                webView.getEngine().setJavaScriptEnabled(true);
                webView.setPrefHeight(600);
                webView.setPrefWidth(500);

                // listen to end oauth flow
                webView.getEngine().getHistory().getEntries().addListener((ListChangeListener<WebHistory.Entry>) c -> {
                    if (c.next() && c.wasAdded()) {
                        for (WebHistory.Entry entry : c.getAddedSubList()) {
                            if (entry.getUrl().startsWith(redirectUrlSuffix)) {
                                String authCode = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));
                                // once we got the auth code, we can turn it into a oauth token
                                System.out.println("authCode: " + authCode); // TODO debug
                                acquireAccessToken(authCode);
                            }
                        }
                    }
                    if (c.wasAdded() && webView.getEngine().getLocation().contains("oauth20_desktop.srf?error=access_denied")) {
                        frame.dispose();
                        Display.getParent().getParent().setVisible(true);
                    }
                });
            }
        });
    }

    JFrame frame;
    WebView webView;

    public MicrosoftLoginController() {
        try {
            frame = new JFrame("MineOnline Login");
            JFXPanel jfxPanel = new JFXPanel();
            frame.add(jfxPanel);
            frame.pack();
            frame.setVisible(true);
            jfxPanel.setScene(new Scene(this));
            frame.setSize(500, 600);
            frame.setLocationRelativeTo(null);
            Display.getParent().getParent().setVisible(false);
            Image img = Toolkit.getDefaultToolkit().getImage(DisplayManager.class.getResource("/img/favicon.png"));
            frame.setIconImage(img);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    Display.getParent().getParent().setVisible(true);
                }
            });

            reset();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void acquireAccessToken(String authcode) {
        try {
            URL url = new URL(authTokenUrl);

            Map<Object, Object> data = new HashMap<>();

            data.put("client_id", "00000000402b5328");
            data.put("code", authcode);
            data.put("grant_type", "authorization_code");
            data.put("redirect_uri", "https://login.live.com/oauth20_desktop.srf");
            data.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.getOutputStream().write(ofFormData(data).getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            JSONObject jsonObject = new JSONObject(response.toString());

            System.out.println(jsonObject);
            String accessToken = (String) jsonObject.get("access_token");
            System.out.println("accessToken: " + accessToken); // TODO debug
            acquireXBLToken(accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acquireXBLToken(String accessToken) {
        try {
            URL uri = new URL(xblAuthUrl);

            JSONObject data = new JSONObject();
            JSONObject properties = new JSONObject();

            properties.put("AuthMethod", "RPS");
            properties.put("SiteName", "user.auth.xboxlive.com");
            properties.put("RpsTicket", accessToken);

            data.put("Properties", properties);
            data.put("RelyingParty", "http://auth.xboxlive.com");
            data.put("TokenType", "JWT");

            HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            System.out.println(jsonObject);
            String xblToken = (String) jsonObject.get("Token");
            acquireXsts(xblToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acquireXsts(String xblToken) {
        try {
            URL uri = new URL(xstsAuthUrl);

            JSONObject data = new JSONObject();
            JSONObject properties = new JSONObject();

            properties.put("SandboxId", "RETAIL");
            properties.put("UserTokens", new String[] { xblToken });

            data.put("Properties", properties);
            data.put("RelyingParty", "rp://api.minecraftservices.com/");
            data.put("TokenType", "JWT");


            HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            String xblXsts = (String) jsonObject.get("Token");
            JSONObject claims = (JSONObject) jsonObject.get("DisplayClaims");
            JSONArray xui = (JSONArray) claims.get("xui");
            String uhs = (String) ((JSONObject) xui.get(0)).get("uhs");
            System.out.println("xblXsts: " + xblXsts + ", uhs: " + uhs); // TODO debug
            acquireMinecraftToken(uhs, xblXsts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acquireMinecraftToken(String xblUhs, String xblXsts) {
        try {
            URL uri = new URL(mcLoginUrl);

            JSONObject data = new JSONObject();
            data.put("identityToken", "XBL3.0 x=" + xblUhs + ";" + xblXsts);

            HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            String mcAccessToken = (String) jsonObject.get("access_token");
            System.out.println("mcAccessToken: " + mcAccessToken); // TODO debug
            checkMcStore(mcAccessToken);
            checkMcProfile(mcAccessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkMcStore(String mcAccessToken) {
        try {
            URL uri = new URL(mcStoreUrl);

            HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            String body = response.toString();
            System.out.println("store: " + body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkMcProfile(String mcAccessToken) {
        try {
            URL uri = new URL(mcProfileUrl);

            HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            if(connection.getResponseCode() == 404) {
                JOptionPane.showMessageDialog(null, "This Microsoft account does not own Minecraft.");
                reset();
            }

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            String name = (String) jsonObject.get("name");
            String uuid = (String) jsonObject.get("id");

            new Session(name, mcAccessToken, "", uuid, true);
            LastLogin.writeLastLogin(Session.session.getAccessToken(), "", null, Session.session.getUsername(), Session.session.getUuid());
            frame.dispose();
            Display.getParent().getParent().setVisible(true);
            MenuManager.setMenuScreen(new GuiMainMenu());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String ofFormData(Map<Object, Object> data) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), "UTF-8"));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }
        return builder.toString();
    }
}
