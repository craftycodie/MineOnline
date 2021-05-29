package gg.codie.mineonline.sound;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.gui.ProgressDialog;
import gg.codie.mineonline.utils.MathUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SoundExtractionService {
    private void extractSoundsZip(InputStream soundsZipStream, String soundsVersion) throws IOException {
        File soundsFolder = new File(LauncherFiles.MINEONLINE_RESOURCES_PATH + soundsVersion + File.separator);
        if (!soundsFolder.exists())
            soundsFolder.mkdir();

        try {
            while (soundsZipStream.available() > 0) {
                ZipInputStream zipIn = new ZipInputStream(soundsZipStream);
                ZipEntry entry = zipIn.getNextEntry();
                // iterates over entries in the zip file
                while (entry != null) {
                    String filePath = LauncherFiles.MINEONLINE_RESOURCES_PATH + soundsVersion + File.separator + entry.getName();
                    if (!entry.isDirectory()) {
                        // if the entry is a file, extracts it
                        if (new File(filePath).exists())
                            continue;
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
                        byte[] bytesIn = new byte[4096];
                        int read = 0;
                        while ((read = zipIn.read(bytesIn)) != -1) {
                            bos.write(bytesIn, 0, read);
                        }
                        bos.close();
                    } else {
                        // if the entry is a directory, make the directory
                        File dir = new File(filePath);
                        dir.mkdirs();
                    }
                    zipIn.closeEntry();
                    entry = zipIn.getNextEntry();
                }
                zipIn.close();
            }
        } catch (IOException ex) {
            // For some reason the stream is closing at the end automatically.
            // TODO: Figure out why and remove this catch.
        }
        soundsZipStream.close();
    }

    public void downloadSoundpack(String version) throws URISyntaxException, IOException {
        if (!new File(LauncherFiles.MINEONLINE_RESOURCES_PATH + version).exists()) {
            try {
                if (!ProgressDialog.isOpen()) {
                    ProgressDialog.showProgress("Installing MineOnline", new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            System.exit(0);
                        }
                    });
                    ProgressDialog.setMessage("Downloading sounds...");
                }
                System.out.println("Downloading sounds for " + version);
                ProgressDialog.setProgress(0);

                ProgressDialog.setSubMessage(version + ".zip");
                /// Download it.
                URL downloadURL = new URL("https://github.com/craftycodie/MineOnline/blob/main/resources/" + version + ".zip?raw=true");
                HttpURLConnection httpConnection = (java.net.HttpURLConnection) (downloadURL.openConnection());
                InputStream in = httpConnection.getInputStream();

                String path = Paths.get(LauncherFiles.MINEONLINE_TEMP_FOLDER + version + ".zip").toString();

                File soundsZip = new File(path);
                soundsZip.getParentFile().mkdirs();
                OutputStream out = new java.io.FileOutputStream(path, false);
                final byte[] data = new byte[1024];
                int count;
                int written = 0;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    written += count;
                    ProgressDialog.setProgress((int) MathUtils.clamp((((float)written / httpConnection.getContentLength()) * 100), 0, 99));
                    out.write(data, 0, count);
                }
                out.flush();
                out.close();

                extractSoundsZip(new FileInputStream(soundsZip), version);

                soundsZip.delete();

                ProgressDialog.setProgress(100);
            } catch (FileNotFoundException ex) {
                // Ignore missing zips for now.
            }
        }
    }

    public void extractSoundFiles() throws IOException, URISyntaxException {
        if (!new File(LauncherFiles.MINEONLINE_RESOURCES_PATH).exists())
            ProgressDialog.showProgress("Installing MineOnline", new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    System.exit(0);
                }
            });

        ProgressDialog.setMessage("Extracting sounds...");

        File jarFile = new File(LibraryManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        if(jarFile.exists() && !jarFile.isDirectory()) {
            java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile.getPath());
            java.util.Enumeration enumEntries = jar.entries();
            while (enumEntries.hasMoreElements()) {
                java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
                if (!file.getName().startsWith("resources"))
                    continue;

                String soundsVersion = file.getName().replace("resources/", "").replace(".zip", "");

                ProgressDialog.setSubMessage(soundsVersion);
                java.io.InputStream is = jar.getInputStream(file);

                ProgressDialog.setProgress(0);

                try {
                    extractSoundsZip(is, soundsVersion);
                } catch (IOException ex) {
                    System.out.println("WARNING: Failed to extract sound zip " + soundsVersion);
                    ex.printStackTrace();
                }

            }
            jar.close();
        }

        ProgressDialog.setMessage("Downloading sounds...");


        try {
            downloadSoundpack("default");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
