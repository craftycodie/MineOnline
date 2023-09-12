package com.johnymuffin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.StreamSupport;

public class LegacyTrackerServer {
    public final String createdAt;
    public final String ip;
    public final String connectAddress;
    public final int port;
    public final int users;
    public final int maxUsers;
    public final String name;
    public final String baseVersion;
    public final boolean onlineMode;
    public final String[] players;
    public final String motd;
    public final boolean dontListPlayers;
    public final boolean featured;
    public final boolean usingBetaEvolutions;
    public final String serverIcon;
    public final boolean whitelisted;

    LegacyTrackerServer(String createdAt, String connectAddress, String ip, int port, int users, int maxUsers, String name, String baseVersion, boolean onlineMode, String[] players, String motd, boolean dontListPlayers, boolean featured, boolean usingBetaEvolutions, String serverIcon, boolean whitelisted) {
        this.createdAt = createdAt;
        this.connectAddress = connectAddress;
        this.ip = ip;
        this.port = port;
        this.users = users;
        this.maxUsers = maxUsers;
        this.name = name;
        this.baseVersion = baseVersion;
        this.onlineMode = onlineMode;
        this.players = players;
        this.motd = motd;
        this.dontListPlayers = dontListPlayers;
        this.featured = featured;
        this.usingBetaEvolutions = usingBetaEvolutions;
        this.serverIcon = serverIcon;
        this.whitelisted = whitelisted;
    }

    public static LinkedList<LegacyTrackerServer> parseServers(JSONArray jsonArray) {
        Iterator<Object> iterator = jsonArray.iterator();

        LinkedList<LegacyTrackerServer> servers = new LinkedList<>();

        while(iterator.hasNext()) {
            JSONObject object = (JSONObject)iterator.next();

            try {
                servers.add(parseServer(object));
            } catch (JSONException ex) {
                ex.printStackTrace();
                // Continue to next element.
            }
        }

        servers.sort((LegacyTrackerServer server, LegacyTrackerServer otherServer) -> otherServer.users - server.users);

        return servers;
    }

    public static LegacyTrackerServer parseServer(JSONObject object) throws JSONException {
        String[] playerNames = StreamSupport.stream(object.getJSONArray("players").spliterator(), false).map((Object playerObject) -> ((JSONObject)playerObject).getString("username")).toArray(String[]::new);

        return new LegacyTrackerServer(
                object.has("createdAt") && !object.isNull("createdAt") ? object.getString("createdAt") : null,
                object.optString("serverIP", object.optString("numericalIP", null)),
                object.has("numericalIP") && !object.isNull("numericalIP") ? object.getString("numericalIP") : null,
                object.has("serverPort") && !object.isNull("serverPort") ? object.getInt("serverPort") : 25565,
                object.getInt("onlinePlayers"),
                object.getInt("maxPlayers"),
                object.getString("serverName"),
                object.getString("serverVersion").toLowerCase(),
                object.getBoolean("onlineMode"),
                playerNames,
                object.optString("serverDescription", null),
                object.optBoolean("dontListPlayers", false),
                object.optBoolean("featured", false),
                object.optBoolean("useBetaEvolutionsAuth", false),
                object.optString("serverIcon", null),
                object.optBoolean("whitelisted", false)
        );
    }
}
