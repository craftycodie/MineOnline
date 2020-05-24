package gg.codie.mineonline;

import gg.codie.mineonline.gui.rendering.PlayerGameObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class Session {

    public static Session session;

    private String username;
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

    private void cacheSkin () {
        (new Thread() {
            public void run() {
                Properties.loadProperties();

                try (BufferedInputStream in = new BufferedInputStream(new URL("http://" + Properties.properties.getProperty("apiDomainName") + "/MinecraftSkins/" + username + ".png").openStream())) {

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

                try (BufferedInputStream in = new BufferedInputStream(new URL("http://" + Properties.properties.getProperty("apiDomainName") + "/MinecraftCloaks/" + username + ".png").openStream())) {

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

                PlayerGameObject.thePlayer.setCloak(LauncherFiles.CACHED_CLOAK_PATH);
                PlayerGameObject.thePlayer.setSkin(LauncherFiles.CACHED_SKIN_PATH);
            }
        }).start();
    }
}
