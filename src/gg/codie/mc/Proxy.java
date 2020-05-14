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

                //Stream to get data from the browser
                BufferedReader inComing = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String incomingRequest;
                String url = "";
                String request = "";
                //Take the incoming request
                char[] buf = new char[8196];        //8196 is the default max size for GET requests in Apache
                int bytesRead = inComing.read(buf);   //BytesRead need to be calculated if the char buffer contains too many values
                request = new String(buf, 0, bytesRead);

                System.out.println("Request");
                System.out.println(request);

                for (String redirectedDomain : redirectedDomains) {
                    request = request.replace(redirectedDomain, destination);
                }

                System.out.println(request);



                url = Proxy.pullLinks(request).get(0);

                String host = url.replace("http://", "");
                if (host.contains(":"))
                    host = host.substring(0, host.indexOf('/'));
                else
                    host = host.substring(0, host.indexOf('/'));

                int websitePort = 80;
                String tempString = url.replace("http://", "").replace("https://", "");
                if(tempString.contains(":")) {
                    websitePort = Integer.parseInt(tempString.substring(tempString.indexOf(":") + 1, tempString.indexOf("/")));
                }

                System.out.println("Sending request");

                //Resolve the hostname to an IP address
                InetAddress ip = InetAddress.getByName(host);

                //Open socket to a specific host and port
                Socket socket = new Socket(host, websitePort);

                //Get input and output streams for the socket
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream();

                out.write(request.getBytes());
                out.flush();

                System.out.println("Sending Complete");


                // Reads the server's response
//                StringBuffer response = new StringBuffer();
                byte[] buffer = new byte[4096];
                int bytes_read;
                LinkedList<Byte> response = new LinkedList<Byte>();

                // Reads HTTP response
                while ((bytes_read = in.read(buffer, 0, 4096)) != -1) {
                    // Print server's response
                    for(int i = 0; i < bytes_read; i++)
                        response.add(buffer[i]);
                }

                socket.close();



                clientSocket.getOutputStream().write(toPrimitives(response.toArray(new Byte[0])));
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

    static byte[] toPrimitives(Byte[] oBytes)
    {

        byte[] bytes = new byte[oBytes.length];
        for(int i = 0; i < oBytes.length; i++){
            bytes[i] = oBytes[i];
        }
        return bytes;

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
