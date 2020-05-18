package gg.codie.mc;

import gg.codie.utils.ArrayUtils;
import gg.codie.utils.OSUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

	public static String getServer (String serverId) throws IOException {
		HttpURLConnection connection = null;

		try {
			String parameters = "server=" + URLEncoder.encode(serverId, "UTF-8");
			URL url = new URL("http://" + Properties.properties.getProperty("apiDomainName") + "/mineonline/getserver.jsp?" + parameters);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(false);
			connection.connect();

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			String server = rd.readLine();

			rd.close();

			return server;
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return null;
		} finally {

			if (connection != null)
				connection.disconnect();
		}
	}

	public static String login(String password) {
		HttpURLConnection connection = null;

		try {
			String parameters = "user=" + URLEncoder.encode(Properties.properties.getProperty("username"), "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8") + "&version=" + '\f';

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

			String[] values = response.toString().split(":");

			return values[3].trim();
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		} finally {

			if (connection != null)
				connection.disconnect();
		}
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
