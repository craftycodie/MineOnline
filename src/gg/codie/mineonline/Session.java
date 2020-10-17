package gg.codie.mineonline;

import gg.codie.mineonline.utils.SkinUtils;
import gg.codie.mineonline.gui.rendering.PlayerGameObject;
import gg.codie.mineonline.utils.LastLogin;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.UUID;

public class Session {

    public static Session session;

    public String getUsername() {
        return username;
    }

    private String username;

    public String getSessionToken() {
        return sessionToken;
    }

    private String sessionToken;

    public String getUuid() {
        return uuid;
    }

    private String uuid;

    public boolean isOnline() {
        return sessionToken != null;
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

    public Session(String username, String sessionToken, String uuid, boolean isPremium) {
        session = this;
        this.username = username;
        this.sessionToken = sessionToken;
        this.uuid = uuid;
        this.isPremium = isPremium;
        cacheSkin();
    }

    public void logout() {
        session = null;
        PlayerGameObject.thePlayer.setCloak(LauncherFiles.TEMPLATE_CLOAK_PATH);
        PlayerGameObject.thePlayer.setSkin(LauncherFiles.TEMPLATE_SKIN_PATH);
        LastLogin.deleteLastLogin();
    }

    public void cacheSkin () {
        (new Thread() {
            public void run() {
                Settings.loadSettings();

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

                if(PlayerGameObject.thePlayer != null) {
                    try {
                        if (new File(LauncherFiles.CACHED_CLOAK_PATH).exists())
                            PlayerGameObject.thePlayer.setCloak(Paths.get(LauncherFiles.CACHED_CLOAK_PATH).toUri().toURL());
                        if (new File(LauncherFiles.CACHED_SKIN_PATH).exists())
                            PlayerGameObject.thePlayer.setSkin(Paths.get(LauncherFiles.CACHED_SKIN_PATH).toUri().toURL());
                        try (FileInputStream input = new FileInputStream(LauncherFiles.CACHED_SKIN_METADATA_PATH)) {
                            byte[] buffer = new byte[8096];
                            int bytes_read = 0;
                            StringBuffer stringBuffer = new StringBuffer();
                            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                                for (int i = 0; i < bytes_read; i++) {
                                    stringBuffer.append((char) buffer[i]);
                                }
                            }

                            JSONObject metadata = new JSONObject(stringBuffer.toString());
                            if(metadata.has("slim"))
                                PlayerGameObject.thePlayer.setSlim(metadata.getBoolean("slim"));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } catch (MalformedURLException mx) {
                        mx.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
