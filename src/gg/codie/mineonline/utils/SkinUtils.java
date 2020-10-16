package gg.codie.mineonline.utils;

import gg.codie.minecraft.api.MojangAPI;
import gg.codie.minecraft.api.SessionServer;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public class SkinUtils {
    public static String findSkinURLForUsername(String username) {
        try {
            JSONObject profile = MojangAPI.minecraftProfile(username);
            if (!profile.has("id"))
                throw new FileNotFoundException("User not found: " + username);
            profile = SessionServer.minecraftProfile(profile.getString("id"));
            if (!profile.has("properties"))
                throw new FileNotFoundException("Skin not found: " + username);
            profile = new JSONObject(new String(Base64.getDecoder().decode(profile.getJSONArray("properties").getJSONObject(0).getString("value")), StandardCharsets.UTF_8));
            return profile.getJSONObject("textures").getJSONObject("SKIN").getString("url");

        } catch (Exception ex) {
            if (Globals.DEV)
                ex.printStackTrace();
            return "";
        }
    }

    public static byte[] minecraftCapesCape(String uuid) throws IOException {
        HttpURLConnection connection;

        URL url = new URL("https://minecraftcapes.net/profile/" + uuid);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(false);

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();

        try {
            JSONObject res = new JSONObject(response.toString());
            return Base64.getDecoder().decode(res.getJSONObject("textures").getString("cape"));
        } catch (Exception ex) {
            if (Globals.DEV)
                ex.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static String minecraftCapesCapeUrl(String uuid) throws IOException {
        HttpURLConnection connection;

        URL url = new URL("https://minecraftcapes.net/profile/" + uuid);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(false);

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();

        try {
            JSONObject res = new JSONObject(response.toString());
            return res.getString("cape");
        } catch (Exception ex) {
            if (Globals.DEV)
                ex.printStackTrace();
            return null;
        }
    }

    public static String findCloakURLForUsername(String username) {
        try {
            JSONObject profile = MojangAPI.minecraftProfile(username);
            if (!profile.has("id"))
                throw new FileNotFoundException("User not found: " + username);

            if (Settings.settings.optBoolean(Settings.CUSTOM_CAPES, false)) {
//                byte[] capePng = minecraftCapesCape(profile.getString("id"));
//
//                if (capePng != null) {
//                    File capeFile = new File(LauncherFiles.MINEONLINE_TEMP_CAPES_FOLDER + profile.getString("id") + ".png");
//                    if (capeFile.exists())
//                        capeFile.delete();
//
//                    capeFile.mkdirs();
//                    capeFile.createNewFile();
//                    Files.write(capeFile.toPath(), capePng, StandardOpenOption.WRITE);
//                    capeFile.deleteOnExit();
//
//                    return capeFile.toURI().toURL().toString();
//                }
                String capeUrl = minecraftCapesCapeUrl(profile.getString("id"));
                if (capeUrl != null)
                    return capeUrl;
            }

            profile = SessionServer.minecraftProfile(profile.getString("id"));
            if (!profile.has("properties"))
                throw new FileNotFoundException("Cloak not found: " + username);
            profile = new JSONObject(new String(Base64.getDecoder().decode(profile.getJSONArray("properties").getJSONObject(0).getString("value")), StandardCharsets.UTF_8));
            return profile.getJSONObject("textures").getJSONObject("CAPE").getString("url");

        } catch (Exception ex) {
            if (Globals.DEV)
                ex.printStackTrace();
            return "";
        }
    }

    public static String findSkinURLForUuid(String uuid) {
        try {
            JSONObject profile = SessionServer.minecraftProfile(uuid);
            if (!profile.has("properties"))
                throw new FileNotFoundException("Skin not found: " + uuid);
            profile = new JSONObject(new String(Base64.getDecoder().decode(profile.getJSONArray("properties").getJSONObject(0).getString("value")), StandardCharsets.UTF_8));
            return profile.getJSONObject("textures").getJSONObject("SKIN").getString("url");

        } catch (Exception ex) {
            if (Globals.DEV)
                ex.printStackTrace();
            return "";
        }
    }

    public static String findCloakURLForUuid(String uuid) {
        try {
            if (Settings.settings.optBoolean(Settings.CUSTOM_CAPES, false)) {
//                byte[] capePng = minecraftCapesCape(uuid);
//
//                if (capePng != null) {
//                    File capeFile = new File(LauncherFiles.MINEONLINE_TEMP_CAPES_FOLDER + uuid + ".png");
//                    if (capeFile.exists())
//                        capeFile.delete();
//                    capeFile.getParentFile().mkdirs();
//                    Files.write(capeFile.toPath(), capePng, StandardOpenOption.CREATE_NEW);
//                    capeFile.deleteOnExit();
//
//                    return capeFile.toURI().toURL().toString();
//                }

                String capeUrl = minecraftCapesCapeUrl(uuid);
                if (capeUrl != null)
                    return capeUrl;
            }

            JSONObject profile = SessionServer.minecraftProfile(uuid);
            if (!profile.has("properties"))
                throw new FileNotFoundException("Cloak not found: " + uuid);
            profile = new JSONObject(new String(Base64.getDecoder().decode(profile.getJSONArray("properties").getJSONObject(0).getString("value")), StandardCharsets.UTF_8));
            return profile.getJSONObject("textures").getJSONObject("CAPE").getString("url");

        } catch (Exception ex) {
            if (Globals.DEV)
                ex.printStackTrace();
            return "";
        }
    }

    public static JSONObject getSkinMetadata(String uuid) {
        try {
            JSONObject profile = SessionServer.minecraftProfile(uuid);
            if (!profile.has("properties"))
                throw new FileNotFoundException("Skin metadata not found: " + uuid);
            profile = new JSONObject(new String(Base64.getDecoder().decode(profile.getJSONArray("properties").getJSONObject(0).getString("value")), StandardCharsets.UTF_8));
            
            boolean slim = profile.getJSONObject("textures").getJSONObject("SKIN").getJSONObject("metadata").getString("model").equals("slim");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("slim", slim);

            return jsonObject;

        } catch (Exception ex) {
            if (Globals.DEV)
                ex.printStackTrace();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("slim", false);

            return jsonObject;
        }
    }
}
