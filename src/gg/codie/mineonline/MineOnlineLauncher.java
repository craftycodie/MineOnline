package gg.codie.mineonline;

import gg.codie.utils.ArrayUtils;
import gg.codie.utils.OSUtils;

import java.io.*;
import java.util.Map;

public class MineOnlineLauncher {
	static String CP = "-cp";
	static String proxySet = "-DproxySet=true";
	static String proxyHost = "-Dhttp.proxyHost=127.0.0.1";
	static String proxyPortArgument = "-Dhttp.proxyPort=";

	public static Process gameProcess = null;

	public static void launch(String jarLocation, ELaunchType launchType, String mainClass, String[] args, int proxyPort) throws Exception{

		String[] CMD_ARRAY = new String[0];

		String classpath = System.getProperty("java.class.path").replace("\"", "");
		String natives = "-Djava.library.path=" + LauncherFiles.MINEONLNE_NATIVES_FOLDER;

		switch (launchType) {
			case Launcher:
				if (Properties.properties.getBoolean("useLocalProxy"))
					CMD_ARRAY = new String[] { Properties.properties.getString("javaCommand"), proxySet, proxyHost, proxyPortArgument + proxyPort, CP, "\"" + jarLocation + "\"", mainClass };
				else
					CMD_ARRAY = new String[] { Properties.properties.getString("javaCommand"), CP, "\"" + jarLocation + "\"", mainClass };
				break;
			case Game:
				classpath = classpath + getClasspathSeparator() + jarLocation + getClasspathSeparator() + LauncherFiles.LWJGL_JAR + getClasspathSeparator() + LauncherFiles.LWJGL_UTIL_JAR;
				if (Properties.properties.getBoolean("useLocalProxy"))
					CMD_ARRAY = new String[] { Properties.properties.getString("javaCommand"), proxySet, proxyHost, proxyPortArgument + proxyPort, natives, CP, classpath, mainClass};
				else
					CMD_ARRAY = new String[] { Properties.properties.getString("javaCommand"), natives, CP, classpath, mainClass};
				CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, args);
				break;
			case Applet:
				String appletViewerLocation = MineOnlineLauncher.class.getProtectionDomain().getCodeSource().getLocation().getPath();

				// Fix drive letters.
				char a_char = appletViewerLocation.charAt(2);
				if (a_char==':')
					appletViewerLocation = appletViewerLocation.substring(1);

				classpath = classpath + getClasspathSeparator() + jarLocation + getClasspathSeparator() + appletViewerLocation;
				if (Properties.properties.getBoolean("useLocalProxy"))
					CMD_ARRAY = new String[] { Properties.properties.getString("javaCommand"), proxySet, proxyHost, proxyPortArgument + proxyPort, natives, CP, classpath, MinecraftAppletViewer.class.getCanonicalName(), mainClass};
				else
					CMD_ARRAY = new String[] { Properties.properties.getString("javaCommand"), natives, CP, classpath, MinecraftAppletViewer.class.getCanonicalName(), mainClass};
				CMD_ARRAY = ArrayUtils.concatenate(CMD_ARRAY, args);
				break;
			case Server:
				classpath = classpath + getClasspathSeparator() + jarLocation;
				CMD_ARRAY = ArrayUtils.concatenate(new String[] { Properties.properties.getString("javaCommand") }, args);
				if (Properties.properties.getBoolean("useLocalProxy"))
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

		Thread closeLauncher = new Thread(() -> MineOnlineLauncher.gameProcess.destroyForcibly());

		Runtime.getRuntime().addShutdownHook(closeLauncher);
	}



	public static char getClasspathSeparator() {
		if (OSUtils.isWindows()) {
			return ';';
		}

		return ':';
	}
}
