package gg.codie.mineonline;

import java.io.*;
import java.net.*;

public class Proxy {

    private static ProxyThread proxyThread = null;

    public static int launchProxy() throws IOException {
        if (!Boolean.parseBoolean(Properties.properties.getProperty("useLocalProxy")))
            return 0;

        ServerSocket serverSocket = new ServerSocket(0);
        proxyThread = new ProxyThread(serverSocket);
        proxyThread.start();
        return serverSocket.getLocalPort();
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
