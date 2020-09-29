package gg.codie.mineonline;

import gg.codie.mineonline.server.MinecraftServerLauncher;

import java.io.*;

public class Server {
    public static void main(String[] args) throws Exception{
        File jarFile = new File(args[0]);
        if(!jarFile.exists()) {
            System.err.println("Couldn't find jar file " + args[0]);
            System.exit(1);
        }

        LibraryManager.extractLibraries();
        LibraryManager.updateClasspath();

        MinecraftServerLauncher.main(args);
    }
}
