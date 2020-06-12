package gg.codie.mineonline;

import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.utils.ArrayUtils;
import gg.codie.utils.MD5Checksum;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Server {
    static String proxySet = "-DproxySet=true";
    static String proxyHost = "-Dhttp.proxyHost=127.0.0.1";
    static String proxyPortArgument = "-Dhttp.proxyPort=";

    static int users = 0;

	public static void main(String[] args) throws Exception{
	    LibraryManager.extractLibraries();
	    LibraryManager.updateClasspath();

	    System.out.println("MineOnline " + Globals.LAUNCHER_VERSION);

	    try {
            if (!Globals.LAUNCHER_VERSION.equals(MinecraftAPI.getLauncherVersion().replaceAll("\\s",""))) {
                System.out.println("An update is available!");
            }
        } catch (Exception ex) {

        }

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
        MinecraftVersionInfo.MinecraftVersion serverVersion = MinecraftVersionInfo.getVersionByMD5(md5);

        boolean hasHeartbeat = serverVersion != null && serverVersion.hasHeartbeat;

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
        processBuilder.redirectErrorStream(true);

        Process serverProcess = processBuilder.start();

        Thread closeLauncher = new Thread() {
            public void run() {
                serverProcess.destroy();
                Proxy.stopProxy();
            }
        };

        Runtime.getRuntime().addShutdownHook(closeLauncher);

        redirectOutput2(serverProcess.getInputStream(), System.out);

        OutputStream stdin = serverProcess.getOutputStream();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

        String whitelistPath = args[0].replace(Paths.get(args[0]).getFileName().toString(), "whitelist.txt");
        String bannedPath = args[0].replace(Paths.get(args[0]).getFileName().toString(), "banned-players.txt");
        String bannedIpsPath = args[0].replace(Paths.get(args[0]).getFileName().toString(), "banned-ips.txt");

        Scanner scanner = new Scanner(System.in);
        long lastPing = System.currentTimeMillis();
        while(serverProcess.isAlive()) {
            boolean reading = true;
            while (reading) {
                if (System.in.available() > 0) {
                    writer.write(scanner.nextLine());
                    writer.newLine();
                    writer.flush();
                } else {
                    reading = false;
                }
            }

            if (Boolean.parseBoolean(serverProperties.getProperty("public", "true")) && System.currentTimeMillis() - lastPing > 45000) {
                try {
                    String[] whitelistedPlayers = new String[0];
                    String[] whitelistedIPs = new String[0];
                    String[] bannedPlayers = readUsersFile(bannedPath);
                    String[] bannedIPs = readUsersFile(bannedIpsPath);

                    if (!gotProperties) {
                        loadServerProperties(serverProperties, args[0]);
                    }

                    if (!hasHeartbeat) {
                        writer.write("list");
                        writer.newLine();
                        writer.flush();
                    }

                    boolean whitelisted = false;
                    if (serverProperties.containsKey("white-list")) {
                        whitelisted = serverProperties.getProperty("white-list").equals("true");
                    }

                    if(whitelisted) {
                        whitelistedPlayers = readUsersFile(whitelistPath);
                    }

                    MinecraftAPI.listServer(
                            serverProperties.getProperty("server-ip", null),
                            serverProperties.getProperty("server-port", serverProperties.getProperty("port", "25565")),
                            hasHeartbeat ? -1 : users,
                            Integer.parseInt(serverProperties.getProperty("max-players")),
                            serverProperties.getProperty("server-name", "Untitled Server"),
                            serverProperties.getProperty("online-mode", serverProperties.getProperty("verify-names", "true")).equals("true"),
                            md5,
                            whitelisted,
                            whitelistedPlayers,
                            whitelistedIPs,
                            bannedPlayers,
                            bannedIPs
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }

                lastPing = System.currentTimeMillis();
            }
        }
//
        scanner.close();

        Proxy.stopProxy();
	}

	private static String[] readUsersFile(String path) {
        try {
            File usersFile = new File(path);
            if (usersFile.exists()) {
                LinkedList list = new LinkedList();
                BufferedReader reader = new BufferedReader(new FileReader(usersFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                reader.close();
                return (String[])list.toArray(new String[0]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new String[0];
    }

    private static void redirectOutput(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    byte[] buffer = new byte[1024];
                    for (int n = 0; n != -1; n = src.read(buffer)) {
                        dest.write(buffer, 0, n);
                    }
                    dest.flush();
                } catch (IOException e) { // just exit
                }
            }
        }).start();
    }

    private static void redirectOutput2(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                Scanner inScanner = new Scanner(src);
                try {
                    while(true) {
                        if(inScanner.hasNext()) {
                            String nextLine = inScanner.nextLine();
                            if(nextLine.length() > 27 && nextLine.substring(27).startsWith("Connected players: ")) {
                                users = 0;
                                if(nextLine.length() > 46){
                                    users = nextLine.split(",").length;
                                }
                            }
                            dest.write(nextLine.getBytes("UTF-8"));
                            dest.write("\n".getBytes());
                        }
                    }
                } catch (IOException e) { // just exit
                }
            }
        }).start();
    }

	static boolean gotProperties = false;
	private static void loadServerProperties(java.util.Properties properties, String jarPath) throws IOException {
	    properties.load(new FileInputStream(new File(jarPath.replace(Paths.get(jarPath).getFileName().toString(), "server.properties"))));
    }

}
