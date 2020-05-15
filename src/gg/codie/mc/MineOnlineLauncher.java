package gg.codie.mc;

import gg.codie.utils.ArrayUtils;

import java.io.*;
import java.net.*;

public class MineOnlineLauncher {
	static String CP = "-cp";
	static String proxySet = "-DproxySet=true";
	static String proxyHost = "-Dhttp.proxyHost=127.0.0.1";
	static String proxyPortArgument = "-Dhttp.proxyPort=";

	static Process gameProcess = null;

	public static void launch(String jarLocation, ELaunchType launchType, String mainClass, String[] args, int proxyPort) throws Exception{

		String[] CMD_ARRAY = new String[0];

		String binPath = jarLocation.substring(0, jarLocation.lastIndexOf(File.separator));
		String nativesPath = "-Djava.library.path=" + binPath + File.separator + "natives" + File.separator ;
		String classpath;

		switch (launchType) {
			case Launcher:
				CMD_ARRAY = new String[] { Properties.properties.getProperty("javaCommand"), proxySet, proxyHost, proxyPortArgument + proxyPort, CP, "\"" + jarLocation + "\"", mainClass };
				break;
			case Game:
				classpath = "\"" + jarLocation + "\";\"" + binPath + File.separator + "lwjgl.jar\";\"" + binPath + File.separator + "lwjgl_util.jar\"";
				CMD_ARRAY = new String[] { Properties.properties.getProperty("javaCommand"), proxySet, proxyHost, proxyPortArgument + proxyPort, nativesPath, CP, classpath, mainClass};
				CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, args);
				break;
			case Applet:
				String appletViewerLocation = MineOnlineLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();

				// Fix drive letters.
				char a_char = appletViewerLocation.charAt(2);
				if (a_char==':') appletViewerLocation = appletViewerLocation.substring(1);

				classpath = "\"" + jarLocation + "\";\"" + binPath + File.separator + "lwjgl.jar\";\"" + binPath + File.separator + "lwjgl_util.jar\";\"" + appletViewerLocation + "\"";
				CMD_ARRAY = new String[] { Properties.properties.getProperty("javaCommand"), proxySet, proxyHost, proxyPortArgument + proxyPort, nativesPath, CP, classpath, MinecraftAppletViewer.class.getCanonicalName(), mainClass};
				CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, args);
				break;
			case Server:
				classpath = "\"" + jarLocation + "\"";
				CMD_ARRAY = ArrayUtils.concatenate(new String[] { Properties.properties.getProperty("javaCommand") }, args);
				CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, new String[] { proxySet, proxyHost, proxyPortArgument + proxyPort, CP, classpath, mainClass});
				break;
		}

		System.out.println("Launching Game: " + String.join(" ", CMD_ARRAY));
		MineOnlineLauncher.gameProcess = new ProcessBuilder(CMD_ARRAY).start();

		Thread closeLauncher = new Thread() {
			public void run() {
				MineOnlineLauncher.gameProcess.destroy();
			}
		};

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

			StringBuffer response = new StringBuffer();
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
}
