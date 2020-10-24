package gg.codie.mineonline.server;

import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.utils.Logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class MinecraftServerLauncher extends ServerLauncher {
    public MinecraftServerLauncher(String[] args) throws Exception {
        super(args[0]);

        Process serverProcess = MinecraftServerProcess.startMinecraftServer(args);

        Thread closeLauncher = new Thread() {
            public void run() {
                Runtime.getRuntime().halt(0);
            }
        };

        Runtime.getRuntime().addShutdownHook(closeLauncher);

        redirectOutput(serverProcess.getInputStream(), System.out);

        OutputStream stdin = serverProcess.getOutputStream();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

        if (!serverProperties.isPublic()) {
            System.out.println("Your server is not public and will not be shown on the server list.\n" +
                    "You can change this by setting public to true in the server.properties file.\n" +
                    "Players can connect via " + serverProperties.serverIP() + ":" + serverProperties.serverPort());
        }

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

            if(serverProperties.isPublic()) {
                handleBroadcast(writer);
            }
        }

        if (serverUUID != null)
            MineOnlineAPI.deleteServerListing(serverUUID);

        scanner.close();

        try {
            Runtime.getRuntime().halt(serverProcess.exitValue());
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            Runtime.getRuntime().halt(1);
        }
    }

    public static void main(String[] args) throws Exception{
        Logging.enableLogging();

        File jarFile = new File(args[0]);
        if(!jarFile.exists()) {
            System.err.println("Couldn't find jar file " + args[0]);
            System.exit(1);
        }

        new MinecraftServerLauncher(args);
    }
}
