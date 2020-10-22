package gg.codie.mineonline.server;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.patches.URLPatch;
import gg.codie.mineonline.utils.JREUtils;
import gg.codie.mineonline.utils.Logging;
import gg.codie.utils.ArrayUtils;
import gg.codie.utils.OSUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class MinecraftServerProcess {

    public static Process startMinecraftServer(String[] args) throws Exception {
        java.util.Properties props = System.getProperties();

        File jarFile = new File(args[0]);
        if(!jarFile.exists()) {
            System.err.println("Couldn't find jar file " + args[0]);
            System.exit(1);
        }

        LinkedList<String> launchArgs = new LinkedList();
        launchArgs.add(JREUtils.getJavaExecutable());
        launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
        launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");

        if (args.length > 1)
            if (!args[1].startsWith("-"))
                launchArgs.addAll(Arrays.asList(Arrays.copyOfRange(args, 2, args.length)));
            else
                launchArgs.addAll(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)));

        launchArgs.add("-cp");
        launchArgs.add(LibraryManager.getClasspath(false, new String[] { new File(MinecraftServerProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(), args[0] }));
        launchArgs.add(MinecraftServerProcess.class.getCanonicalName());
        launchArgs.addAll(Arrays.asList(args));

        ProcessBuilder processBuilder = new ProcessBuilder(launchArgs);

        Map<String, String> env = processBuilder.environment();
        for(String prop : props.stringPropertyNames()) {
            env.put(prop, props.getProperty(prop));
        }
        processBuilder.directory(new File(System.getProperty("user.dir")));
        processBuilder.redirectErrorStream(true);

        Process serverProcess = processBuilder.start();

        Thread closeLauncher = new Thread(() -> serverProcess.destroyForcibly());
        Runtime.getRuntime().addShutdownHook(closeLauncher);

        return serverProcess;
    }

    public static void main(String[] args) throws Exception {
        Logging.enableLogging();

        File jarFile = new File(args[0]);
        if(!jarFile.exists()) {
            System.err.println("Couldn't find jar file " + args[0]);
            System.exit(1);
        }

        URLClassLoader classLoader = new URLClassLoader(new URL[] { Paths.get(args[0]).toUri().toURL() });

        Properties serverProperties = new Properties(args[0]);
        URLPatch.redefineURL(serverProperties.serverIP(), "" + serverProperties.serverPort());

        Class mainClass = null;

        // Custom
        if(args.length > 1) {
            try {
                mainClass = classLoader.loadClass(args[1]);
            } catch (ClassNotFoundException ex) { }
        }

        // Bukkit

        try {
            mainClass = classLoader.loadClass("org.bukkit.craftbukkit.Main");
        } catch (ClassNotFoundException ex) { }

        // Release
        try {
            mainClass = classLoader.loadClass("net.minecraft.server.Main");
        } catch (ClassNotFoundException ex) { }

        // Alpha/Beta/Release
        if(mainClass == null) {
            try {
                mainClass = classLoader.loadClass("net.minecraft.server.MinecraftServer");
            } catch (ClassNotFoundException ex1) { }
        }

        // Classic
        if(mainClass == null) {
            try {
                mainClass = classLoader.loadClass("com.mojang.minecraft.server.MinecraftServer");
            } catch (ClassNotFoundException ex1) { }
        }

        // Classic 0.0.15a remake
        if(mainClass == null) {
            try {
                mainClass = classLoader.loadClass("p000com.mojang.minecraft.server.MinecraftServer");
            } catch (ClassNotFoundException ex1) { }
        }

        if (mainClass == null) {
            System.err.println("Main class not found!");
        }

        Method main = mainClass.getMethod("main", String[].class);

        // Fixes the player list on Bukkit.
        System.setProperty("jline.terminal", "jline.UnsupportedTerminal");

        main.invoke(null, new Object[] { new String[] { "nogui" }});
    }
}
