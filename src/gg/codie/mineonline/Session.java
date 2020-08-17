package gg.codie.mineonline;

import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.gui.rendering.PlayerGameObject;
import gg.codie.utils.JSONUtils;
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

    public Session(String username) {
        session = this;
        this.username = username;
        this.uuid = UUID.randomUUID().toString();
        cacheSkin();
    }

    public Session(String username, String sessionToken, String uuid) {
        session = this;
        this.username = username;
        this.sessionToken = sessionToken;
        this.uuid = uuid;
        cacheSkin();
    }

    public void logout() {
        session = null;
        PlayerGameObject.thePlayer.setCloak(LauncherFiles.TEMPLATE_CLOAK_PATH);
        PlayerGameObject.thePlayer.setSkin(LauncherFiles.TEMPLATE_SKIN_PATH);
    }

    public void cacheSkin () {
        (new Thread() {
            public void run() {
                Settings.loadSettings();

                try (BufferedInputStream in = new BufferedInputStream(new URL("http://" + Globals.API_HOSTNAME + "/mineonline/player/" + uuid + "/skin").openStream())) {

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
                    // handle exception
                }

                try (BufferedInputStream in = new BufferedInputStream(new URL("http://" + Globals.API_HOSTNAME + "/mineonline/player/" + uuid + "/cloak").openStream())) {

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
                }

                try {
                    JSONObject skinMetadata = MineOnlineAPI.getSkinMetadata(uuid);

                    FileWriter fileWriter = new FileWriter(LauncherFiles.CACHED_SKIN_METADATA_PATH, false);
                    fileWriter.write(skinMetadata.toString());
                    fileWriter.close();
                } catch (IOException io) {
                    io.printStackTrace();
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
