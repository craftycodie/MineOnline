package gg.codie.mineonline.api;

import gg.codie.mineonline.Globals;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.LinkedList;

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

    public static JSONObject getVersionIndex() throws IOException {
        HttpURLConnection connection = null;

        URL url = new URL("http://" + Globals.API_HOSTNAME + "/mineonline/versions");
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

    public static JSONObject getSkinMetadata(String uuid) throws IOException {
        HttpURLConnection connection;

        URL url = new URL("http://" + Globals.API_HOSTNAME + "/mineonline/player/" + uuid + "/skin/metadata");
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

    public static void setSkinMetadata(String uuid, String token, JSONObject metadata) throws IOException {
        HttpURLConnection connection;

        String json = metadata.toString();

        URL url = new URL("http://" + Globals.API_HOSTNAME + "/mineonline/player/" + uuid + "/skin/metadata?session=" + URLEncoder.encode(token, "UTF-8"));
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

        rd.close();
    }

    public static String getVersionInfo(String path) throws IOException {
        HttpURLConnection connection = null;

        URL url = new URL("http://" + Globals.API_HOSTNAME + path.replace(" ", "%20"));
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

    public static LinkedList<MineOnlineServer> listServers(String uuid, String sessionId) throws IOException {
        HttpURLConnection connection = null;

        String parameters = "sessionId=" + URLEncoder.encode(sessionId, "UTF-8") + "&user=" + URLEncoder.encode(uuid, "UTF-8");
        URL url = new URL("http://" + Globals.API_HOSTNAME + "/mineonline/listservers.jsp?" + parameters);
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

    public static boolean removecloak(String sessionId) {
        HttpURLConnection connection = null;

        try {
            String parameters = "sessionId=" + URLEncoder.encode(sessionId, "UTF-8");
            URL url = new URL("http://" + Globals.API_HOSTNAME + "/mineonline/removecloak.jsp?" + parameters);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String res = rd.readLine();

            rd.close();

            return res.equals("ok");
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        } finally {

            if (connection != null)
                connection.disconnect();
        }
    }

    public static boolean uploadSkin(String uuid, String sessionId, InputStream skinFile) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL("http://" + Globals.API_HOSTNAME + "/mineonline/player/" + uuid + "/skin");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.getOutputStream().write(ByteBuffer.allocate(2).putShort((short)sessionId.length()).array());
            connection.getOutputStream().write(sessionId.getBytes(Charset.forName("UTF-8")));

            int skinSize = skinFile.available();

            connection.getOutputStream().write(ByteBuffer.allocate(4).putInt(skinSize).array());

            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            while ((bytes_read = skinFile.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    connection.getOutputStream().write(buffer[i]);
                }
            }

            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String res = rd.readLine();

            rd.close();

            return res.equals("ok");
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        } finally {

            if (connection != null)
                connection.disconnect();
        }

    }

    public static boolean uploadCloak(String uuid, String sessionId, InputStream cloakFile) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL("http://" + Globals.API_HOSTNAME + "/mineonline/player/" + uuid + "/cloak");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.getOutputStream().write(ByteBuffer.allocate(2).putShort((short)sessionId.length()).array());
            connection.getOutputStream().write(sessionId.getBytes(Charset.forName("UTF-8")));

            int cloakSize = cloakFile.available();

            connection.getOutputStream().write(ByteBuffer.allocate(4).putInt(cloakSize).array());

            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            while ((bytes_read = cloakFile.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    connection.getOutputStream().write(buffer[i]);
                }
            }

            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String res = rd.readLine();

            rd.close();

            return res.equals("ok");
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        } finally {

            if (connection != null)
                connection.disconnect();
        }

    }

    public static boolean listServer(
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
            String[] playerNamess
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
            jsonObject.put("players", playerNamess);

            String json = jsonObject.toString();

            URL url = new URL("http://" + Globals.API_HOSTNAME + "/mineonline/listserver.jsp");
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

            String res = rd.readLine();

            rd.close();

            return res.equals("ok");
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        } finally {

            if (connection != null)
                connection.disconnect();
        }
    }
}
