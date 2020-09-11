package gg.codie.mineonline.api;

import org.json.JSONObject;

public class MineOnlinePlayerPresence {
    public String serverIP;
    public String serverPort;

    public MineOnlinePlayerPresence(JSONObject jsonObject) {
        if(jsonObject.has("serverIP"))
            serverIP = jsonObject.getString("serverIP");
        if(jsonObject.has("serverPort"))
            serverPort = jsonObject.getString("serverPort");
    }
}
