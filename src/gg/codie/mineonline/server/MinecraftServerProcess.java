package gg.codie.mineonline.server;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.patches.URLPatch;
import gg.codie.utils.ArrayUtils;
import gg.codie.utils.OSUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Map;

public class MinecraftServerProcess {

    public static final String PROP_AUTH_HOST = "minecraft.api.auth.host";
    public static final String PROP_ACCOUNT_HOST = "minecraft.api.account.host";
    public static final String PROP_SESSION_HOST = "minecraft.api.session.host";

    public static Process startMinecraftServer(String[] args) throws Exception {
        // Start the proxy as a new process.
        java.util.Properties props = System.getProperties();

        File jarFile = new File(args[0]);
        if(!jarFile.exists()) {
            System.err.println("Couldn't find jar file " + args[0]);
            System.exit(1);
        }

        String[] launchArgs = new String[] {
                OSUtils.getJREPath(),
                "-javaagent:" + LauncherFiles.PATCH_AGENT_JAR,
                "-Djava.util.Arrays.useLegacyMergeSort=true",
                "-cp",
                LibraryManager.getClasspath(false, new String[] { new File(MinecraftServerProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(), args[0]}),
                MinecraftServerProcess.class.getCanonicalName(),
        };

        ProcessBuilder processBuilder = new ProcessBuilder(ArrayUtils.concatenate(launchArgs, args));

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
        File jarFile = new File(args[0]);
        if(!jarFile.exists()) {
            System.err.println("Couldn't find jar file " + args[0]);
            System.exit(1);
        }

        URLClassLoader classLoader = new URLClassLoader(new URL[] { Paths.get(args[0]).toUri().toURL() });

        Properties serverProperties = new Properties(args[0]);
        URLPatch.redefineURL(serverProperties.serverIP(), "" + serverProperties.serverPort());

        Class mainClass = null;

        if(args.length > 1) {
            try {
                mainClass = classLoader.loadClass(args[1]);
            } catch (ClassNotFoundException ex) {
                System.out.println("Could not find main class " + args[1]);
                System.exit(1);
            }
        }

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

        if (mainClass == null) {
            System.out.println("Main class not found!");
        }

        Method main = mainClass.getMethod("main", String[].class);

        System.setProperty(PROP_AUTH_HOST, "http://" + Globals.API_HOSTNAME);
        System.setProperty(PROP_ACCOUNT_HOST, "http://" + Globals.API_HOSTNAME);
        System.setProperty(PROP_SESSION_HOST, "http://" + Globals.API_HOSTNAME);

        main.invoke(null, new Object[] { new String[] { "nogui" }});
    }
}
