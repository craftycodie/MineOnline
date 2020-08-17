package gg.codie.mineonline.api;

import gg.codie.mineonline.Session;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public class MineOnlineServerRepository {
    private LinkedList<MineOnlineServer> servers;
    private boolean failed = false;

    public LinkedList<MineOnlineServer> getServers() {
        return servers;
    }

    public boolean didFail() {
        return failed;
    }

    public void loadServers() {
        servers = null;
        failed = false;

        CompletableFuture.runAsync(() -> {
            try {
                servers = MineOnlineAPI.listServers(Session.session.getUuid(), Session.session.getSessionToken());
            } catch (Exception ex) {
                servers = new LinkedList<>();
                failed = true;
            }
            for(GotServersListener listener : listeners) {
                listener.GotServers(servers);
            }
        });
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
