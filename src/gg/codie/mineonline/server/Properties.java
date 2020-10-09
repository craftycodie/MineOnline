package gg.codie.mineonline.server;

import java.io.IOException;

public class Properties extends gg.codie.minecraft.server.Properties {
    protected static final String SERVERLIST_IP = "serverlist-ip";
    protected static final String SERVERLIST_PORT = "serverlist-port";
    protected static final String OWNER = "owner";

    public Properties(String jarPath) throws IOException {
        super(jarPath);
    }

    public String serverIP() {
        return properties.getProperty(SERVERLIST_IP, super.serverIP());
    }

    public int serverPort() {
        if(properties.contains(SERVERLIST_PORT)) {
            try {
                return Integer.parseInt(properties.getProperty(SERVER_PORT, properties.getProperty(PORT, "25565")));
            } catch (NumberFormatException nfe) {
                return 25565;
            }
        } else {
            return super.serverPort();
        }
    }

    public String owner() {
        return properties.getProperty(OWNER, null);
    }

    public String versionMD5() {
        return properties.getProperty("version-md5", null);
    }
}
