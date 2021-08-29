package gg.codie.mineonline;

import gg.codie.common.utils.OSUtils;
import gg.codie.mineonline.gui.ProgressDialog;
import gg.codie.mineonline.utils.MathUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class LibraryManager {

    public static void extractLibraries() throws IOException, URISyntaxException {
        if (!new File(LauncherFiles.MINEONLINE_LIBRARY_FOLDER).exists())
            ProgressDialog.showProgress("Installing MineOnline", new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    System.exit(0);
                }
            });

        ProgressDialog.setMessage("Extracting libraries...");

        File jarFile = new File(LibraryManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if(!jarFile.exists() || jarFile.isDirectory())
            return;

        File nativesFolder = new File(LauncherFiles.MINEONLINE_NATIVES_FOLDER);

        if (!nativesFolder.exists())
            nativesFolder.mkdirs();

        java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile.getPath());
        java.util.Enumeration enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
            if(!file.getName().startsWith("libraries") && !file.getName().startsWith("native"))
                continue;

            // Only extract libraries for the running OS.
            if (file.getName().contains("native") && !file.getName().contains(OSUtils.getPlatform().name()))
                continue;
            if (OSUtils.isMac() && (file.getName().endsWith("lwjgl.jar") || file.getName().endsWith("lwjgl_util.jar")))
                continue;
            else if (!OSUtils.isMac() && (file.getName().endsWith("lwjgl-mac.jar") || file.getName().endsWith("lwjgl_util-mac.jar")))
                continue;

            ProgressDialog.setSubMessage(file.getName());

            java.io.File f = new java.io.File(LauncherFiles.MINEONLINE_FOLDER + java.io.File.separator + file.getName());

            if (f.exists()){
                if (f.length() == file.getSize())
                    continue;
                else
                    f.delete();
            }

            if (file.isDirectory()) { // if its a directory, create it
                f.mkdir();
                continue;
            }
            java.io.InputStream is = jar.getInputStream(file); // get the input stream
            java.io.FileOutputStream fos = new java.io.FileOutputStream(f);


            ProgressDialog.setProgress(0);
            while (is.available() > 0) {  // write contents of 'is' to 'fos'
                ProgressDialog.setProgress((int)MathUtils.clamp((100 - ((float)is.available() / file.getSize()) * 100), 0, 99));
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
        jar.close();

        ProgressDialog.setProgress(100);
    }

    public static void extractRuntimeNatives(String[] nativeJars) throws IOException {
        File runtimeNativesFolder = new File(LauncherFiles.MINEONLINE_RUNTIME_NATIVES_FOLDER);
        if(runtimeNativesFolder.exists()) {
            runtimeNativesFolder.delete();
        }

        runtimeNativesFolder.mkdirs();

        for (String nativeJar : nativeJars) {
            File jarFile = new File(LauncherFiles.MINECRAFT_LIBRARIES_PATH + nativeJar);

            if(!jarFile.exists() || jarFile.isDirectory())
                return;

            java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile.getPath());
            java.util.Enumeration enumEntries = jar.entries();
            while (enumEntries.hasMoreElements()) {
                java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
                if(file.getName().startsWith("META-INF")) {
                    continue;
                }

                java.io.File f = new java.io.File(LauncherFiles.MINEONLINE_RUNTIME_NATIVES_FOLDER + java.io.File.separator + file.getName());

                if(f.exists()){
                    continue;
                }

                if (file.isDirectory()) { // if its a directory, create it
                    f.mkdir();
                    continue;
                }
                java.io.InputStream is = jar.getInputStream(file); // get the input stream
                java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
                while (is.available() > 0) {  // write contents of 'is' to 'fos'
                    fos.write(is.read());
                }
                fos.close();
                is.close();
            }
            jar.close();
        }
    }

    public static String getClasspath(boolean includeLWJGL2, boolean includeJFX, String[] includeJars) {
        StringBuilder classpath = new StringBuilder();
        //classpath.append(System.getProperty("java.class.path").replace("\"", ""));

        if (includeLWJGL2){
            classpath.append(getClasspathSeparator() + LauncherFiles.LWJGL_UTIL_JAR);
            classpath.append(getClasspathSeparator() + LauncherFiles.LWJGL_JAR);
            classpath.append(getClasspathSeparator() + LauncherFiles.SLICK_UTIL_JAR);
            classpath.append(getClasspathSeparator() + LauncherFiles.JINPUT_JAR);
        }

        if (includeJFX) {
            // Java 8
            classpath.append(getClasspathSeparator() + System.getProperty("java.home") + "\\lib\\ext\\jfxrt.jar");
            // Open JDK
            classpath.append(getClasspathSeparator() + System.getProperty("java.home") + "\\lib\\javafx.graphics.jar");
            classpath.append(getClasspathSeparator() + System.getProperty("java.home") + "\\lib\\javafx.web.jar");
            classpath.append(getClasspathSeparator() + System.getProperty("java.home") + "\\lib\\javafx.swing.jar");
            classpath.append(getClasspathSeparator() + System.getProperty("java.home") + "\\lib\\javafx.controls.jar");
            classpath.append(getClasspathSeparator() + System.getProperty("java.home") + "\\lib\\javafx.media.jar");
            classpath.append(getClasspathSeparator() + System.getProperty("java.home") + "\\lib\\javafx.base.jar");
        }


        classpath.append(getClasspathSeparator() + LauncherFiles.JSON_JAR);
        classpath.append(getClasspathSeparator() + LauncherFiles.BYTEBUDDY_JAR);
        classpath.append(getClasspathSeparator() + LauncherFiles.ASM_COMMONS_JAR);
        classpath.append(getClasspathSeparator() + LauncherFiles.ASM_JAR);

        for(String jar : includeJars) {
            classpath.append(getClasspathSeparator() + jar);
        }

        return classpath.toString();
    }

    public static char getClasspathSeparator() {
        if (OSUtils.isWindows()) {
            return ';';
        }

        return ':';
    }

    public static void updateNativesPath() throws PrivilegedActionException {
        AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
            public String run() throws Exception {
                return LauncherFiles.MINEONLINE_NATIVES_FOLDER;
            }
        });

        updateNativesPath(LauncherFiles.MINEONLINE_NATIVES_FOLDER);
    }

    public static void updateNativesPath(String path) {
        System.setProperty("java.library.path", path);
        System.setProperty("org.lwjgl.librarypath", path);
        System.setProperty("net.java.games.input.librarypath", path);
    }
}
