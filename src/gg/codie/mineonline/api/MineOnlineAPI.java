package gg.codie.mineonline.api;

import gg.codie.mineonline.Globals;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class MineOnlineAPI {
    public static String getMpPass(String sessionId, String username, String useruuid, String serverIP, String serverPort) {

        try {
            InetAddress inetAddress = InetAddress.getByName(serverIP);
            serverIP = inetAddress.getHostAddress();
        } catch (Exception ex) {
            //ignore.
        }

        HttpURLConnection connection = null;

        try {
            String parameters = "sessionId=" + URLEncoder.encode(sessionId, "UTF-8") + "&serverIP=" + URLEncoder.encode(serverIP, "UTF-8") + "&serverPort=" + URLEncoder.encode(serverPort, "UTF-8") + "&username=" + URLEncoder.encode(username, "UTF-8") + "&uuid=" + URLEncoder.encode(useruuid, "UTF-8");
            URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/servertoken?" + parameters);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.connect();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String mpPass = rd.readLine();

            rd.close();

            return mpPass;
        } catch (Exception e) {
            if (e.getClass() != FileNotFoundException.class)
                e.printStackTrace();
            return "0";
        } finally {

            if (connection != null)
                connection.disconnect();
        }
    }

    public static String getExternalIP() {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/getmyip");
            connection = (HttpURLConnection) url.openConnection();
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

            return new JSONObject(response.toString()).getString("ip");

        } catch (Exception ex) {
            return "";
        }
    }

    public static JSONObject getVersionIndex() throws IOException {
        HttpURLConnection connection = null;

        URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/versions");
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

        JSONObject jsonObject = new JSONObject(response.toString());

        if (connection != null)
            connection.disconnect();

        return jsonObject;
    }

    public static void deleteServerListing(String uuid) throws IOException {
        HttpURLConnection connection;

        URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/servers/" + uuid);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.connect();

        if (connection != null)
            connection.disconnect();
    }

    public static String getVersionInfo(String path) throws IOException {
        HttpURLConnection connection;

        URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + path.replace(" ", "%20"));
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

        if (connection != null)
            connection.disconnect();

        return response.toString();
    }

    public static LinkedList<MineOnlineServer> listServers(String sessionId) throws IOException {
        HttpURLConnection connection;

        String parameters = "sessionId=" + URLEncoder.encode(sessionId, "UTF-8");
        URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/servers?" + parameters);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(false);
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

        JSONArray jsonArray = new JSONArray(response.toString());

        if (connection != null)
            connection.disconnect();

        return MineOnlineServer.parseServers(jsonArray);
    }

    public static MineOnlineServer getServer(String serverIP, String serverPort) throws IOException {
        HttpURLConnection connection;

        String parameters = "serverIP=" + URLEncoder.encode(serverIP, "UTF-8") + "&serverPort=" + URLEncoder.encode(serverPort, "UTF-8");
        URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/getserver?" + parameters);
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(false);
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

        JSONObject jsonObject = new JSONObject(response.toString());

        if (connection != null)
            connection.disconnect();

        return MineOnlineServer.parseServer(jsonObject);
    }

    public static String listServer(
            String ip,
            String port,
            int users,
            int maxUsers,
            String name,
            boolean onlineMode,
            String md5,
            boolean whitelisted,
            String[] whitelistUsers,
            String[] whitelistIPs,
            String[] whitelistUUIDs,
            String[] bannedUsers,
            String[] bannedIPs,
            String[] bannedUUIDs,
            String owner,
            String[] playerNames
    ) {
        HttpURLConnection connection = null;

        try {
            JSONObject jsonObject = new JSONObject();
            if (ip != null)
                jsonObject.put("ip", ip);
            jsonObject.put("port", port);
            if (users > -1)
                jsonObject.put("users", users);
            jsonObject.put("max", maxUsers);
            jsonObject.put("name", name);
            jsonObject.put("onlinemode", onlineMode);
            jsonObject.put("md5", md5);
            jsonObject.put("whitelisted", whitelisted);
            jsonObject.put("whitelistUsers", whitelistUsers);
            jsonObject.put("whitelistIPs", whitelistIPs);
            jsonObject.put("whitelistUUIDs", whitelistUUIDs);
            jsonObject.put("bannedUsers", bannedUsers);
            jsonObject.put("bannedIPs", bannedIPs);
            jsonObject.put("bannedUUIDs", bannedUUIDs);
            if(owner != null)
                jsonObject.put("owner", owner);
            jsonObject.put("players", playerNames);

            String json = jsonObject.toString();

            URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/api/servers");
            connection = (HttpURLConnection) url.openConnection();
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
            return resObject.has("uuid") ? resObject.getString("uuid") : null;
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        } finally {

            if (connection != null)
                connection.disconnect();
        }
    }

    public static String getLauncherVersion() throws IOException {
        HttpURLConnection connection;

        URL url = new URL(Globals.API_PROTOCOL + Globals.API_HOSTNAME + "/launcherversion");
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

        return response.toString();
    }
}
