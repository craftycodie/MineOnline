package gg.codie.mineonline;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class LibraryManager {

    public static void extractLibraries() throws IOException, URISyntaxException {
        File jarFile = new File(LibraryManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if(!jarFile.exists() || jarFile.isDirectory())
            return;

        java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile.getPath());
        java.util.Enumeration enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
            if(!file.getName().startsWith("lib")) {
                continue;
            }

            java.io.File f = new java.io.File(LauncherFiles.MINEONLINE_FOLDER + java.io.File.separator + file.getName());

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

    public static void updateClasspath() throws IOException {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);

            File libFolder = new File(LauncherFiles.MINEONLINE_LIBRARY_FOLDER);
            File[] libraries = libFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getPath().endsWith(".jar");
                }
            });

            for(File file : libraries) {
                method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
            }

        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }//end try catch
    }

    public static void addJarToClasspath(URL url) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{url});
        } catch (Exception e) {
            System.err.println("Java Error");
            e.printStackTrace();
            System.exit(1);
        }
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
