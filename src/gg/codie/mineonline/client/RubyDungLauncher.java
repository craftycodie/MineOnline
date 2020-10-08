package gg.codie.mineonline.client;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.utils.JREUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class RubyDungLauncher {
    public RubyDungLauncher(String jarPath) throws Exception {
        // Check if it's a Ruby Dung jar.
        URLClassLoader classLoader = new URLClassLoader(new URL[] {Paths.get(jarPath).toUri().toURL() });

        Class rubyDungClass;
        try {
            rubyDungClass = classLoader.loadClass("com.mojang.rubydung.RubyDung");
        } catch (ClassNotFoundException ex) {
            rubyDungClass = classLoader.loadClass("com.mojang.minecraft.RubyDung");
        }

        DisplayManager.closeDisplay();


        LinkedList<String> arguments = new LinkedList<>();
        arguments.add(JREUtils.getJavaExecutable());
        arguments.add("-Djava.library.path=" + LauncherFiles.MINEONLINE_NATIVES_FOLDER);

        if (Settings.settings.has(Settings.CLIENT_LAUNCH_ARGS) && !Settings.settings.getString(Settings.CLIENT_LAUNCH_ARGS).isEmpty())
            arguments.addAll(Arrays.asList(Settings.settings.getString(Settings.CLIENT_LAUNCH_ARGS).split(" ")));

        arguments.add("-cp");
        arguments.add(System.getProperty("java.class.path").replace("\"", "") + LibraryManager.getClasspathSeparator() + LauncherFiles.LWJGL_JAR + LibraryManager.getClasspathSeparator() + LauncherFiles.LWJGL_UTIL_JAR + LibraryManager.getClasspathSeparator() + jarPath);
        arguments.add(rubyDungClass.getCanonicalName());

        System.out.println("Launching RubyDung!  " + String.join(" ", arguments));

        java.util.Properties props = System.getProperties();
        ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        Map<String, String> env = processBuilder.environment();
        for(String prop : props.stringPropertyNames()) {
            env.put(prop, props.getProperty(prop));
        }
        processBuilder.directory(new File(System.getProperty("user.dir")));

        processBuilder.start();
        System.exit(0);
    }
}
