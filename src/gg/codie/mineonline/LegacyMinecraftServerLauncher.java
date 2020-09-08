package gg.codie.mineonline;

import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.utils.ArrayUtils;

import java.io.*;
import java.util.*;

public class LegacyMinecraftServerLauncher extends ServerLauncher {


    public LegacyMinecraftServerLauncher(String[] args) throws Exception {
        super(args[0]);

        Startup.launchProxy();

        String[] CMD_ARRAY = new String[] {Settings.settings.getString(Settings.JAVA_COMMAND), Startup.PROXY_SET_ARG, Startup.PROXY_HOST_ARG, Startup.PROXY_PORT_ARG + Startup.getProxyPort()};

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
                Startup.stopProxy();
            }
        };

        Runtime.getRuntime().addShutdownHook(closeLauncher);

        redirectOutput(serverProcess.getInputStream(), System.out);

        OutputStream stdin = serverProcess.getOutputStream();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

        Scanner scanner = new Scanner(System.in);

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

            handleBroadcast(writer);
        }

        if (serverUUID != null)
            MineOnlineAPI.deleteServerListing(serverUUID);

        scanner.close();
        Startup.stopProxy();

        serverProcess.destroyForcibly();
        System.exit(0);
    }


    public static void main(String[] args) {
        File jarFile = new File(args[0]);
        if(!jarFile.exists()) {
            System.err.println("Couldn't find jar file " + args[0]);
            System.exit(1);
        }

        if(args.length < 2) {
            System.err.println("Too few arguments. Include a jar location and main class. \n Eg minecraft-server.jar com.mojang.minecraft.server.MinecraftServer");
        }

        try {
            new LegacyMinecraftServerLauncher(args);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
