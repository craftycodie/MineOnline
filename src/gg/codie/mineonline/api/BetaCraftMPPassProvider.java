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
            URL url = new URL("https://betacraft.pl/server.jsp");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent", "Java/1.8.0_265");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.writeUTF(username);
            output.writeUTF(betacraftPayload);
            output.flush();
            output.close();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
                response.append(line);
            }
            rd.close();

            String mpPassPrefix = "join://" + serverIP + ":" + serverPort + "/";

            String mpPass = response.toString().substring(response.indexOf(mpPassPrefix) + mpPassPrefix.length());
            mpPass = mpPass.substring(0, mpPass.indexOf("/"));

            if (mpPass.equals("-"))
                mpPass = null;

            return mpPass;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
