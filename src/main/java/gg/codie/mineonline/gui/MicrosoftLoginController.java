package gg.codie.mineonline.gui;

import gg.codie.mineonline.Session;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.screens.GuiMainMenu;
import gg.codie.mineonline.utils.LastLogin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MicrosoftLoginController {
    private static final String xblAuthUrl = "https://user.auth.xboxlive.com/user/authenticate";

    private static final String xstsAuthUrl = "https://xsts.auth.xboxlive.com/xsts/authorize";

    private static final String mcLoginUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";

    private static final String mcStoreUrl = "https://api.minecraftservices.com/entitlements/mcstore";

    private static final String mcProfileUrl = "https://api.minecraftservices.com/minecraft/profile";

    private static final String deviceCodeUrl = "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode";

    private static final String loginPollUrl = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";

    private static final String clientId = "e16fd7c7-cd93-4467-81ad-7fddf0ed1483";

    private static MicrosoftLoginController singleton = new MicrosoftLoginController();

    public static void loadDeviceCode() {
        singleton.reset();
        singleton.deviceCode();
    }

    String userCode;
    String verificationUrl;
    String deviceCode;
    String error;

    boolean isLoggingIn;

    Thread loginPollThread;

    private void reset() {
        deviceCode = null;
        userCode = null;
        verificationUrl = null;
        error = null;
        isLoggingIn = false;
        loginPollThread = new Thread(){
            public void run(){
            while (isLoggingIn && error == null) {
                deviceCodeLoginPoll();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            interrupt();
            }
        };
    }

    public static String getLoginCode() {
        return singleton.userCode;
    }

    public static String getVerificationUrl() {
        return singleton.verificationUrl;
    }

    public static String getError() {
        return singleton.error;
    }

    private void deviceCode() {
        try {
            URL url = new URL(deviceCodeUrl);

            Map<Object, Object> data = new HashMap<>();

            data.put("client_id", clientId);
            data.put("scope", "XboxLive.signin offline_access");

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.getOutputStream().write(ofFormData(data).getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            InputStream is = connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            if (connection.getResponseCode() >= 400) {
                System.out.println(response);
                error = "Failed to retrieve login code!";
                return;
            }

            JSONObject jsonObject = new JSONObject(response.toString());

            userCode = jsonObject.getString("user_code");
            verificationUrl = jsonObject.getString("verification_uri");
            deviceCode = jsonObject.getString("device_code");

            isLoggingIn = true;
            loginPollThread.start();
        } catch (UnknownHostException e) {
            error = "Failed to contact Microsoft. Are you offline?";
            isLoggingIn = false;
            e.printStackTrace();
        } catch (Exception e) {
            error = "Something went wrong!";
            isLoggingIn = false;
            e.printStackTrace();
        }
    }

    private void deviceCodeLoginPoll() {
        try {
            URL url = new URL(loginPollUrl);

            Map<Object, Object> data = new HashMap<>();

            data.put("grant_type", "urn:ietf:params:oauth:grant-type:device_code");
            data.put("client_id", clientId);
            data.put("device_code", deviceCode);

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.getOutputStream().write(ofFormData(data).getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            InputStream is = connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            JSONObject jsonObject = new JSONObject(response.toString());

            if (jsonObject.optString("error", "").equals("authorization_pending")) {
                return;
            }

            if (jsonObject.optString("error", "").equals("authorization_declined")) {
                error = "Login was cancelled.";
                isLoggingIn = false;
                return;
            }

            if (jsonObject.optString("error", "").equals("bad_verification_code")) {
                error = "Something went wrong!";
                isLoggingIn = false;
                return;
            }

            if (jsonObject.optString("error", "").equals("expired_token")) {
                error = "Login has expired.";
                isLoggingIn = false;
                return;
            }

            if (connection.getResponseCode() != 200) {
                error = "Something went wrong!";
                isLoggingIn = false;
                System.out.println(response);
                return;
            }

            singleton.isLoggingIn = false;

            String accessToken = (String) jsonObject.get("access_token");
            acquireXBLToken(accessToken);
        } catch (UnknownHostException e) {
            error = "Failed to contact Microsoft. Are you offline?";
            isLoggingIn = false;
            e.printStackTrace();
        } catch (Exception e) {
            error = "Something went wrong!";
            isLoggingIn = false;
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
            properties.put("RpsTicket", "d=" + accessToken);

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

            InputStream is = connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            if (connection.getResponseCode() != 200) {
                error = "Failed to login to Xbox Live";
                isLoggingIn = false;
                System.out.println(response);
                return;
            }

            JSONObject jsonObject = new JSONObject(response.toString());
            String xblToken = (String) jsonObject.get("Token");
            acquireXsts(xblToken);
        } catch (UnknownHostException e) {
            error = "Failed to contact Xbox Live. Are you offline?";
            isLoggingIn = false;
            e.printStackTrace();
        } catch (Exception e) {
            error = "Something went wrong!";
            isLoggingIn = false;
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

            try {
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
                acquireMinecraftToken(uhs, xblXsts);
            } catch (IOException e) {
                InputStream is = connection.getErrorStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                if (jsonObject.has("XErr")) {
                    long errorCode = jsonObject.getLong("XErr");
                    if (errorCode ==  2148916233L) {
                        error = "You are not signed up with Xbox.\nPlease login to minecraft.net to continue.";
                    } else if (errorCode == 2148916238L) {
                        error = "Account holder is under 18.\nPlease add this account to a family to continue.";
                    } else if (errorCode == 2148916235L) {
                        error = "Account is registered in a country where Xbox Live is not available.";
                    } else if (errorCode == 2148916236L) {
                        error = "You must complete Adult verification on the Xbox Live homepage.";
                    } else if (errorCode == 2148916237L) {
                        error = "You must complete Age verification on the Xbox Live homepage.";
                    } else {
                        error = "Failed to login to Xbox Live";
                        System.out.println(response);
                    }
                } else {
                    error = "Failed to login to Xbox Live";
                    System.out.println(response);
                }
            }
        } catch (UnknownHostException e) {
            error = "Failed to contact Xbox Live. Are you offline?";
            isLoggingIn = false;
            e.printStackTrace();
        } catch (Exception e) {
            error = "Something went wrong!";
            isLoggingIn = false;
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
            checkMcStore(mcAccessToken);
            checkMcProfile(mcAccessToken);
        } catch (UnknownHostException e) {
            error = "Failed to contact Minecraft Services. Are you offline?";
            isLoggingIn = false;
            e.printStackTrace();
        } catch (Exception e) {
            error = "Something went wrong!";
            isLoggingIn = false;
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
        } catch (UnknownHostException e) {
            error = "Failed to contact Minecraft Services. Are you offline?";
            isLoggingIn = false;
            e.printStackTrace();
        } catch (Exception e) {
            error = "Something went wrong!";
            isLoggingIn = false;
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
            LastLogin.writeLastLogin(Session.session.getAccessToken(), "", "", Session.session.getUsername(), Session.session.getUuid(), false);
            MenuManager.setMenuScreen(new GuiMainMenu());
            reset();
            DisplayManager.getFrame().setAlwaysOnTop(true);
            DisplayManager.getFrame().setAlwaysOnTop(false);
        } catch (UnknownHostException e) {
            error = "Failed to contact Minecraft Services. Are you offline?";
            isLoggingIn = false;
            e.printStackTrace();
        } catch (Exception e) {
            error = "Something went wrong!";
            isLoggingIn = false;
            e.printStackTrace();
        }
    }

    public boolean validateToken(String mcAccessToken) {
        try {
            URL uri = new URL(mcProfileUrl);

            HttpURLConnection connection = (HttpURLConnection)uri.openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            return connection.getResponseCode() == 200 || connection.getResponseCode() == 204;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
