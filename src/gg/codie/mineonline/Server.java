package gg.codie.mineonline;

import gg.codie.utils.ArrayUtils;

import java.io.File;
import java.util.Map;

public class Server {
    static String proxySet = "-DproxySet=true";
    static String proxyHost = "-Dhttp.proxyHost=127.0.0.1";
    static String proxyPortArgument = "-Dhttp.proxyPort=";

	public static void main(String[] args) throws Exception{
        Properties.loadProperties();

        if(args.length < 2) {
            System.err.println("Too few arguments. Include a jar location and main class. \n Eg minecraft-server.jar com.mojang.minecraft.server.MinecraftServer");
        }

        String[] CMD_ARRAY = Properties.properties.getBoolean("useLocalProxy")
                ? new String[] {Properties.properties.getString("javaCommand"), proxySet, proxyHost, proxyPortArgument + Proxy.getProxyPort()}
                : new String[] {Properties.properties.getString("javaCommand")};

        CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, args);

        java.util.Properties props = System.getProperties();
        System.out.println("Launching Server: " + String.join(" ", CMD_ARRAY));
        ProcessBuilder processBuilder = new ProcessBuilder(CMD_ARRAY);
        Map<String, String> env = processBuilder.environment();
        for(String prop : props.stringPropertyNames()) {
            env.put(prop, props.getProperty(prop));
        }
        processBuilder.directory(new File(System.getProperty("user.dir")));
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

        Process serverProcess = processBuilder.start();

        Thread closeLauncher = new Thread() {
            public void run() {
                serverProcess.destroy();
                Proxy.stopProxy();
            }
        };

        Runtime.getRuntime().addShutdownHook(closeLauncher);

        while(serverProcess.isAlive()) {

        }

        Proxy.stopProxy();
	}

}
