package gg.codie.minecraft.skins;

import gg.codie.minecraft.api.MojangAPI;
import gg.codie.minecraft.api.SessionServer;
import gg.codie.mineonline.Globals;
import org.json.JSONObject;

import java.io.FileNotFoundException;
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

    public static String findCloakURLForUsername(String username) {
        try {
            JSONObject profile = MojangAPI.minecraftProfile(username);
            if (!profile.has("id"))
                throw new FileNotFoundException("User not found: " + username);
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
