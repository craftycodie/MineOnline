package com.ahnewark.mineonline.api;

import com.ahnewark.mineonline.LauncherFiles;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class SavedServerRepository {
    JSONObject serversJSON = new JSONObject();

    private static SavedServerRepository singleton;

    public static SavedServerRepository getSingleton() {
        if(singleton == null) {
            singleton = new SavedServerRepository();
        }
        return singleton;
    }

    SavedServerRepository() {
        loadServers();
    }

    public LinkedList<SavedMinecraftServer> getServers() {
        return serversJSON.has("servers") ? SavedMinecraftServer.parseServers(serversJSON.optJSONArray("servers")) : new LinkedList<>();
    }

    public void loadServers() {

        try (FileInputStream input = new FileInputStream(LauncherFiles.MINEONLINE_SERVERS_FILE)) {
            // load a settings file
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for (int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char) buffer[i]);
                }
            }

            serversJSON = new JSONObject(stringBuffer.toString());
        } catch (IOException ex) {
            serversJSON = new JSONObject();
            serversJSON.put("servers", new JSONArray());
            saveServers();
        }

        if (!serversJSON.has("servers")) {
            serversJSON.put("servers", new JSONArray[0]);
            saveServers();
        }


        LinkedList<SavedMinecraftServer> servers = serversJSON.has("servers") ? SavedMinecraftServer.parseServers(serversJSON.optJSONArray("servers")) : new LinkedList<>();

        for(GotServersListener listener : listeners) {
            listener.GotServers(servers);
        }
    }

    public void addServer (SavedMinecraftServer server) {
        serversJSON.getJSONArray("servers").put(server.toJson());
        saveServers();
        loadServers();
    }

    public void deleteServer (int index) {
        serversJSON.getJSONArray("servers").remove(index);
        saveServers();
    }

    public void editServer (SavedMinecraftServer server, int index) {
        serversJSON.getJSONArray("servers").put(index, server.toJson());
        saveServers();
        loadServers();
    }

    private void saveServers() {
        try {
            FileWriter fileWriter = new FileWriter(LauncherFiles.MINEONLINE_SERVERS_FILE, false);
            fileWriter.write(serversJSON.toString());
            fileWriter.close();

            FileInputStream input = new FileInputStream(LauncherFiles.MINEONLINE_SERVERS_FILE);
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char)buffer[i]);
                }
            }

            input.close();

            serversJSON = new JSONObject(stringBuffer.toString());
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public interface GotServersListener {
        void GotServers(LinkedList<SavedMinecraftServer> servers);
    }

    private LinkedList<GotServersListener> listeners = new LinkedList<>();

    public void onGotServers(GotServersListener listener) {
        listeners.add(listener);
    }

    public void offGotServers(GotServersListener listener) {
        listeners.remove(listener);
    }
}
