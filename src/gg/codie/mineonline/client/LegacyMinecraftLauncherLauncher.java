package gg.codie.mineonline.client;

import gg.codie.minecraft.client.VersionFile;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.patches.*;
import gg.codie.utils.FileUtils;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

public class LegacyMinecraftLauncherLauncher {
    public LegacyMinecraftLauncherLauncher(String jarPath) throws Exception {
        MinecraftVersion minecraftVersion = MinecraftVersionRepository.getSingleton(true).getVersion(jarPath);

        URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { Paths.get(jarPath).toUri().toURL() });

        if (minecraftVersion != null) {
            DisplayManager.closeDisplay();
            if (DisplayManager.getFrame() != null)
                DisplayManager.getFrame().dispose();

            Settings.singleton.loadSettings();
            String updateURLString = Settings.singleton.getMinecraftUpdateURL();

            SystemSetPropertyPatch.banNativeChanges();

            if(updateURLString != null && !updateURLString.isEmpty()) {
                URL updateURL = new URL(updateURLString);
                File currentJar = new File(LauncherFiles.MINECRAFT_BINARIES_PATH + FileUtils.getFileName(updateURL));
                if(currentJar.exists()) {
                    int existingJarSize = (int)currentJar.length();
                    HttpURLConnection versionRequest = (HttpURLConnection) new URL(updateURLString).openConnection();
                    versionRequest.setRequestMethod("HEAD");
                    versionRequest.connect();

                    if(existingJarSize != versionRequest.getContentLength()) {
                        VersionFile.delete();
                    }
                }
            }

            SocketPatch.watchSockets();
            URLPatch.redefineURL(updateURLString);
            URLConnectionPatch.patchResponses();
            // Allow texture packs in versions before Alpha 1.2.2
            if (minecraftVersion != null && minecraftVersion.useTexturepackPatch)
                ClassPatch.useTexturePacks(Settings.singleton.getTexturePack());

            try {
                Class launcherClass = urlClassLoader.loadClass("net.minecraft.LauncherFrame");
                Method mainFunction = launcherClass.getDeclaredMethod("main", String[].class);
                mainFunction.invoke(null, new Object[] { new String[0]} );
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Failed to launch minecraft.");
            }
        }
    }
}
