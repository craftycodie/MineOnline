package gg.codie.minecraft.skins;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.utils.JREUtils;
import gg.codie.utils.OSUtils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class SkinRendererLauncher {

    public static void main(String[] args) throws Exception {
        LinkedList<String> launchArgs = new LinkedList();
        launchArgs.add(JREUtils.getJavaExecutable());
        launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
        launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
        launchArgs.add("-cp");
        launchArgs.add(LibraryManager.getClasspath(true, new String[] { new File(SkinRenderer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath() }));
        launchArgs.add(SkinRenderer.class.getCanonicalName());
        launchArgs.addAll(Arrays.asList(args));

        java.util.Properties props = System.getProperties();
        ProcessBuilder processBuilder = new ProcessBuilder(launchArgs);

        Map<String, String> env = processBuilder.environment();
        for(String prop : props.stringPropertyNames()) {
            env.put(prop, props.getProperty(prop));
        }
        processBuilder.directory(new File(System.getProperty("user.dir")));
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);

        Process skinRenderer = processBuilder.start();

        Thread closeLauncher = new Thread(() -> skinRenderer.destroyForcibly());
        Runtime.getRuntime().addShutdownHook(closeLauncher);

        while(skinRenderer.isAlive()) {

        }

        System.exit(skinRenderer.exitValue());
    }

}
