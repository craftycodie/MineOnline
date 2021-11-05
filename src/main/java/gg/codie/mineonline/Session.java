package gg.codie.mineonline;

import gg.codie.mineonline.discord.DiscordRPCHandler;
import gg.codie.mineonline.utils.LastLogin;

import java.util.UUID;

public class Session {

    public static Session session;

    public String getUsername() {
        return username;
    }

    private String username;

    private String clientToken;

    public String getClientToken() {
        return clientToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    private String accessToken;

    public String getUuid() {
        return uuid;
    }

    private String uuid;

    public boolean isOnline() {
        return accessToken != null;
    }

    private boolean isPremium;

    public boolean isPremium() {
        return isPremium;
    }

    public Session(String username) {
        session = this;
        this.username = username;
        this.uuid = UUID.randomUUID().toString();
        this.isPremium = false;
    }

    public Session(String username, String accessToken, String clientToken, String uuid, boolean isPremium) {
        this(username, accessToken, uuid, isPremium);
        this.clientToken = clientToken;
    }

    public Session(String username, String accessToken, String uuid, boolean isPremium) {
        session = this;
        this.username = username;
        this.accessToken = accessToken;
        this.uuid = uuid;
        this.isPremium = isPremium;
        DiscordRPCHandler.updateSession(username);
    }

    public void logout() {
        session = null;
        LastLogin.deleteLastLogin();
    }
}
