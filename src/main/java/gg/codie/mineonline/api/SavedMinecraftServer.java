package gg.codie.mineonline.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedList;

public class SavedMinecraftServer {
    public String address;
    public String name;
    public String clientMD5;


    public SavedMinecraftServer() {
        name = "Minecraft Server";
        address = "";
    }

    public SavedMinecraftServer(String address, String name, String md5) {
        this.address = address;
        this.name = name;
        this.clientMD5 = md5;
    }

    public static LinkedList<SavedMinecraftServer> parseServers(JSONArray jsonArray) {
        Iterator<Object> iterator = jsonArray.iterator();

        LinkedList<SavedMinecraftServer> servers = new LinkedList();

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

    public static SavedMinecraftServer parseServer(JSONObject object) throws JSONException {
        return new SavedMinecraftServer(
                object.optString("address", object.optString("address", null)),
                object.getString("name"),
                object.getString("clientMD5")
        );
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("address", address);
        jsonObject.put("name", name);
        jsonObject.put("clientMD5", clientMD5);
        return jsonObject;
    }
}
