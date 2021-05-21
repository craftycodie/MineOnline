package gg.codie.mineonline;

import gg.codie.mineonline.discord.DiscordRPCHandler;
import gg.codie.mineonline.utils.LastLogin;
import gg.codie.mineonline.utils.SkinUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.UUID;

public class Session {

    public static Session session;

    public String getUsername() {
        return username;
    }

    private String username;

    private String clientToken;

    public String getClientToken() {
        return clientToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    private String accessToken;

    public String getUuid() {
        return uuid;
    }

    private String uuid;

    public boolean isOnline() {
        return accessToken != null;
    }

    private boolean isPremium;

    public boolean isPremium() {
        return isPremium;
    }

    public Session(String username) {
        session = this;
        this.username = username;
        this.uuid = UUID.randomUUID().toString();
        this.isPremium = false;
        cacheSkin();
    }

    public Session(String username, String accessToken, String clientToken, String uuid, boolean isPremium) {
        this(username, accessToken, uuid, isPremium);
        this.clientToken = clientToken;
    }

    public Session(String username, String accessToken, String uuid, boolean isPremium) {
        session = this;
        this.username = username;
        this.accessToken = accessToken;
        this.uuid = uuid;
        this.isPremium = isPremium;
        cacheSkin();
        DiscordRPCHandler.updateSession(username);
    }

    public void logout() {
        session = null;
        LastLogin.deleteLastLogin();
    }

    public void cacheSkin () {
        (new Thread() {
            public void run() {
                Settings.singleton.loadSettings();

                String skinUrl = SkinUtils.findSkinURLForUuid(uuid);

                try (BufferedInputStream in = new BufferedInputStream(new URL(skinUrl).openStream())) {

                    // Delete the currently cached skin.
                    File cachedSkin = new File(LauncherFiles.CACHED_SKIN_PATH);
                    if (cachedSkin.exists()) {
                        cachedSkin.delete();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(LauncherFiles.CACHED_SKIN_PATH);
                    byte dataBuffer[] = new byte[2048];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 2048)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }

                    fileOutputStream.close();


                } catch (IOException e) {
                    File cachedSkin = new File(LauncherFiles.CACHED_SKIN_PATH);
                    if (cachedSkin.exists()) {
                        cachedSkin.delete();
                    }
                }

                String cloakUrl = SkinUtils.findCloakURLForUuid(uuid);

                try (BufferedInputStream in = new BufferedInputStream(new URL(cloakUrl).openStream())) {

                    // Delete the currently cached skin.
                    File cachedCloak = new File(LauncherFiles.CACHED_CLOAK_PATH);
                    if (cachedCloak.exists()) {
                        cachedCloak.delete();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(LauncherFiles.CACHED_CLOAK_PATH);

                    byte dataBuffer[] = new byte[2048];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 2048)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }

                    fileOutputStream.close();

                } catch (IOException e) {
                    // handle exception
                    File cachedCloak = new File(LauncherFiles.CACHED_CLOAK_PATH);
                    if (cachedCloak.exists()) {
                        cachedCloak.delete();
                    }
                }

                try {
                    JSONObject skinMetadata = SkinUtils.getSkinMetadata(uuid);

                    FileWriter fileWriter = new FileWriter(LauncherFiles.CACHED_SKIN_METADATA_PATH, false);
                    fileWriter.write(skinMetadata.toString());
                    fileWriter.close();
                } catch (IOException io) {
                    File cachedMetadata = new File(LauncherFiles.CACHED_SKIN_METADATA_PATH);
                    if (cachedMetadata.exists()) {
                        cachedMetadata.delete();
                    }
                }
            }
        }).start();
    }
}
