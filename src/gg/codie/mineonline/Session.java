package gg.codie.mineonline;

import gg.codie.mineonline.gui.rendering.PlayerGameObject;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

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

    public boolean isOnline() {
        return sessionToken != null;
    }

    public Session(String username) {
        session = this;
        this.username = username;
        cacheSkin();
    }

    public Session(String username, String sessionToken) {
        session = this;
        this.username = username;
        this.sessionToken = sessionToken;
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
                Properties.loadProperties();

                try (BufferedInputStream in = new BufferedInputStream(new URL("http://mineonline.codie.gg/MinecraftSkins/" + username + ".png").openStream())) {

                    // Delete the currently cached skin.
                    File cachedSkin = new File(LauncherFiles.CACHED_SKIN_PATH);
                    if (cachedSkin.exists()) {
                        cachedSkin.delete();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(LauncherFiles.CACHED_SKIN_PATH);
                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }

                    fileOutputStream.close();


                } catch (IOException e) {
                    // handle exception
                }

                try (BufferedInputStream in = new BufferedInputStream(new URL("http://mineonline.codie.gg/MinecraftCloaks/" + username + ".png").openStream())) {

                    // Delete the currently cached skin.
                    File cachedCloak = new File(LauncherFiles.CACHED_CLOAK_PATH);
                    if (cachedCloak.exists()) {
                        cachedCloak.delete();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(LauncherFiles.CACHED_CLOAK_PATH);

                    byte dataBuffer[] = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead);
                    }

                    fileOutputStream.close();

                } catch (IOException e) {
                    // handle exception
                }

                if(PlayerGameObject.thePlayer != null) {
                    try {
                        if (new File(LauncherFiles.CACHED_CLOAK_PATH).exists())
                            PlayerGameObject.thePlayer.setCloak(Paths.get(LauncherFiles.CACHED_CLOAK_PATH).toUri().toURL());
                        if (new File(LauncherFiles.CACHED_SKIN_PATH).exists())
                            PlayerGameObject.thePlayer.setSkin(Paths.get(LauncherFiles.CACHED_SKIN_PATH).toUri().toURL());
                    } catch (MalformedURLException mx) {

                    }
                }
            }
        }).start();
    }
}
