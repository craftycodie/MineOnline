package gg.codie.mineonline.utils;

import gg.codie.minecraft.api.MojangAPI;
import gg.codie.minecraft.api.SessionServer;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.Settings;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    public static boolean hasCustomCape(String uuid) throws IOException {
        HttpURLConnection connection;

        URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/player/" + uuid + "/customcape");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(false);

        return connection.getResponseCode() == 200;
    }

    public static String getEventCapeURL(String uuid) throws IOException {
        HttpURLConnection connection;

        URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/player/" + uuid + "/eventcape");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(false);

        if (connection.getResponseCode() != 200)
            return null;

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();

        return response.toString();
    }

    public static String findCloakURLForUsername(String username) {
        try {
            JSONObject profile = MojangAPI.minecraftProfile(username);
            if (!profile.has("id"))
                throw new FileNotFoundException("User not found: " + username);

            String eventCapeUEL = getEventCapeURL(profile.getString("id"));

            if (eventCapeUEL != null)
                return eventCapeUEL;
            else if (Settings.singleton.getCustomCapes()) {
                if (hasCustomCape(profile.getString("id")))
                    return Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/player/" + profile.getString("id") + "/customcape";
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
            String eventCapeUEL = getEventCapeURL(uuid);

            if (eventCapeUEL != null)
                return eventCapeUEL;
            else if (Settings.singleton.getCustomCapes()) {
                if (hasCustomCape(uuid))
                    return Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/player/" + uuid + "/customcape";
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
