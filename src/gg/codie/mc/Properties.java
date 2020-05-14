package gg.codie.mc;

import java.io.*;
import java.util.Arrays;

public class Properties {

    public static java.util.Properties properties = new java.util.Properties();

    static {
        properties.setProperty("username", "");
        properties.setProperty("isPremium", "true");
        properties.setProperty("apiDomainName", "localhost");
        properties.setProperty("redirectedDomains", Arrays.toString(new String[] {"www.minecraft.net:-1", "mineraft.net", "www.minecraft.net", "s3.amazonaws.com"}));
        properties.setProperty("useLocalProxy", "true");
        properties.setProperty("serverIP", "");
        properties.setProperty("serverPort", "25565");
        properties.setProperty("joinServer", "false");
        properties.setProperty("jarFilePath", "");
        properties.setProperty("javaCommand", "java");
        properties.setProperty("baseUrl", "www.minecraft.net/game/");
    }

    public static void loadProperties() {
        try (InputStream input = new FileInputStream("mineonline.properties")) {
            // load a properties file
            properties.load(input);
            properties.setProperty("baseUrl", properties.getProperty("baseUrl").replace("_", ":"));
            properties.setProperty("redirectedDomains", properties.getProperty("redirectedDomains").replace("_", ":"));
        } catch (IOException ex) {

        }
    }

    public  static void saveProperties() {
        try (OutputStream output = new FileOutputStream("mineonline.properties")) {
            properties.setProperty("baseUrl", properties.getProperty("baseUrl").replace(":", "_"));
            properties.setProperty("redirectedDomains", properties.getProperty("redirectedDomains").replace(":", "_"));
            properties.store(output, null);
            properties.setProperty("baseUrl", properties.getProperty("baseUrl").replace("_", ":"));
            properties.setProperty("redirectedDomains", properties.getProperty("redirectedDomains").replace("_", ":"));
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}