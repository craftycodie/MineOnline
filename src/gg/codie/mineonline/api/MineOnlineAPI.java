package gg.codie.mineonline.api;

import gg.codie.mineonline.Globals;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MineOnlineAPI {
    public static String playeruuid(String username, String token) throws IOException {
        HttpURLConnection connection = null;

        String parameters = "session=" + URLEncoder.encode(token, "UTF-8");

        URL url = new URL("http://" + Globals.API_HOSTNAME + "/mineonline/playeruuid/" + username + "?" + parameters);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        connection.setRequestProperty("Content-Length", Integer.toString((parameters.getBytes()).length));
        connection.setRequestProperty("Content-Language", "en-US");

        connection.setUseCaches(false);
        connection.setDoOutput(true);

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }
        rd.close();

        if (connection != null)
            connection.disconnect();

        return new JSONObject(response.toString()).getString("uuid");
    }
}
