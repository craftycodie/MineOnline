package gg.codie.mineonline;

import gg.codie.utils.OSUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class Startup {

    public static void main(String[] args) throws IOException, URISyntaxException {
        LibraryManager.extractLibraries();

        LinkedList<String> launchArgs = new LinkedList();
        launchArgs.add(OSUtils.getJREPath());
        launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
        launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
        launchArgs.add("-cp");
        launchArgs.add(LibraryManager.getClasspath(false, new String[] { new File(MineOnline.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(), LauncherFiles.DISCORD_RPC_JAR }));
        launchArgs.add(MineOnline.class.getCanonicalName());
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

        Process launcherProcess = processBuilder.start();

        Thread closeLauncher = new Thread(() -> launcherProcess.destroyForcibly());
        Runtime.getRuntime().addShutdownHook(closeLauncher);

        while (launcherProcess.isAlive()) {

        }

        System.exit(launcherProcess.exitValue());
    }

}
