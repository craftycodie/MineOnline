package gg.codie.mineonline.client;

import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.*;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.patches.SocketPatch;
import gg.codie.mineonline.patches.URLPatch;
import gg.codie.mineonline.patches.minecraft.PropertiesSignaturePatch;
import gg.codie.mineonline.patches.minecraft.YggdrasilMinecraftSessionServicePatch;
import gg.codie.mineonline.server.MinecraftServerProcess;
import gg.codie.mineonline.utils.JREUtils;
import gg.codie.mineonline.utils.Logging;
import gg.codie.utils.MD5Checksum;
import gg.codie.utils.OSUtils;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class MinecraftClientLauncher {

    private static final boolean DEBUG = true;

    public static final String PROP_ENV = "minecraft.api.env";
    public static final String PROP_AUTH_HOST = "minecraft.api.auth.host";
    public static final String PROP_ACCOUNT_HOST = "minecraft.api.account.host";
    public static final String PROP_SESSION_HOST = "minecraft.api.session.host";

    String jarPath;
    String serverAddress;
    String serverPort;
    String username;
    String token;
    String uuid;
    String width;
    String height;

    MinecraftVersion minecraftVersion;

    private static Process gameProcess;

    public static void startProcess(String jarPath, String serverIP, String serverPort, MinecraftVersion minecraftVersion) {
        try {

            java.util.Properties props = System.getProperties();

            MinecraftVersion version = MinecraftVersionRepository.getSingleton().getVersion(jarPath);

            LinkedList<String> libraries = new LinkedList<>();

            for(String library : minecraftVersion.libraries) {
                libraries.add(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_PATH + library).toString());
            }

            for(String nativeJar : minecraftVersion.natives) {
                libraries.add(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_PATH + nativeJar).toString());
            }

            libraries.add(new File(MinecraftServerProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
            libraries.add(jarPath);

            LinkedList<String> launchArgs = new LinkedList();
            launchArgs.add(JREUtils.getJavaExecutable());
            launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
            launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
            launchArgs.add("-Djava.net.preferIPv4Stack=true");
            if(OSUtils.isMac())
                launchArgs.add("-XstartOnFirstThread");
            launchArgs.add("-Dmineonline.username=" + Session.session.getUsername());
            launchArgs.add("-Dmineonline.token=" + Session.session.getSessionToken());
            launchArgs.add("-Dmineonline.uuid=" + Session.session.getUuid());
            if (Settings.settings.has(Settings.CLIENT_LAUNCH_ARGS) && !Settings.settings.getString(Settings.CLIENT_LAUNCH_ARGS).isEmpty())
                launchArgs.addAll(Arrays.asList(Settings.settings.getString(Settings.CLIENT_LAUNCH_ARGS).split(" ")));
            launchArgs.add("-cp");
            launchArgs.add(LibraryManager.getClasspath(false, libraries.toArray(new String[libraries.size()])));
            launchArgs.add(MinecraftClientLauncher.class.getCanonicalName());
            launchArgs.add(jarPath);
            launchArgs.add("" + Display.getWidth());
            launchArgs.add("" + Display.getHeight());
            if(serverIP != null) {
                launchArgs.add(serverIP);
                if(serverPort != null)
                    launchArgs.add(serverPort);
            }

            ProcessBuilder processBuilder = new ProcessBuilder(launchArgs.toArray(new String[0]));

            Map<String, String> env = processBuilder.environment();
            for (String prop : props.stringPropertyNames()) {
                env.put(prop, props.getProperty(prop));
            }
            processBuilder.directory(new File(System.getProperty("user.dir")));
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

            DisplayManager.getFrame().setVisible(false);

            gameProcess = processBuilder.start();

            Thread closeLauncher = new Thread(() -> gameProcess.destroyForcibly());
            Runtime.getRuntime().addShutdownHook(closeLauncher);

            while (gameProcess.isAlive()) {

            }

            if(gameProcess.exitValue() == 1) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Failed to launch Minecraft.\nPlease make sure all libraries are present.");
                    }
                });
                DisplayManager.getFrame().setVisible(true);

            } else {
                Runtime.getRuntime().halt(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Logging.enableLogging();

        String serverAddress = args.length > 3 ? args[3] : null;
        String serverPort = args.length > 4 ? args[4] : null;
        new MinecraftClientLauncher(args[0], args[1], args[2], serverAddress, serverPort).startMinecraft();
    }

    public MinecraftClientLauncher(String jarPath, String width, String height, String serverAddress, String serverPort) throws Exception {
        this.jarPath = jarPath;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = System.getProperty("mineonline.username");
        this.token = System.getProperty("mineonline.token");
        this.uuid = System.getProperty("mineonline.uuid");
        this.width = width;
        this.height = height;

        if(serverAddress != null && serverPort == null)
            this.serverPort = "25565";

        minecraftVersion = MinecraftVersionRepository.getSingleton().getVersion(jarPath);
    }

    public void startMinecraft() throws Exception {
        URLClassLoader classLoader = new URLClassLoader(new URL[] { Paths.get(jarPath).toUri().toURL() });

        if(serverAddress != null) {
            try {
                new Options(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", serverAddress + "_" + serverPort);
            } catch (Exception ex) {

            }
        }

        System.out.println("Launching Jar, MD5: " + MD5Checksum.getMD5ChecksumForFile(jarPath));

        try {

            LinkedList<String> args = new LinkedList<>();

            new Session(username, token, uuid);

            LibraryManager.extractRuntimeNatives(minecraftVersion.natives);
            LibraryManager.updateNativesPath(LauncherFiles.MINEONLINE_RUNTIME_NATIVES_FOLDER.substring(0, LauncherFiles.MINEONLINE_RUNTIME_NATIVES_FOLDER.length() - 1));

            Class clazz = classLoader.loadClass("net.minecraft.client.main.Main");

            if(serverAddress != null) {
                args.add("--server");
                args.add(serverAddress);
            }

            if(serverPort != null) {
                args.add("--port");
                args.add(serverPort);
            }

            args.add("--width");
            args.add(width);

            args.add("--height");
            args.add(height);

            args.add("--gameDir");
            args.add(LauncherFiles.getNewMinecraftDirectory().getPath());

            args.add("--assetsDir");
            args.add(LauncherFiles.MINECRAFT_ASSETS_PATH);

            args.add("--assetIndex");
            args.add(minecraftVersion.assetIndex);

            args.add("--version");
            args.add(minecraftVersion.baseVersion);

            args.add("--uuid");
            args.add(uuid);

            args.add("--username");
            args.add(username);

            args.add("--session");
            args.add(token);

            args.add("--accessToken");
            args.add(token);

            if (Settings.settings.has(Settings.FULLSCREEN) && Settings.settings.getBoolean(Settings.FULLSCREEN))
                args.add("--fullscreen");

            if (Settings.settings.has(Settings.IS_PREMIUM) && !Settings.settings.getBoolean(Settings.IS_PREMIUM))
                args.add("--demo");

            Method main = clazz.getMethod("main", String[].class);

            System.setProperty(PROP_AUTH_HOST, "http://" + Globals.API_HOSTNAME);
            System.setProperty(PROP_ACCOUNT_HOST, "http://" + Globals.API_HOSTNAME);
            System.setProperty(PROP_SESSION_HOST, "http://" + Globals.API_HOSTNAME);

            URLPatch.redefineURL();
            PropertiesSignaturePatch.redefineIsSignatureValid(classLoader);
            YggdrasilMinecraftSessionServicePatch.allowMineonlineSkins(classLoader);
            SocketPatch.watchSockets();

            main.invoke(null, new Object[] {args.toArray(new String[0])});

            System.exit(0);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            ex.getTargetException().printStackTrace();

            System.exit(1);
        } catch (Throwable e) {
            e.printStackTrace();

            System.exit(1);
        }

    }
}
