package gg.codie.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class Properties {
    protected static final String WHITELIST = "white-list";
    protected static final String PUBLIC = "public";
    protected static final String MAX_PLAYERS = "max-players";
    protected static final String SERVER_NAME = "server-name";
    protected static final String ONLINE_MODE = "online-mode";
    protected static final String VERIFY_NAMES = "verify-names";
    protected static final String SERVER_IP = "server-ip";
    protected static final String SERVER_PORT = "server-port";
    protected static final String PORT = "port";
    protected static final String MOTD = "motd";

    protected java.util.Properties properties;

    public Properties(String jarPath) throws IOException {
        properties = new java.util.Properties();
        if (jarPath != null)
            properties.load(new FileInputStream(new File(jarPath.replace(Paths.get(jarPath).getFileName().toString(), "server.properties"))));
    }

    public boolean isWhitelisted() {
        return properties.getProperty(WHITELIST, "false").equals("true");
    }

    public boolean isPublic() {
        return properties.getProperty(PUBLIC, "true").equals("true");
    }

    public int maxPlayers() {
        try {
            return Integer.parseInt(properties.getProperty(MAX_PLAYERS, "24"));
        } catch (NumberFormatException nfe) {
            return 24;
        }
    }

    public String serverName() {
        return properties.getProperty(SERVER_NAME, "MineOnline Server");
    }

    public String motd() {
        return properties.getProperty(MOTD, null);
    }

    public boolean onlineMode() {
        return properties.getProperty(ONLINE_MODE, properties.getProperty(VERIFY_NAMES, "true")).equals("true");
    }

    public String serverIP() {
        return properties.getProperty(SERVER_IP, null);
    }

    public int serverPort() {
        try {
            return Integer.parseInt(properties.getProperty(SERVER_PORT, properties.getProperty(PORT, "25565")));
        } catch (NumberFormatException nfe) {
            return 25565;
        }
    }
}
