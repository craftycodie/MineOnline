package gg.codie.mineonline.protocol;

import gg.codie.minecraft.api.SessionServer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckServerURLConnection extends HttpURLConnection {
    public CheckServerURLConnection(URL url) {
        super(url);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    private String response = "bad login";

    @Override
    public void connect() throws IOException {
        String username = null;
        String serverId = null;
        String ip = null;

        String[] args = url.toString()
                .substring(url.toString().lastIndexOf("?") + 1)
                .split("&");
        for (String arg : args) {
            String[] keyValue = arg.split("=");

            if (keyValue[0].equals("user"))
                username = keyValue[1];
            else if (keyValue[0].equals("serverId"))
                serverId = keyValue[1];
            else if (keyValue[0].equals("ip"))
                ip = keyValue[1];
        }

        if (username == null || serverId == null) {
            return;
        }

        boolean validJoin = SessionServer.hasJoined(username, serverId, ip);

        if (validJoin) {
            response = "YES";
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(response.getBytes());
    }

    @Override
    public int getResponseCode() {
        return 200;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }
}
