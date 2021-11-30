package gg.codie.mineonline.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BetaCraftMPPassProvider implements IMPPassProvider {
    @Override
    public String getMPPass(String serverIP, String serverPort, String username) {
        try {
            URL url = new URL("https://api.betacraft.uk/getmppass.jsp?user=" + username + "&server=" + serverIP + ":" + serverPort);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            return rd.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
