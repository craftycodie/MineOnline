package gg.codie.mineonline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Arrays;

public class Properties {

    public static JSONObject properties;

    static {
        if(new File(LauncherFiles.MINEONLINE_PROPS_FILE).exists()) {
            loadProperties();
        } else {
            properties = new JSONObject();
            properties.put("isPremium", true);
            properties.put("apiDomainName", "mineonline.codie.gg");
            properties.put("redirectedDomains", new String[] {"www.minecraft.net:-1", "mineraft.net", "www.minecraft.net", "s3.amazonaws.com"} );
            properties.put("useLocalProxy", true);
            properties.put("serverIP", "");
            properties.put("serverPort", 25565);
            properties.put("jarFilePath", "");
            properties.put("javaCommand", "java");
            properties.put("baseUrl", "www.minecraft.net:80/game/");
            properties.put("minecraftInstalls", new JSONArray());

            saveProperties();
        }
    }

    public static void loadProperties() {
        try (FileInputStream input = new FileInputStream(LauncherFiles.MINEONLINE_PROPS_FILE)) {
            // load a properties file
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char)buffer[i]);
                }
            }

            properties = new JSONObject(stringBuffer.toString());
        } catch (IOException ex) {
            saveProperties();
        }
    }

    public  static void saveProperties() {
        try {
            FileWriter fileWriter = new FileWriter(LauncherFiles.MINEONLINE_PROPS_FILE);
            fileWriter.write(properties.toString());
            fileWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
