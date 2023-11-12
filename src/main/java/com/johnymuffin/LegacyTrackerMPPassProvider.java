package gg.johnymuffin;

import gg.codie.mineonline.api.IMPPassProvider;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LegacyTrackerMPPassProvider implements IMPPassProvider {
    @Override
    public String getMPPass(String serverIP, String serverPort, String username) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("serverIP", serverIP);
            jsonObject.put("serverPort", serverPort);
            jsonObject.put("username", username);

            String json = jsonObject.toString();

            URL url = new URL("https://servers.api.legacyminecraft.com/api/v1/mpPass");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            connection.getOutputStream().flush();
            connection.getOutputStream().close();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            JSONObject resObject = new JSONObject(response.toString());
            return resObject.has("mpPass") ? resObject.getString("mpPass") : null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
