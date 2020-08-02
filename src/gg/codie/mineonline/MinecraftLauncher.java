package gg.codie.mineonline;

import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.utils.MD5Checksum;
import gg.codie.utils.OSUtils;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.lang.reflect.*;
import java.nio.file.Paths;
import java.util.*;

public class MinecraftLauncher {

    private static final boolean DEBUG = true;

    String jarPath;
    String serverAddress;
    String serverPort;
    String username;
    String token;
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
                    "-cp",
                    new File(MinecraftLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(),
                    MinecraftLauncher.class.getCanonicalName(),
                    jarPath,
                    Session.session.getUsername(),
                    Session.session.getSessionToken(),
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
        String serverAddress = args.length > 5 ? args[5] : null;
        String serverPort = args.length > 6 ? args[6] : null;
        new MinecraftLauncher(args[0], args[1], args[2], args[3], args[4], serverAddress, serverPort).startMinecraft();
    }

    public MinecraftLauncher(String jarPath, String username, String token, String width, String height, String serverAddress, String serverPort) throws Exception {
        this.jarPath = jarPath;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
        this.token = token;
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
        URLPatch.redefineURL();

        if(serverAddress != null) {
            try {
                new Options(LauncherFiles.MINECRAFT_OPTIONS_PATH).setOption("lastServer", serverAddress + "_" + serverPort);
            } catch (Exception ex) {

            }
        }

        System.out.println("Launching Jar, MD5: " + MD5Checksum.getMD5Checksum(jarPath));


        try {

            LinkedList<String> args = new LinkedList<>();

            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_AUTHLIB_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_CODEC_JORBIS_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_CODEC_WAV_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_COMMONS_IO_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_COMMONS_LANG3_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_GSON_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_GUAVA_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_ICU4J_CORE_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_JINPUT_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_JOPT_SIMPLE_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_JUTILS_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LIBRARY_LWJGL_OPENAL_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LIBRARY_JAVA_SOUND_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_NETTY_ALL_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_SOUNDSYSTEM_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_TROVE4J_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_VECMATH_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_ARGO_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_BCPROV_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LOG4J_CORE_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LOG4J_API_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_FASTUTIL_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_BRIGADIER_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_DATAFIXERUPPER_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_JAVABRIDGE_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_PATCHY_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_REALMS_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_TEXT2SPEECH_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_GLFW_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_GLFW_NATIVES_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_OPENGL_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_OPENGL_NATIVES_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_OPENAL_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_OPENAL_NATIVES_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_STB_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_STB_NATIVES_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_PATH).toUri().toURL());
            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_NATIVES_PATH).toUri().toURL());

            LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_LWJGL_GLFW_PATH).toUri().toURL());

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
            args.add("1.16");

            args.add("--version");
            args.add("1.16.1");

            args.add("uuid");
            args.add("806f3493624332a29166b098a0b03fb0");

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



            java.util.Properties props = System.getProperties();
            ProcessBuilder processBuilder = new ProcessBuilder(args.toArray(new String[0]));
            System.out.println(Arrays.toString(args.toArray(new String[0])));
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Map<String, String> env = processBuilder.environment();
            for(String prop : props.stringPropertyNames()) {
                env.put(prop, props.getProperty(prop));
            }
            System.setProperty("java.net.preferIPv4Stack", "true");
            processBuilder.directory(new File(System.getProperty("user.dir")));

            Method main = clazz.getMethod("main", String[].class);
            main.invoke(null, new Object[] {args.toArray(new String[0])});

            System.exit(0);
        } catch (Throwable e) {
            if(e.getClass() != ClassNotFoundException.class)
                e.printStackTrace();
        }

    }
}
