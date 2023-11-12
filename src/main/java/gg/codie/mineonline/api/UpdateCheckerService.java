package gg.codie.mineonline.api;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateCheckerService {
    public String getLauncherVersion() throws Exception {
        HttpURLConnection connection;

        URL url = new URL("https://api.github.com/repos/craftycodie/MineOnline/releases/latest");
        connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();

        return new JSONObject(response.toString()).getString("tag_name");
    }
}
