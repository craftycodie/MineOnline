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
    static String JAVA_CMD = "java";
    static String CP = "-cp";

    static Process proxyProcess = null;

    public static int launchProxy() throws Exception {
        String CLASS_PATH = Proxy.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        // Fix drive letters.
        char a_char = CLASS_PATH.charAt(2);
        if (a_char==':') CLASS_PATH = CLASS_PATH.substring(1);

        CLASS_PATH = "\"" + CLASS_PATH + "\"";

        String PROG = Proxy.class.getCanonicalName();

        final String[] CMD_ARRAY = { JAVA_CMD, CP, CLASS_PATH, PROG };

        try {
            System.out.println("Launching Proxy: " + Arrays.toString(CMD_ARRAY));
            proxyProcess = new ProcessBuilder(CMD_ARRAY).start();

            Thread closeLauncher = new Thread() {
                public void run() {
                    proxyProcess.destroy();
                }
            };

            Runtime.getRuntime().addShutdownHook(closeLauncher);

            BufferedReader br = new BufferedReader(new InputStreamReader(proxyProcess.getInputStream()));
            String line = br.readLine();

            if(line.equals("Port Error")) {
                throw new Exception("Couldn't bind to port.");
            }

            int port = Integer.parseInt(line.split(":")[1]);

            System.out.println("Proxy launched. Port " + port);

            return port;

        } catch (Exception ex) {
            ex.printStackTrace();

            if(proxyProcess != null) {
                byte[] error = new byte[1000];
                proxyProcess.getErrorStream().read(error);
                System.out.write(error);

                System.exit(proxyProcess.exitValue());
            }

            System.exit(1);
        }

        return 0;
    }

    public static void main(String[] args) throws IOException {
        Properties.loadProperties();
        String[] redirectedDomains = ArrayUtils.fromString(Properties.properties.getProperty("redirectedDomains"));
        String destination = Properties.properties.getProperty("apiDomainName");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        boolean listening = true;
        try {
            serverSocket = new ServerSocket(0);
            System.out.println(serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
        } catch (IOException e) {
            System.out.println("Port Error");
            System.exit(-1);
        }
        while (listening) {
            //Stream to put data to the browser
            PrintWriter outGoing = null;
            try {
                clientSocket = serverSocket.accept();
                outGoing = new PrintWriter(clientSocket.getOutputStream(), true);

                final int bufferSize = 8096;

                byte[] buffer = new byte[bufferSize];
                int bytes_read;
                LinkedList<Byte> request = new LinkedList();

                bytes_read = clientSocket.getInputStream().read(buffer, 0, bufferSize);
                for(int i = 0; i < bytes_read; i++)
                    request.add(buffer[i]);

                String requestHeaders = new String(buffer).split("\r\n\r\n")[0];

//                // keep reading.
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

                System.out.println("Request");

                for (String redirectedDomain : redirectedDomains) {
                    requestString = requestString.replace(redirectedDomain, destination);
                }

                System.out.println(requestHeaders);



                String urlString = Proxy.pullLinks(requestString).get(0);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                for(String header : requestHeaders.substring(requestHeaders.indexOf("\r\n") + 2).split("\r\n")) {
                    String headerName = header.substring(0, header.indexOf(":"));
                    String headerValue = header.substring(header.indexOf(":"));
                    connection.setRequestProperty(headerName, headerValue);
                }

                String methodLine = requestHeaders.substring(0, requestHeaders.indexOf("\r\n"));
                connection.setRequestMethod(methodLine.substring(0, methodLine.indexOf(" ")));

                connection.setUseCaches(false);
                connection.setDoInput(true);

                if((requestHeaders.length() + 4) < requestBytes.length) {
                    byte[] content = Arrays.copyOfRange(requestBytes, requestHeaders.length() + 4, requestBytes.length);
                    connection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.write(content);
                    wr.flush();
                    wr.close();
                } else {
                    connection.connect();
                }

                String responseHeader = "";
                for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                    if(header.getKey() == null) {
                        Iterator<String> valueIterator = header.getValue().iterator();
                        responseHeader += valueIterator.next();
                        while (valueIterator.hasNext()) {
                            responseHeader += " " + valueIterator.next();
                        }
                        continue;
                    }
                    responseHeader += header.getKey() +  ":";
                    Iterator<String> valueIterator = header.getValue().iterator();
                    responseHeader += valueIterator.next();
                    while (valueIterator.hasNext()) {
                        responseHeader += ", " + valueIterator.next();
                    }
                    responseHeader += "\r\n";
                }
                responseHeader += "\r\n";

                System.out.println(responseHeader);
                clientSocket.getOutputStream().write(responseHeader.getBytes());

                InputStream is = connection.getInputStream();

                buffer = new byte[bufferSize];
                while ((bytes_read = is.read(buffer, 0, 4096)) != -1) {
                    for(int i = 0; i < bytes_read; i++)
                        clientSocket.getOutputStream().write(buffer[i]);
                }

                clientSocket.getOutputStream().flush();
                clientSocket.getOutputStream().close();

            } catch (IOException ex) {
                System.out.println("Something went very wrong.");
                ex.printStackTrace();
            } finally {
                outGoing.close();
                clientSocket.close();
            }
        }
        serverSocket.close();
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
