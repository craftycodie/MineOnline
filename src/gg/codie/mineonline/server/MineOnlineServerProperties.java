package gg.codie.mineonline.server;

import gg.codie.minecraft.server.MinecraftServerProperties;

import java.io.IOException;

public class MineOnlineServerProperties extends MinecraftServerProperties {
    protected static final String SERVERLIST_IP = "serverlist-ip";
    protected static final String SERVERLIST_PORT = "serverlist-port";
    protected static final String VERSION_MD5 = "version-md5";
    protected static final String DISCORD_TOKEN = "discord-token";
    protected static final String DISCORD_CHANNEL = "discord-channel";
    protected static final String DISCORD_WEBHOOK_URL = "discord-webhook-url";
    protected static final String DONT_LIST_PLAYERS = "dont-list-players";
    protected static final String SERVERLIST_MOTD = "serverlist-motd";

    public MineOnlineServerProperties(String serverDir) throws IOException {
        super(serverDir);
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

    public String discordToken() {
        return getString(DISCORD_TOKEN, null);
    }

    public String discordChannel() {
        return getString(DISCORD_CHANNEL, null);
    }

    public String discordWebhookUrl() {
        return getString(DISCORD_WEBHOOK_URL, null);
    }

    public String versionMD5() {
        return getString(VERSION_MD5, null);
    }

    public boolean dontListPlayers() {
        return properties.getProperty(DONT_LIST_PLAYERS, "false").equals("true");
    }

    public String motd() {
        return getString(SERVERLIST_MOTD, null);
    }

}
