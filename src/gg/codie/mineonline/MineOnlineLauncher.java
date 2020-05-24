package gg.codie.mineonline;

import gg.codie.utils.ArrayUtils;
import gg.codie.utils.OSUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

public class MineOnlineLauncher {
	static String CP = "-cp";
	static String proxySet = "-DproxySet=true";
	static String proxyHost = "-Dhttp.proxyHost=127.0.0.1";
	static String proxyPortArgument = "-Dhttp.proxyPort=";

	static Process gameProcess = null;

	public static void launch(String jarLocation, ELaunchType launchType, String mainClass, String[] args, int proxyPort) throws Exception{

		String[] CMD_ARRAY = new String[0];

		String binPath = jarLocation.substring(0, jarLocation.lastIndexOf(File.separator));

		String nativesPath = binPath + File.separator + "natives" + File.separator ;

		nativesPath = "-Djava.library.path=" + nativesPath;

		String classpath = System.getProperty("java.class.path").replace("\"", "");

		switch (launchType) {
			case Launcher:
				if (Boolean.parseBoolean(Properties.properties.getProperty("useLocalProxy")))
					CMD_ARRAY = new String[] { Properties.properties.getProperty("javaCommand"), proxySet, proxyHost, proxyPortArgument + proxyPort, CP, "\"" + jarLocation + "\"", mainClass };
				else
					CMD_ARRAY = new String[] { Properties.properties.getProperty("javaCommand"), CP, "\"" + jarLocation + "\"", mainClass };
				break;
			case Game:
				classpath = classpath + getClasspathSeparator() + jarLocation + getClasspathSeparator() + binPath + File.separator + "lwjgl.jar" + getClasspathSeparator() + binPath + File.separator + "lwjgl_util.jar";
				if (Boolean.parseBoolean(Properties.properties.getProperty("useLocalProxy")))
					CMD_ARRAY = new String[] { Properties.properties.getProperty("javaCommand"), proxySet, proxyHost, proxyPortArgument + proxyPort, nativesPath, CP, classpath, mainClass};
				else
					CMD_ARRAY = new String[] { Properties.properties.getProperty("javaCommand"), nativesPath, CP, classpath, mainClass};
				CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, args);
				break;
			case Applet:
				String appletViewerLocation = MineOnlineLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();

				// Fix drive letters.
				char a_char = appletViewerLocation.charAt(2);
				if (a_char==':')
					appletViewerLocation = appletViewerLocation.substring(1);

				classpath = classpath + getClasspathSeparator() + jarLocation + getClasspathSeparator() + binPath + File.separator + "lwjgl.jar" + getClasspathSeparator() + binPath + File.separator + "lwjgl_util.jar" + getClasspathSeparator() + appletViewerLocation;
				if (Boolean.parseBoolean(Properties.properties.getProperty("useLocalProxy")))
					CMD_ARRAY = new String[] { Properties.properties.getProperty("javaCommand"), proxySet, proxyHost, proxyPortArgument + proxyPort, nativesPath, CP, classpath, MinecraftAppletViewer.class.getCanonicalName(), mainClass};
				else
					CMD_ARRAY = new String[] { Properties.properties.getProperty("javaCommand"), nativesPath, CP, classpath, MinecraftAppletViewer.class.getCanonicalName(), mainClass};
				CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, args);
				break;
			case Server:
				classpath = classpath + getClasspathSeparator() + jarLocation;
				CMD_ARRAY = ArrayUtils.concatenate(new String[] { Properties.properties.getProperty("javaCommand") }, args);
				if (Boolean.parseBoolean(Properties.properties.getProperty("useLocalProxy")))
					CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, new String[] { proxySet, proxyHost, proxyPortArgument + proxyPort, CP, classpath, mainClass});
				else
					CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, new String[] { CP, classpath, mainClass});
				break;
		}

		System.out.println("Launching Game: " + String.join(" ", CMD_ARRAY));

		java.util.Properties props = System.getProperties();
		ProcessBuilder processBuilder = new ProcessBuilder(CMD_ARRAY);
		Map<String, String> env = processBuilder.environment();
		for(String prop : props.stringPropertyNames()) {
			env.put(prop, props.getProperty(prop));
		}
		processBuilder.directory(new File(System.getProperty("user.dir")));
		processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		processBuilder.redirectErrorStream(true);
		processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

		MineOnlineLauncher.gameProcess = processBuilder.start();

		Thread closeLauncher = new Thread(() -> MineOnlineLauncher.gameProcess.destroy());

		Runtime.getRuntime().addShutdownHook(closeLauncher);
	}

	public static String getMpPass (String sessionId, String serverIP, String serverPort) throws IOException {
		HttpURLConnection connection = null;

		try {
			String parameters = "sessionId=" + URLEncoder.encode(sessionId, "UTF-8") + "&serverIP=" + URLEncoder.encode(serverIP, "UTF-8") + "&serverPort=" + URLEncoder.encode(serverPort, "UTF-8");
			URL url = new URL("http://" + Properties.properties.getProperty("apiDomainName") + "/mineonline/mppass.jsp?" + parameters);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.connect();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			String mpPass = rd.readLine();

			rd.close();

			return mpPass;
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return null;
		} finally {

			if (connection != null)
				connection.disconnect();
		}
	}

    public static boolean removecloak(String sessionId) {
        HttpURLConnection connection = null;

        try {
            String parameters = "sessionId=" + URLEncoder.encode(sessionId, "UTF-8");
            URL url = new URL("http://" + Properties.properties.getProperty("apiDomainName") + "/mineonline/removecloak.jsp?" + parameters);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String res = rd.readLine();

            rd.close();

            return res.equals("ok");
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        } finally {

            if (connection != null)
                connection.disconnect();
        }
    }

    public static boolean uploadSkin(String username, String sessionId, InputStream skinFile) {
		HttpURLConnection connection = null;

		try {
			URL url = new URL("http://" + Properties.properties.getProperty("apiDomainName") + "/mineonline/skin.jsp");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);

			connection.getOutputStream().write(ByteBuffer.allocate(2).putShort((short)username.length()).array());
			connection.getOutputStream().write(username.getBytes(Charset.forName("UTF-8")));
			connection.getOutputStream().write(ByteBuffer.allocate(2).putShort((short)sessionId.length()).array());
			connection.getOutputStream().write(sessionId.getBytes(Charset.forName("UTF-8")));

			int skinSize = skinFile.available();

			connection.getOutputStream().write(ByteBuffer.allocate(4).putInt(skinSize).array());

			byte[] buffer = new byte[8096];
			int bytes_read = 0;
			while ((bytes_read = skinFile.read(buffer, 0, 8096)) != -1) {
				for(int i = 0; i < bytes_read; i++) {
					System.out.print(buffer[i]);
					connection.getOutputStream().write(buffer[i]);
				}
			}

			connection.getOutputStream().flush();
			connection.getOutputStream().close();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			String res = rd.readLine();

			rd.close();

			return res.equals("ok");
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		} finally {

			if (connection != null)
				connection.disconnect();
		}

	}

	public static boolean uploadCloak(String username, String sessionId, InputStream cloakFile) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL("http://" + Properties.properties.getProperty("apiDomainName") + "/mineonline/cloak.jsp");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.getOutputStream().write(ByteBuffer.allocate(2).putShort((short)username.length()).array());
            connection.getOutputStream().write(username.getBytes(Charset.forName("UTF-8")));
            connection.getOutputStream().write(ByteBuffer.allocate(2).putShort((short)sessionId.length()).array());
            connection.getOutputStream().write(sessionId.getBytes(Charset.forName("UTF-8")));

            int cloakSize = cloakFile.available();

            connection.getOutputStream().write(ByteBuffer.allocate(4).putInt(cloakSize).array());

            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            while ((bytes_read = cloakFile.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    System.out.print(buffer[i]);
                    connection.getOutputStream().write(buffer[i]);
                }
            }

            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String res = rd.readLine();

            rd.close();

            return res.equals("ok");
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        } finally {

            if (connection != null)
                connection.disconnect();
        }

	}

	public static String login(String username, String password) throws IOException {
		HttpURLConnection connection = null;

        String parameters = "user=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&version=" + '\f';

        URL url = new URL("http://" + Properties.properties.getProperty("apiDomainName") + "/game/getversion.jsp");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        connection.setRequestProperty("Content-Length", Integer.toString((parameters.getBytes()).length));
        connection.setRequestProperty("Content-Language", "en-US");

        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);


        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();


        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();

        if(response.indexOf(":") < 0) {
            throw new IOException(response.toString());
        }

        String[] values = response.toString().split(":");

        if (connection != null)
            connection.disconnect();

        return values[3].trim();
	}

	public static boolean checkSession(String username, String sessionId) {
		HttpURLConnection connection = null;

		try {
			String parameters = "session=" + URLEncoder.encode(sessionId, "UTF-8") + "&name=" + URLEncoder.encode(username, "UTF-8");
			URL url = new URL("http://" + Properties.properties.getProperty("apiDomainName") + "/login/session.jsp?" + parameters);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.connect();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			String res = rd.readLine();

			rd.close();

			return res.equals("ok");
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		} finally {

			if (connection != null)
				connection.disconnect();
		}
	}

	public static char getClasspathSeparator() {
		if (OSUtils.isWindows()) {
			return ';';
		}

		return ':';
	}
}
