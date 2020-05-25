package gg.codie.mineonline;

import gg.codie.mineonline.gui.FormManager;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LibraryManager {

    public static void main(String[] args) throws Exception {
        extractLibraries();
        updateClasspath();
        updateNativesPath();

        FormManager.main(args);
    }

    public static void extractLibraries() throws IOException {
        String resourcePathString = LibraryManager.class.getResource("").getPath();
        if (resourcePathString.contains("jar!")) {
            int excl = resourcePathString.lastIndexOf("!");
            resourcePathString = resourcePathString.substring(0, excl);
            resourcePathString = resourcePathString.substring("file:/".length());
            new File(LauncherFiles.MINEONLINE_LIBRARY_FOLDER).mkdirs();
            Path extractPath = Paths.get(LauncherFiles.MINEONLINE_FOLDER);
            try (JarFile jarFile = new JarFile(resourcePathString);){
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String name = jarEntry.getName();

                    if(jarEntry.getName().startsWith("lib")) {
                        if (jarEntry.isDirectory()) {
                            Path dir = extractPath.resolve(name);
                            try {
                                Files.createDirectory(dir);
                            } catch (FileAlreadyExistsException fae) {

                            }
                        } else {
                            Path file = extractPath.resolve(name);
                            try (InputStream is = jarFile.getInputStream(jarEntry)) {
                                Files.copy(is, file, StandardCopyOption.REPLACE_EXISTING);
                            }
                        }
                    }
                }
            }
        } else {

        }
    }

    public static void updateClasspath() throws IOException {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);

            File libFolder = new File(LauncherFiles.MINEONLINE_LIBRARY_FOLDER);
            File[] libraries = libFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getPath().endsWith(".jar");
                }
            });

            for(File file : libraries) {
                method.invoke(sysloader, new Object[]{file.toURL()});
            }

        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }//end try catch
    }

    public static void updateNativesPath() throws PrivilegedActionException {
       AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
            public String run() throws Exception {
                return LauncherFiles.MINEONLNE_NATIVES_FOLDER;
            }
       });

        System.setProperty("java.library.path", LauncherFiles.MINEONLNE_NATIVES_FOLDER);
        System.setProperty("org.lwjgl.librarypath", LauncherFiles.MINEONLNE_NATIVES_FOLDER);
    }
}
