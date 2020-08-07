package gg.codie.mineonline;

import gg.codie.mineonline.patches.URLConstructAdvice;
import gg.codie.utils.ArrayUtils;
import gg.codie.utils.JSONUtils;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyThread implements Runnable {
    private AtomicReference<ServerSocket> serverSocket = new AtomicReference<>();
    private Thread worker;
    private AtomicBoolean running = new AtomicBoolean(false);

    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
    }

    boolean isRunning() {
        return running.get();
    }

    public ProxyThread(ServerSocket serverSocket) {
        this.serverSocket.set(serverSocket);
    }

    @Override
    public void run() {
        running.set(true);
        System.setProperty("java.net.preferIPv4Stack", "true");

        Settings.loadSettings();
        String[] redirectedDomains = Globals.REDIRECTED_DOMAINS;

        Socket clientSocket = null;

        Settings.loadSettings();
        if (Settings.settings.has(Settings.PROXY_LOGGING) && Settings.settings.getBoolean(Settings.PROXY_LOGGING))
            System.out.println(serverSocket.get().getInetAddress() + ":" + serverSocket.get().getLocalPort());

        while (running.get()) {
            //Stream to put data to the browser
            PrintWriter outGoing = null;
            try {
                clientSocket = serverSocket.get().accept();
                outGoing = new PrintWriter(clientSocket.getOutputStream(), true);

                final int bufferSize = 8096;

                byte[] buffer = new byte[bufferSize];
                int bytes_read;
                LinkedList<Byte> request = new LinkedList();

                bytes_read = clientSocket.getInputStream().read(buffer, 0, bufferSize);
                for(int i = 0; i < bytes_read; i++)
                    request.add(buffer[i]);

                String requestHeaders = new String(buffer).split("\r\n\r\n")[0];

                // keep reading.
                String requestString = new String(buffer);
                for (String header : requestHeaders.split("\r\n")) {
                    if(header.contains("Content-Length")) {
                        int contentLength = Integer.parseInt(header.split(": ")[1]);
                        int headerLength = requestString.substring(0, requestString.indexOf("\r\n\r\n") + 4).length();

                        while(request.size() < contentLength + headerLength) {
                            bytes_read = clientSocket.getInputStream().read(buffer, 0, bufferSize);
                            for(int i = 0; i < bytes_read; i++)
                                request.add(buffer[i]);
                        }

                        break;
                    }
                }

                byte[] requestBytes = ArrayUtils.toPrimitives(request.toArray(new Byte[0]));
                requestString = new String(requestBytes);

                int headerSize = requestString.split("\r\n\r\n")[0].length() + 4;
                for (String redirectedDomain : redirectedDomains) {
                    requestString = requestString.replace(redirectedDomain, Globals.API_HOSTNAME);
                }

                Settings.loadSettings();
                if (Settings.settings.has(Settings.PROXY_LOGGING) && Settings.settings.getBoolean(Settings.PROXY_LOGGING))
                    System.out.println("Request");
                requestHeaders = requestString.split("\r\n\r\n")[0];

                Settings.loadSettings();
                if (Settings.settings.has(Settings.PROXY_LOGGING) && Settings.settings.getBoolean(Settings.PROXY_LOGGING))
                    System.out.println(requestString);


                String urlString = "";
                try {
                    urlString = pullLinks(requestString).get(0);
                } catch (IndexOutOfBoundsException ex) {
                    return;
                }

                // Tell the game 1.6 hasn't been release yet. Anyone using MineOnline doesn't need to be told to update.
                if(urlString.contains("1_6_has_been_released.flag")) {
                    String responseHeaders =
                            "HTTP/1.0 404 Not FoundServer:Werkzeug/1.0.1 Python/3.7.0\r\n\r\n";

                    clientSocket.getOutputStream().write(responseHeaders.getBytes());
                    clientSocket.getOutputStream().flush();
                    clientSocket.getOutputStream().close();
                    continue;
                }

                // Don't bother redirecting snooping data yet.
                if(urlString.contains("snoop.minecraft.net")) {
                    String responseHeaders =
                            "HTTP/1.0 404 Not FoundServer:Werkzeug/1.0.1 Python/3.7.0\r\n\r\n";

                    clientSocket.getOutputStream().write(responseHeaders.getBytes());
                    clientSocket.getOutputStream().flush();
                    clientSocket.getOutputStream().close();
                    continue;
                }

                if ((urlString.contains("/resources/") && !urlString.endsWith("/resources/")) || (urlString.contains("/MinecraftResources/") && !urlString.endsWith("/MinecraftResources/"))) {
                    // There's probably a better way to do this, but to avoid downloading resources every play (which would be demanding on the API), 404 if resources are already downloaded.
                    String oggFilePath = urlString;

                    if (urlString.contains("/resources/")) {
                        oggFilePath = urlString.substring(oggFilePath.indexOf("/resources/")).replace("/resources/", "");
                    } else if (urlString.contains("/MinecraftResources")) {
                        oggFilePath = urlString.substring(oggFilePath.indexOf("/MinecraftResources/")).replace("/MinecraftResources/", "");
                    }

                    oggFilePath.replace("/", File.separator);

                    File oggFile = new File(LauncherFiles.MINECRAFT_RESOURCES_PATH + oggFilePath);

                    if (oggFile.exists()) {
                        Settings.loadSettings();
                        if (Settings.settings.has(Settings.PROXY_LOGGING) && Settings.settings.getBoolean(Settings.PROXY_LOGGING))
                            System.out.println("Responding already downloaded resource.");

                        String responseHeaders =
                                "HTTP/1.0 404 Not FoundServer:Werkzeug/1.0.1 Python/3.7.0\r\n\r\n";

                        clientSocket.getOutputStream().write(responseHeaders.getBytes());
                        clientSocket.getOutputStream().flush();
                        clientSocket.getOutputStream().close();
                        continue;
                    }
                }


                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                for (String header : requestHeaders.substring(requestHeaders.indexOf("\r\n") + 2).split("\r\n")) {
                    String headerName = header.substring(0, header.indexOf(":"));
                    String headerValue = header.substring(header.indexOf(":") + 2);
                    connection.setRequestProperty(headerName, headerValue);
                }

                String methodLine = requestHeaders.substring(0, requestHeaders.indexOf("\r\n"));
                connection.setRequestMethod(methodLine.substring(0, methodLine.indexOf(" ")));

                byte[] content = new byte[0];

                try {
                    if (headerSize < requestBytes.length) {
                        content = Arrays.copyOfRange(requestBytes, headerSize, requestBytes.length);

                        if (urlString.endsWith("/heartbeat.jsp")) {
                            // If there's a server running which has a connection hostname not equal to the bound IP address (like if you're using ngrok or something),
                            // Update the heartbeat to use that hostname.
                            if (MinecraftServerLauncher.serverlistAddress != null && !MinecraftServerLauncher.serverlistAddress.isEmpty()) {
                                String query = new String(content);
                                String ip = query.substring(query.indexOf("ip=") + 3);
                                ip = ip.substring(0, ip.indexOf("&"));
                                // Append "ip=" in case no IP was provided.
                                query = query.replace("ip=" + ip, "ip=" + MinecraftServerLauncher.serverlistAddress);
                                content = query.getBytes();
                            }
                            if (MinecraftServerLauncher.serverlistPort != null && !MinecraftServerLauncher.serverlistPort.isEmpty()) {
                                String query = new String(content);
                                String port = query.substring(query.indexOf("port=") + 5);
                                port = port.substring(0, port.indexOf("&"));
                                query = query.replace("port=" + port, "port=" + MinecraftServerLauncher.serverlistPort);
                                content = query.getBytes();
                            }

                            connection.setRequestProperty("Content-Length", content.length + "");
                        }

                        connection.setUseCaches(false);
                        connection.setDoInput(true);
                        connection.setDoOutput(true);

                        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());

                        wr.write(content);
                        wr.flush();
                        wr.close();
                    } else {
                        connection.setUseCaches(false);
                        connection.setDoInput(true);
                        connection.connect();
                    }


                    String responseHeader = "";
                    for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                        if (header.getKey() == null) {
                            Iterator<String> valueIterator = header.getValue().iterator();
                            responseHeader += valueIterator.next();
                            while (valueIterator.hasNext()) {
                                responseHeader += " " + valueIterator.next();
                            }
                            continue;
                        }
                        responseHeader += header.getKey() + ":";
                        Iterator<String> valueIterator = header.getValue().iterator();
                        responseHeader += valueIterator.next();
                        while (valueIterator.hasNext()) {
                            responseHeader += ", " + valueIterator.next();
                        }
                        responseHeader += "\r\n";
                    }
                    responseHeader += "\r\n";

                    String contentString = new String(content);

                    Settings.loadSettings();
                    if (Settings.settings.has(Settings.PROXY_LOGGING) && Settings.settings.getBoolean(Settings.PROXY_LOGGING)) {
                        System.out.println("Response");
                        System.out.print(responseHeader);
                        System.out.println(contentString);
                    }

                    InputStream is = connection.getInputStream();

                    clientSocket.getOutputStream().write(responseHeader.getBytes());

                    buffer = new byte[bufferSize];
                    while ((bytes_read = is.read(buffer, 0, bufferSize)) != -1) {
                        for (int i = 0; i < bytes_read; i++) {
                            clientSocket.getOutputStream().write(buffer[i]);
                        }
                    }

                    clientSocket.getOutputStream().flush();
                    clientSocket.getOutputStream().close();

                } catch (Exception ex) {
                    String responseHeaders =
                            "HTTP/1.0 404 Not FoundServer:Werkzeug/1.0.1 Python/3.7.0\r\n\r\n";
                    clientSocket.getOutputStream().write(responseHeaders.getBytes());
                    clientSocket.getOutputStream().flush();
                    clientSocket.getOutputStream().close();
                    ex.printStackTrace();
                }
            } catch (FileNotFoundException ex) {
                System.err.println("Got a 404 for: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("Something went very wrong.");
                ex.printStackTrace();
            } finally {
                outGoing.close();
                try {
                    clientSocket.close();
                } catch (Exception e) { }
            }
        }
        try {
            serverSocket.get().close();
        } catch (Exception e) { }
        running.set(false);
    }

    //Pull all links from the body for easy retrieval
    private static ArrayList<String> pullLinks(String text) {
        ArrayList<String> links = new ArrayList<String>();

        String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);
        }
        return links;
    }
}
