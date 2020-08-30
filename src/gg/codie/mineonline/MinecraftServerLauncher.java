package gg.codie.mineonline;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class MinecraftServerLauncher extends ServerLauncher {
    public static String serverlistAddress;
    public static String serverlistPort;

    public MinecraftServerLauncher(String[] args) throws Exception {
        super(args[0]);

        LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.JSON_JAR).toUri().toURL());
        LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.BYTEBUDDY_JAR).toUri().toURL());
        LibraryManager.addJarToClasspath(Paths.get(LauncherFiles.BYTEBUDDY_DEP_JAR).toUri().toURL());

        MinecraftVersion serverVersion = MinecraftVersionRepository.getSingleton().getVersionByMD5(md5);

        Process serverProcess = MinecraftServerProcess.startMinecraftServer(args);

        Thread closeLauncher = new Thread() {
            public void run() {
                serverProcess.destroy();
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

        scanner.close();

        serverProcess.destroyForcibly();
        System.exit(0);
    }

    public static void main(String[] args) throws Exception{

        File jarFile = new File(args[0]);
        if(!jarFile.exists()) {
            System.err.println("Couldn't find jar file " + args[0]);
            System.exit(1);
        }

        if(args.length < 2) {
            System.err.println("Too few arguments. Include a jar location and main class. \n Eg minecraft-server.jar com.mojang.minecraft.server.MinecraftServer");
        }

        new MinecraftServerLauncher(args);
    }
}
