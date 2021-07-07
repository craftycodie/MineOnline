package gg.codie.mineonline.api;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.Session;
import org.json.JSONObject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BetaCraftMPPassProvider implements IMPPassProvider {
    @Override
    public String getMPPass(String serverIP, String serverPort, String username) {
        // TODO: Remove the access token whenever Moresteck gets around to it.
        String betacraftPayload = "token:" + Session.session.getAccessToken() + ":" + Session.session.getUuid();

        try {
            URL url = new URL("https://betacraft.pl/api/getmppass.jsp?user=" + username + "&server=" + serverIP + ":" + serverPort);
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
