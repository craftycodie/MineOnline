package gg.codie.mineonline;

import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.utils.ArrayUtils;
import gg.codie.utils.MD5Checksum;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class Server {
    static String proxySet = "-DproxySet=true";
    static String proxyHost = "-Dhttp.proxyHost=127.0.0.1";
    static String proxyPortArgument = "-Dhttp.proxyPort=";

	public static void main(String[] args) throws Exception{
	    LibraryManager.extractLibraries();
	    LibraryManager.updateClasspath();

	    File jarFile = new File(args[0]);
	    if(!jarFile.exists()) {
	        System.err.println("Couldn't find jar file " + args[0]);
	        System.exit(1);
        }

        java.util.Properties serverProperties = new java.util.Properties();

	    try {
            loadServerProperties(serverProperties, args[0]);
        } catch (Exception ex) {

        }

	    String md5 = MD5Checksum.getMD5Checksum(jarFile.getPath());

        if(args.length < 2) {
            System.err.println("Too few arguments. Include a jar location and main class. \n Eg minecraft-server.jar com.mojang.minecraft.server.MinecraftServer");
        }

        Proxy.launchProxy();

        String[] CMD_ARRAY = new String[] {Properties.properties.getString("javaCommand"), proxySet, proxyHost, proxyPortArgument + Proxy.getProxyPort()};

        CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, Arrays.copyOfRange(args, 1, args.length));

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

        long lastPing = System.currentTimeMillis();
        while(serverProcess.isAlive()) {
            if (System.currentTimeMillis() - lastPing > 45000) {
                try {
                    if (!gotProperties) {
                        loadServerProperties(serverProperties, args[0]);
                    }

                    MinecraftAPI.listServer(
                            serverProperties.getProperty("server-ip"),
                            serverProperties.getProperty("server-port"),
                            0,
                            Integer.parseInt(serverProperties.getProperty("max-players")),
                            serverProperties.getProperty("server-name", "Untitled Server"),
                            serverProperties.getProperty("online-mode").equals("true"),
                            md5
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                lastPing = System.currentTimeMillis();
            }
        }

        Proxy.stopProxy();
	}

	static boolean gotProperties = false;
	private static void loadServerProperties(java.util.Properties properties, String jarPath) throws IOException {
	    properties.load(new FileInputStream(new File(jarPath.replace(Paths.get(jarPath).getFileName().toString(), "server.properties"))));
    }

}
