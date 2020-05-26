package gg.codie.mineonline;

import java.io.*;
import java.net.*;

public class Proxy {

    private static ProxyThread proxyThread = null;

    public static int getProxyPort() {
        return proxyPort;
    }

    private static int proxyPort;

    public static void launchProxy() throws IOException {
        if (!Properties.properties.getBoolean("useLocalProxy"))
            proxyPort = 0;

        ServerSocket serverSocket = new ServerSocket(0);
        proxyThread = new ProxyThread(serverSocket);
        proxyThread.start();
        proxyPort = serverSocket.getLocalPort();
    }

    public static void stopProxy() {
        if (proxyThread != null) {
            proxyThread.stop();
            proxyThread = null;
        }
    }

    public static void main(String[] args) throws IOException {
        launchProxy();
    }



}
