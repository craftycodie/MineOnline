package com.johnymuffin;

import gg.codie.common.utils.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

        List<LegacyTrackerServer> ampServers = servers.stream().filter(server -> server.connectAddress.equals("mc.craftycodie.com")).collect(Collectors.toList());
        LegacyTrackerServer ampServer = ampServers.size() > 0 ? ampServers.get(0) : null;

        String serverIcon = ampServer.serverIcon;
        if (ampServer != null) {
            servers = new LinkedList(servers.stream().filter(server -> !server.connectAddress.equals("mc.craftycodie.com")).collect(Collectors.toList()));
            if (ampServer.serverIcon == null) {
                try {
                    InputStream iconStream = LegacyTrackerServer.class.getClassLoader().getResourceAsStream("textures/mineonline/gui/server-icon.png");
                    byte[] data = new byte[iconStream.available()];
                    iconStream.read(data);

                    serverIcon = Base64.getEncoder().encodeToString(data);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            ampServer = new LegacyTrackerServer(
                    ampServer.createdAt,
                    ampServer.connectAddress,
                    ampServer.ip,
                    ampServer.port,
                    ampServer.users,
                    ampServer.maxUsers,
                    ampServer.name,
                    ampServer.baseVersion,
                    ampServer.onlineMode,
                    ampServer.players,
                    ampServer.motd,
                    ampServer.dontListPlayers,
                    ampServer.featured,
                    ampServer.usingBetaEvolutions,
                    serverIcon,
                    ampServer.whitelisted
            );
            servers.push(ampServer);
        } else {
            try {
                InputStream iconStream = LegacyTrackerServer.class.getClassLoader().getResourceAsStream("textures/mineonline/gui/server-icon.png");
                byte[] data = new byte[iconStream.available()];
                iconStream.read(data);

                serverIcon = Base64.getEncoder().encodeToString(data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            servers.push(new LegacyTrackerServer(
                    "",
                    "mc.craftycodie.com",
                    "mc.craftycodie.com",
                    25565,
                    0,
                    64,
                    "Ampersand SMP &",
                    "b1.2_02",
                    true,
                    null,
                    "An Early-Beta, Vanilla SMP Experience!",
                    false,
                    false,
                    false,
                    serverIcon,
                    false
            ));
        }

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
