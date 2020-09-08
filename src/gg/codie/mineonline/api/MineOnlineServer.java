package gg.codie.mineonline.api;

import gg.codie.mineonline.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class MineOnlineServer {
    public final String createdAt;
    public final String ip;
    public final int port;
    public final int users;
    public final int maxUsers;
    public final String name;
    public final String md5;
    public final boolean isMineOnline;
    public final EMineOnlineServerStatus status;

    MineOnlineServer(String createdAt, String ip, int port, int users, int maxUsers, String name, String md5, boolean isMineOnline, EMineOnlineServerStatus status) {
        this.createdAt = createdAt;
        this.ip = ip;
        this.port = port;
        this.users = users;
        this.maxUsers = maxUsers;
        this.name = name;
        this.md5 = md5;
        this.isMineOnline = isMineOnline;
        this.status = status;
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
        EMineOnlineServerStatus status = EMineOnlineServerStatus.NONE;
        if(object.has("status")) {
            try {
                status = object.getEnum(EMineOnlineServerStatus.class, "status");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        return new MineOnlineServer(
                object.has("createdAt") && !object.isNull("createdAt") ? object.getString("createdAt") : null,
                object.has("ip") && !object.isNull("ip") ? object.getString("ip") : null,
                object.has("port") && !object.isNull("port") ? object.getInt("port") : 25565,
                object.getInt("users"),
                object.getInt("maxUsers"),
                object.getString("name"),
                object.getString("md5"),
                object.getBoolean("isMineOnline"),
                status
        );
    }
}
