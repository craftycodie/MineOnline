package com.ahnewark.mineonline.utils;

import com.ahnewark.minecraft.api.MojangAPI;
import com.ahnewark.minecraft.api.SessionServer;
import com.ahnewark.mineonline.Globals;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Base64;

public class SkinUtils {
    public static JSONObject getUserSkin(String username) {
        try {
            JSONObject profile = MojangAPI.minecraftProfile(username);
            if (!profile.has("id"))
                throw new FileNotFoundException("User not found: " + username);
            profile = SessionServer.minecraftProfile(profile.getString("id"));
            if (!profile.has("properties"))
                throw new FileNotFoundException("Skin not found: " + username);
            profile = new JSONObject(new String(Base64.getDecoder().decode(profile.getJSONArray("properties").getJSONObject(0).getString("value")), StandardCharsets.UTF_8));
            return profile.getJSONObject("textures").getJSONObject("SKIN");

        } catch (Exception ex) {
            if (Globals.DEV)
                ex.printStackTrace();
            return null;
        }
    }

    public static URL findEventCloakURLForUsername(String username) {
        try {
            LocalDateTime today = java.time.LocalDateTime.now();
            if ((today.getDayOfMonth() == 24 || today.getDayOfMonth() == 25) && today.getMonth() == Month.DECEMBER)
                return SkinUtils.class.getResource("/textures/mineonline/capes/xmas.png");

            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static URL findCloakURLForUsername(String username) {
        try {
            JSONObject profile = MojangAPI.minecraftProfile(username);
            if (!profile.has("id"))
                throw new FileNotFoundException("User not found: " + username);

            profile = SessionServer.minecraftProfile(profile.getString("id"));
            if (!profile.has("properties"))
                throw new FileNotFoundException("Cloak not found: " + username);
            profile = new JSONObject(new String(Base64.getDecoder().decode(profile.getJSONArray("properties").getJSONObject(0).getString("value")), StandardCharsets.UTF_8));
            return new URL(profile.getJSONObject("textures").getJSONObject("CAPE").getString("url"));
        } catch (Exception ex) {
            return null;
        }
    }
}
