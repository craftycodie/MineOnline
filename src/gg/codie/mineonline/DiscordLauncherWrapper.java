package gg.codie.mineonline;

import gg.codie.mineonline.discord.DiscordRPCHandler;
import gg.codie.utils.OSUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class DiscordLauncherWrapper {
    private static Process launcherProcess;

    static String discordJoin = null;

    public static void joinDiscord(String serverAddress) {
        discordJoin = serverAddress;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
//        LibraryManager.updateClasspath();

        DiscordRPCHandler.initialize();

        LinkedList<String> launchArgs = new LinkedList();
        launchArgs.add(OSUtils.getJREPath());
        launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
        launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
        launchArgs.add("-cp");
        launchArgs.add(LibraryManager.getClasspath(true, new String[] { new File(MineOnline.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() }));
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

        launcherProcess = processBuilder.start();

        Thread closeLauncher = new Thread(() -> launcherProcess.destroyForcibly());
        Runtime.getRuntime().addShutdownHook(closeLauncher);

        while(launcherProcess.isAlive()) {
            if(discordJoin != null) {
                launcherProcess.destroyForcibly();

                LinkedList<String> joinArgs = (LinkedList<String>)launchArgs.clone();
                joinArgs.add("-joinserver");
                joinArgs.add(discordJoin);
                ProcessBuilder joinProcessBuilder = new ProcessBuilder(joinArgs);
                joinProcessBuilder.directory(new File(System.getProperty("user.dir")));
                joinProcessBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                joinProcessBuilder.redirectErrorStream(true);
                joinProcessBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

                launcherProcess = joinProcessBuilder.start();

                Runtime.getRuntime().removeShutdownHook(closeLauncher);
                closeLauncher = new Thread(() -> launcherProcess.destroyForcibly());
                Runtime.getRuntime().addShutdownHook(closeLauncher);

                discordJoin = null;
            }
        }

        System.exit(0);
    }
}
