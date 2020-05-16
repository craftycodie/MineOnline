package gg.codie.mc;

import gg.codie.utils.ArrayUtils;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
