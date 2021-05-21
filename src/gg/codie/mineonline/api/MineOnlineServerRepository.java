package gg.codie.mineonline.api;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.MinecraftVersionRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class MineOnlineServerRepository {
    JSONObject serversJSON = new JSONObject();

    private static MineOnlineServerRepository singleton;

    public static MineOnlineServerRepository getSingleton() {
        if(singleton == null) {
            singleton = new MineOnlineServerRepository();
        }
        return singleton;
    }

    MineOnlineServerRepository() {
        loadServers();
    }

    public LinkedList<MineOnlineServer> getServers() {
        return serversJSON.has("servers") ? MineOnlineServer.parseServers(serversJSON.optJSONArray("servers")) : new LinkedList<>();
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
            JSONArray servers = new JSONArray();
            JSONObject retroMC = new JSONObject();
            retroMC.put("name", "Retro MC");
            retroMC.put("address", "mc.retromc.org");
            retroMC.put("clientMD5", "EAE3353FDAA7E10A59B4CB5B45BFA10D");
            servers.put(retroMC);
            serversJSON.put("servers", servers);
            saveServers();
        }

        if (!serversJSON.has("servers")) {
            serversJSON.put("servers", new JSONArray[0]);
            saveServers();
        }


        LinkedList<MineOnlineServer> servers = serversJSON.has("servers") ? MineOnlineServer.parseServers(serversJSON.optJSONArray("servers")) : new LinkedList<>();

        for(GotServersListener listener : listeners) {
            listener.GotServers(servers);
        }
    }

    public void addServer (MineOnlineServer server) {
        serversJSON.getJSONArray("servers").put(server.toJson());
        saveServers();
        for(GotServersListener listener : listeners) {
            listener.GotServers(getServers());
        }
    }

    public void deleteServer (int index) {
        serversJSON.getJSONArray("servers").remove(index);
        saveServers();
    }

    public void editServer (MineOnlineServer server, int index) {
        serversJSON.getJSONArray("servers").put(index, server.toJson());
        saveServers();
        for(GotServersListener listener : listeners) {
            listener.GotServers(getServers());
        }
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
        void GotServers(LinkedList<MineOnlineServer> servers);
    }

    private LinkedList<GotServersListener> listeners = new LinkedList<>();

    public void onGotServers(GotServersListener listener) {
        listeners.add(listener);
    }

    public void offGotServers(GotServersListener listener) {
        listeners.remove(listener);
    }
}
