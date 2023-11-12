package gg.codie.mineonline;

import gg.codie.common.utils.OSUtils;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.sound.SoundExtractionService;
import gg.codie.mineonline.utils.JREUtils;
import gg.codie.mineonline.utils.Logging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class Startup {
    public static void main(String[] args) throws IOException, URISyntaxException {
        System.setProperty("apple.awt.application.name", "MineOnline");

        Logging.deleteLog();
        Logging.enableLogging();

        if (Globals.DEV) {
            System.out.println("&&& MineOnline v " + Globals.LAUNCHER_VERSION + " b " + Globals.BRANCH + " &&&");
            System.out.println("Starting in Dev mode using Java: " + JREUtils.getRunningJavaExecutable());
        }

        try {
            ensureCorrectDPIScalingOnWindows();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        LibraryManager.extractLibraries();
        new SoundExtractionService().extractSoundFiles();

        LinkedList<String> launchArgs = new LinkedList();
        launchArgs.add(JREUtils.getRunningJavaExecutable());
        launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
        launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
        launchArgs.add("-cp");
        launchArgs.add(LibraryManager.getClasspath(true, new String[] { new File(MenuManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(), LauncherFiles.DISCORD_RPC_JAR }));
        launchArgs.add(MenuManager.class.getCanonicalName());
        launchArgs.addAll(Arrays.asList(args));

        java.util.Properties props = System.getProperties();
        ProcessBuilder processBuilder = new ProcessBuilder(launchArgs);

        Map<String, String> env = processBuilder.environment();
        for(String prop : props.stringPropertyNames()) {
            env.put(prop, props.getProperty(prop));
        }
        processBuilder.directory(new File(System.getProperty("user.dir")));

        Process launcherProcess = processBuilder.inheritIO().start();

        // for unix debugging, capture IO.
        if (Globals.DEV) {
            int exitCode = 1;
            try {
                exitCode = launcherProcess.waitFor();
                System.exit(exitCode);
            } catch (Exception ex) {
                // ignore.
            }
        }
        Runtime.getRuntime().halt(0);
    }

    // This is a hacky way to correct scaling on Windows by fiddling with the Registry and restarting the process.
    private static void ensureCorrectDPIScalingOnWindows() throws IOException, URISyntaxException {
        if (!OSUtils.isWindows())
            return;

        if (!Startup.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().endsWith(".exe"))
            return;

        final String DPI_AWARENESS_CMD = "reg query " +
                "\"HKCU\\Software\\Microsoft\\Windows NT\\CurrentVersion\\AppCompatFlags\\Layers\""
                + " /v \"" + Startup.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().substring(1).replace("/", File.separator) + "\"";

        Process process = Runtime.getRuntime().exec(DPI_AWARENESS_CMD);

        InputStream is = process.getInputStream();
        StringWriter sw = new StringWriter();

        int c;
        while ((c = is.read()) != -1)
            sw.write(c);


        boolean scalingFlagSet = sw.toString().contains("~ GDIDPISCALING DPIUNAWARE");

        if (!scalingFlagSet) {
            Runtime.getRuntime().exec("reg add \"HKCU\\Software\\Microsoft\\Windows NT\\CurrentVersion\\AppCompatFlags\\Layers\" /V \"" + Startup.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().substring(1).replace("/", File.separator) + "\" /T REG_SZ /D \"~ GDIDPISCALING DPIUNAWARE\" /F");
            Runtime.getRuntime().exec("cmd /c " + Startup.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().substring(1).replace("/", File.separator));
            System.exit(0);
        }
    }

}
