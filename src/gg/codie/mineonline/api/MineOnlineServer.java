package gg.codie.mineonline.api;

import gg.codie.mineonline.gui.MinecraftInstall;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MineOnlineServer {
    public final String createdAt;
    public final String ip;
    public final int port;
    public final int users;
    public final int maxUsers;
    public final String name;
    public final boolean onlineMode;
    public final String md5;
    public final boolean isPublic;
    public final boolean isMineOnline;

    MineOnlineServer(String createdAt, String ip, int port, int users, int maxUsers, String name, boolean onlineMode, String md5, boolean isPublic, boolean isMineOnline) {
        this.createdAt = createdAt;
        this.ip = ip;
        this.port = port;
        this.users = users;
        this.maxUsers = maxUsers;
        this.name = name;
        this.onlineMode = onlineMode;
        this.md5 = md5;
        this.isPublic = isPublic;
        this.isMineOnline = isMineOnline;
    }

    public static LinkedList<MineOnlineServer> getServers(JSONArray jsonArray) {
        Iterator<Object> iterator = jsonArray.iterator();

        LinkedList<MineOnlineServer> servers = new LinkedList();

        while(iterator.hasNext()) {
            JSONObject object = (JSONObject)iterator.next();

            try {
                servers.add(new MineOnlineServer(
                        object.has("createdAt") && !object.isNull("createdAt") ? object.getString("createdAt") : null,
                        object.getString("ip"),
                        object.getInt("port"),
                        object.getInt("users"),
                        object.getInt("maxUsers"),
                        object.getString("name"),
                        object.getBoolean("onlinemode"),
                        object.getString("md5"),
                        object.getBoolean("public"),
                        object.getBoolean("isMineOnline")
                ));
            } catch (JSONException ex) {
                ex.printStackTrace();
                // Continue to next element.
            }
        }

        return servers;
    }
}
