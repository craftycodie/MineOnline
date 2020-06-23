package gg.codie.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class Properties {

    public static java.util.Properties loadServerProperties(String jarPath) throws IOException {
        java.util.Properties properties = new java.util.Properties();
        properties.load(new FileInputStream(new File(jarPath.replace(Paths.get(jarPath).getFileName().toString(), "server.properties"))));
        return properties;
    }

}
