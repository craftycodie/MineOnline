package gg.codie.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MinecraftServerProperties {
    protected static final String WHITELIST = "white-list";
    protected static final String PUBLIC = "public";
    protected static final String MAX_PLAYERS = "max-players";
    protected static final String SERVER_NAME = "server-name";
    protected static final String ONLINE_MODE = "online-mode";
    protected static final String VERIFY_NAMES = "verify-names";
    protected static final String SERVER_IP = "server-ip";
    protected static final String SERVER_PORT = "server-port";
    protected static final String PORT = "port";

    protected java.util.Properties properties;

    protected String getString(String key, String defaultValue) {
        String discordToken = properties.getProperty(key, defaultValue);
        if (discordToken == null || discordToken.isEmpty())
            return null;
        else
            return discordToken;
    }

    public MinecraftServerProperties(String serverDir) throws IOException {
        properties = new java.util.Properties();
        if (serverDir != null) {
            File propertiesFile = new File(serverDir + File.separator + "server.properties");
            if (propertiesFile.exists())
                properties.load(new FileInputStream(propertiesFile));
        }
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
        return getString(SERVER_NAME, "MineOnline Server");
    }

    public boolean onlineMode() {
        return properties.getProperty(ONLINE_MODE, properties.getProperty(VERIFY_NAMES, "true")).equals("true");
    }

    public String serverIP() {
        return getString(SERVER_IP, null);
    }

    public int serverPort() {
        try {
            return Integer.parseInt(properties.getProperty(SERVER_PORT, properties.getProperty(PORT, "25565")));
        } catch (NumberFormatException nfe) {
            return 25565;
        }
    }
}
