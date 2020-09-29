package gg.codie.mineonline;

import gg.codie.mineonline.server.MinecraftServerLauncher;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class Server {
    public static void main(String[] args) throws Exception{
        File jarFile = new File(args[0]);
        if(!jarFile.exists()) {
            System.err.println("Couldn't find jar file " + args[0]);
            System.exit(1);
        }

        LibraryManager.extractLibraries();

        LinkedList<String> launchArgs = new LinkedList();
        launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
        launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
        launchArgs.add("-cp");
        launchArgs.add(LibraryManager.getClasspath(false, new String[] { new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() }));
        launchArgs.add(MinecraftServerLauncher.class.getCanonicalName());
        launchArgs.addAll(Arrays.asList(args));

        java.util.Properties props = System.getProperties();
        ProcessBuilder processBuilder = new ProcessBuilder(launchArgs);

        Map<String, String> env = processBuilder.environment();
        for(String prop : props.stringPropertyNames()) {
            env.put(prop, props.getProperty(prop));
        }
        processBuilder.directory(new File(System.getProperty("user.dir")));
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

        Process serverProcess = processBuilder.start();

        Thread closeLauncher = new Thread(() -> serverProcess.destroyForcibly());
        Runtime.getRuntime().addShutdownHook(closeLauncher);

        while (serverProcess.isAlive()) {

        }

        System.exit(serverProcess.exitValue());
    }
}
