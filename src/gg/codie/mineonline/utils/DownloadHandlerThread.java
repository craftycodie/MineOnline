package gg.codie.mineonline.utils;

import java.io.IOException;

import static gg.codie.mineonline.api.MineOnlineAPI.getVersionInfo;

public class DownloadHandlerThread extends Thread {
    private final String manifestURL;
    private final String version;
    private volatile boolean completed = false;

    private Object dataLock = new Object();
    private Object data;


    public DownloadHandlerThread(String manifestURL, String version) {
        this.manifestURL = manifestURL;
        this.version = version;
    }


    @Override
    public void run() {
        try {
            String versionManifestRaw = getVersionInfo(manifestURL);
            synchronized (dataLock) {
                this.data = versionManifestRaw;
                this.completed = true;
            }
        } catch (Exception exception) {
            synchronized (dataLock) {
                this.data = exception;
                this.completed = true;
            }
        }
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public Object getData() {
        return this.data;
    }

    public String getManifestURL() {
        return manifestURL;
    }

    public String getVersion() {
        return version;
    }
}