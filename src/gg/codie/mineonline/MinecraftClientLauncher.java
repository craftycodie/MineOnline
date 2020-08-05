package gg.codie.mineonline;

import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.patches.*;
import gg.codie.utils.MD5Checksum;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.lang.reflect.*;
import java.nio.file.Paths;
import java.util.*;

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

    MinecraftVersionInfo.MinecraftVersion minecraftVersion;

    private static Process gameProcess;

    public static void startProcess(String jarPath, String serverIP, String serverPort) {
        try {

            java.util.Properties props = System.getProperties();
            ProcessBuilder processBuilder = new ProcessBuilder(
                    Settings.settings.getString(Settings.JAVA_COMMAND),
                    "-javaagent:" + LauncherFiles.PATCH_AGENT_JAR,
                    "-Djava.util.Arrays.useLegacyMergeSort=true",
                    "-Djava.net.preferIPv4Stack=true",
                    "-Dmineonline.username=" + Session.session.getUsername(),
                    "-Dmineonline.token=" + Session.session.getSessionToken(),
                    "-Dmineonline.uuid=" + Session.session.getUuid(),
                    //TODO: Remove this:
                    "-Xmx1G",
                    "-Xms1G",

                    "-cp",
                    new File(MinecraftClientLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(),
                    MinecraftClientLauncher.class.getCanonicalName(),
                    jarPath,
                    "" + Display.getWidth(),
                    "" + Display.getHeight(),
                    serverIP != null ? serverIP : "",
                    serverPort != null ? serverPort : "");

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

            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
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

        LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.JSON_JAR).toUri().toURL());
        LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.BYTEBUDDY_JAR).toUri().toURL());
        LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.BYTEBUDDY_DEP_JAR).toUri().toURL());

        if(serverAddress != null && serverPort == null)
            this.serverPort = "25565";

        try {
            LibraryManager.addJarToClasspath(Paths.get(jarPath).toUri().toURL());
        } catch (Exception e) {
            System.err.println("Couldn't load jar file " + jarPath);
            e.printStackTrace();
            System.exit(1);
        }

        minecraftVersion = MinecraftVersionInfo.getVersion(jarPath);
    }

    public void startMinecraft() throws Exception {
        if(serverAddress != null) {
            try {
                new Options(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", serverAddress + "_" + serverPort);
            } catch (Exception ex) {

            }
        }

        System.out.println("Launching Jar, MD5: " + MD5Checksum.getMD5Checksum(jarPath));


        try {

            LinkedList<String> args = new LinkedList<>();

            for(String library : minecraftVersion.libraries) {
                LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_PATH + library).toUri().toURL());
            }

            for(String nativeJar : minecraftVersion.nativesWindows) {
                LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_PATH + nativeJar).toUri().toURL());
            }

            new Session(username, token, uuid);

            URLPatch.redefineURL();
            PropertiesSignaturePatch.redefineIsSignatureValid();
            YggdrasilMinecraftSessionServicePatch.allowMineonlineSkins();


            Class clazz = Class.forName("net.minecraft.client.main.Main");

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
            args.add(LauncherFiles.getMinecraftDirectory().getPath());

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


            main.invoke(null, new Object[] {args.toArray(new String[0])});

            System.exit(0);
        } catch (Throwable e) {
            if(e.getClass() != ClassNotFoundException.class)
                e.printStackTrace();
        }

    }
}
