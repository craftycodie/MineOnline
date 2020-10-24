package gg.codie.mineonline;

import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.utils.JREUtils;
import gg.codie.mineonline.utils.Logging;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class Startup {
    public static void main(String[] args) throws IOException, URISyntaxException {
        Logging.deleteLog();
        Logging.enableLogging();

        LibraryManager.extractLibraries();

        LinkedList<String> launchArgs = new LinkedList();
        launchArgs.add(JREUtils.getRunningJavaExecutable());
        launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
        launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
        launchArgs.add("-cp");
        launchArgs.add(LibraryManager.getClasspath(true, new String[] { new File(MenuManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(), LauncherFiles.DISCORD_RPC_JAR }));
        launchArgs.add(MenuManager.class.getCanonicalName());
        launchArgs.addAll(Arrays.asList(args));

        java.util.Properties props = System.getProperties();
        ProcessBuilder processBuilder = new ProcessBuilder(launchArgs);

        Map<String, String> env = processBuilder.environment();
        for(String prop : props.stringPropertyNames()) {
            env.put(prop, props.getProperty(prop));
        }
        processBuilder.directory(new File(System.getProperty("user.dir")));

        processBuilder.inheritIO().start();

        Runtime.getRuntime().halt(0);
    }

}
