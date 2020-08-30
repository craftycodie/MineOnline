package gg.codie.mineonline;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;



public class Proxy {

    public static final String PROXY_SET_ARG = "-DproxySet=true";
    public static final String PROXY_HOST_ARG = "-Dhttp.proxyHost=127.0.0.1";
    public static final String PROXY_PORT_ARG = "-Dhttp.proxyPort=";

    private static ProxyThread proxyThread = null;

    public static int getProxyPort() {
        return proxyPort;
    }

    private static int proxyPort;

    public static void launchProxy() throws IOException {
        ServerSocket serverSocket = new ServerSocket(0);
        proxyThread = new ProxyThread(serverSocket);
        proxyThread.start();
        proxyPort = serverSocket.getLocalPort();
    }

    public static void stopProxy() {
        if (proxyThread != null) {
            proxyThread.stop();
            proxyThread = null;
        }
    }

    private static Process launcherProcess;

    public static void main(String[] args) throws IOException, URISyntaxException {
        boolean multiInstance = false;

        LibraryManager.extractLibraries();
        LibraryManager.updateClasspath();

        launchProxy();

        LinkedList<String> launchArgs = new LinkedList();
        launchArgs.add(Settings.settings.getString(Settings.JAVA_COMMAND));
        launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
        launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
        launchArgs.add("-cp");
        launchArgs.add(new File(Proxy.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
        launchArgs.add(MineOnline.class.getCanonicalName());
        launchArgs.add("" + proxyPort);
        launchArgs.addAll(Arrays.asList(args));

        // Start the proxy as a new process.
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

        }

        stopProxy();
        System.exit(0);
    }
}
