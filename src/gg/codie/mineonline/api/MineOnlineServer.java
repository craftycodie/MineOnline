package gg.codie.mineonline.api;

import gg.codie.common.utils.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;

public class MineOnlineServer {
    public final String createdAt;
    public final String ip;
    public final String connectAddress;
    public final int port;
    public final int users;
    public final int maxUsers;
    public final String name;
    public final String md5;
    public final boolean isMineOnline;
    public final boolean onlineMode;
    public final String[] players;
    public final String motd;
    public final boolean dontListPlayers;
    public final boolean featured;
    public final boolean usingBetaEvolutions;
    public final String serverIcon;
    public final boolean whitelisted;

    MineOnlineServer(String createdAt, String connectAddress, String ip, int port, int users, int maxUsers, String name, String md5, boolean isMineOnline, boolean onlineMode, String[] players, String motd, boolean dontListPlayers, boolean featured, boolean usingBetaEvolutions, String serverIcon, boolean whitelisted) {
        this.createdAt = createdAt;
        this.connectAddress = connectAddress;
        this.ip = ip;
        this.port = port;
        this.users = users;
        this.maxUsers = maxUsers;
        this.name = name;
        this.md5 = md5;
        this.isMineOnline = isMineOnline;
        this.onlineMode = onlineMode;
        this.players = players;
        this.motd = motd;
        this.dontListPlayers = dontListPlayers;
        this.featured = featured;
        this.usingBetaEvolutions = usingBetaEvolutions;
        this.serverIcon = serverIcon;
        this.whitelisted = whitelisted;
    }

    public static LinkedList<MineOnlineServer> parseServers(JSONArray jsonArray) {
        Iterator<Object> iterator = jsonArray.iterator();

        LinkedList<MineOnlineServer> servers = new LinkedList();

        while(iterator.hasNext()) {
            JSONObject object = (JSONObject)iterator.next();

            try {
                servers.add(parseServer(object));
            } catch (JSONException ex) {
                ex.printStackTrace();
                // Continue to next element.
            }
        }

        return servers;
    }

    public static MineOnlineServer parseServer(JSONObject object) throws JSONException {
        return new MineOnlineServer(
                object.has("createdAt") && !object.isNull("createdAt") ? object.getString("createdAt") : null,
                object.optString("connectAddress", object.optString("ip", null)),
                object.has("ip") && !object.isNull("ip") ? object.getString("ip") : null,
                object.has("port") && !object.isNull("port") ? object.getInt("port") : 25565,
                object.getInt("users"),
                object.getInt("maxUsers"),
                object.getString("name"),
                object.getString("md5"),
                object.getBoolean("isMineOnline"),
                object.getBoolean("onlinemode"),
                object.has("players") ? JSONUtils.getStringArray(object.getJSONArray("players")) : new String[0],
                object.optString("motd", null),
                object.optBoolean("dontListPlayers", false),
                object.optBoolean("featured", false),
                object.getBoolean("useBetaEvolutionsAuth"),
                object.optString("serverIcon", null),
                object.optBoolean("whitelisted", false)
        );
    }
}
