package gg.codie.mineonline.client;

import gg.codie.common.utils.OSUtils;
import gg.codie.mineonline.*;
import gg.codie.mineonline.discord.DiscordRPCHandler;
import gg.codie.mineonline.patches.SocketPatch;
import gg.codie.mineonline.patches.URLPatch;
import gg.codie.mineonline.patches.minecraft.PropertiesSignaturePatch;
import gg.codie.mineonline.patches.minecraft.YggdrasilMinecraftSessionServicePatch;
import gg.codie.mineonline.utils.JREUtils;
import gg.codie.mineonline.utils.Logging;

import javax.swing.*;
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
    String jarPath;
    String serverAddress;
    String serverPort;
    String width;
    String height;

    MinecraftVersion minecraftVersion;

    public static void startProcess(String jarPath, String serverIP, String serverPort, MinecraftVersion minecraftVersion) {
        try {

            java.util.Properties props = System.getProperties();

            LinkedList<String> libraries = new LinkedList<>();

            for(String library : minecraftVersion.libraries) {
                libraries.add(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_PATH + library).toString());
            }

            for(String nativeJar : minecraftVersion.natives) {
                libraries.add(Paths.get(LauncherFiles.MINECRAFT_LIBRARIES_PATH + nativeJar).toString());
            }

            libraries.add(new File(MinecraftClientLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
            libraries.add(jarPath);
            libraries.add(LauncherFiles.DISCORD_RPC_JAR);

            LinkedList<String> launchArgs = new LinkedList();
            launchArgs.add(JREUtils.getJavaExecutable());
            launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
            launchArgs.add("-Djava.net.preferIPv4Stack=true");
            if(OSUtils.isMac())
                launchArgs.add("-XstartOnFirstThread");
            launchArgs.add("-Dmineonline.username=" + Session.session.getUsername());
            launchArgs.add("-Dmineonline.token=" + Session.session.getAccessToken());
            launchArgs.add("-Dmineonline.uuid=" + Session.session.getUuid());
            if (!Settings.singleton.getClientLaunchArgs().isEmpty())
                launchArgs.addAll(Arrays.asList(Settings.singleton.getClientLaunchArgs().split(" ")));
            launchArgs.add("-cp");
            launchArgs.add(LibraryManager.getClasspath(false, false, libraries.toArray(new String[libraries.size()])));
            launchArgs.add(MinecraftClientLauncher.class.getCanonicalName());
            launchArgs.add(jarPath);
            launchArgs.add("" + Settings.singleton.getGameWidth());
            launchArgs.add("" + Settings.singleton.getGameHeight());
            if(serverIP != null) {
                launchArgs.add(serverIP);
                if(serverPort != null)
                    launchArgs.add(serverPort);
            }

            Settings.singleton.saveMinecraftOptions(minecraftVersion.optionsVersion);

            ProcessBuilder processBuilder = new ProcessBuilder(launchArgs.toArray(new String[0]));

            Map<String, String> env = processBuilder.environment();
            for (String prop : props.stringPropertyNames()) {
                env.put(prop, props.getProperty(prop));
            }
            processBuilder.directory(new File(System.getProperty("user.dir")));
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

            processBuilder.inheritIO().start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        Logging.enableLogging();
        DiscordRPCHandler.initialize();
        String serverAddress = args.length > 3 ? args[3] : null;
        String serverPort = args.length > 4 ? args[4] : null;
        new MinecraftClientLauncher(args[0], args[1], args[2], serverAddress, serverPort).startMinecraft();
    }

    public MinecraftClientLauncher(String jarPath, String width, String height, String serverAddress, String serverPort) throws Exception {
        this.jarPath = jarPath;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.width = width;
        this.height = height;

        boolean premium = System.getProperty("mineonline.token") != null;
        new Session(System.getProperty("mineonline.username"), System.getProperty("mineonline.token"), System.getProperty("mineonline.uuid"), premium);

        if(serverAddress != null && serverPort == null)
            this.serverPort = "25565";

        minecraftVersion = MinecraftVersionRepository.getSingleton(true).getVersion(jarPath);
    }

    public void startMinecraft() throws Exception {
        System.setProperty("apple.awt.application.name", "MineOnline");

        URLClassLoader classLoader = new URLClassLoader(new URL[] { Paths.get(jarPath).toUri().toURL() });

        if(minecraftVersion != null)
            DiscordRPCHandler.play(minecraftVersion.name, serverAddress, serverPort);
        else
            DiscordRPCHandler.play(Paths.get(jarPath).getFileName().toString(), serverAddress, serverPort);

        try {
            LinkedList<String> args = new LinkedList<>();

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
            args.add(Session.session.getUuid());

            args.add("--username");
            args.add(Session.session.getUsername());

            args.add("--session");
            args.add(Session.session.getAccessToken());

            args.add("--accessToken");
            args.add(Session.session.getAccessToken());

            if (Settings.singleton.getFullscreen())
                args.add("--fullscreen");

            if (!Session.session.isPremium())
                args.add("--demo");

            Method main = clazz.getMethod("main", String[].class);

            SocketPatch.watchSockets();
            URLPatch.redefineURL();
            YggdrasilMinecraftSessionServicePatch.allowMineonlineSkins(classLoader);
            PropertiesSignaturePatch.redefineIsSignatureValid(classLoader);

            main.invoke(null, new Object[] {args.toArray(new String[0])});

            System.exit(0);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            ex.getTargetException().printStackTrace();

            JOptionPane.showMessageDialog(null, "Failed to launch Minecraft.\nPlease make sure all libraries are present.");
        } catch (Throwable e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "Failed to launch Minecraft.\nPlease make sure all libraries are present.");
        }
    }
}
