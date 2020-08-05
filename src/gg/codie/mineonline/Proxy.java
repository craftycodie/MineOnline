package gg.codie.mineonline;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.*;
import java.util.Map;



public class Proxy {

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
//        System.out.println(String.format("%02x", (byte)0xE));

        boolean multiInstance = false;

        for(String arg : args) {
            if (arg.equals("-multiinstance"))
                multiInstance = true;
        }

        LibraryManager.extractLibraries();
        LibraryManager.updateClasspath();

        launchProxy();

        // Start the proxy as a new process.
        java.util.Properties props = System.getProperties();
        ProcessBuilder processBuilder = new ProcessBuilder(
                Settings.settings.getString(Settings.JAVA_COMMAND),
                "-javaagent:" + LauncherFiles.PATCH_AGENT_JAR,
                "-Djava.util.Arrays.useLegacyMergeSort=true",
                multiInstance ? "-Dmineonline.multiinstance=true" : "",
                "-cp",
                new File(Proxy.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(),
                MineOnline.class.getCanonicalName(),
                "" + proxyPort);

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
