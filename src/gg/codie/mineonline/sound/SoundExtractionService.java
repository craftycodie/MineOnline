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
    // TODO: Remove or move.
    final String[] soundVersions = new String[]{
            "1.2.5",
            "1.5.2",
            "a1.0.1_01",
            "a1.0.15",
            "a1.0.17_04",
            "a1.1.0",
            "a1.1.1",
            "a1.1.2",
            "a1.1.2_01",
            "a1.2.0",
            "a1.2.1_01",
            "b1.3_01",
            "b1.4_01",
            "b1.5_01",
            "b1.7.3",
            "b1.9-pre4",
            "c0.0.22a_05",
            "remake_a1.0.8_01",
            "remake_c0.0.14a_08"
    };

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

        for (String soundVersion : soundVersions) {
            // If the sound version wasn't extracted...
            if (!new File(LauncherFiles.MINEONLINE_RESOURCES_PATH + soundVersion).exists()) {
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
                    System.out.println("Downloading sounds for " + soundVersion);
                    ProgressDialog.setProgress(0);

                    ProgressDialog.setSubMessage(soundVersion + ".zip");
                    /// Download it.
                    URL downloadURL = new URL("https://github.com/craftycodie/MineOnline/blob/main/resources/" + soundVersion + ".zip?raw=true");
                    HttpURLConnection httpConnection = (java.net.HttpURLConnection) (downloadURL.openConnection());
                    InputStream in = httpConnection.getInputStream();

                    String path = Paths.get(LauncherFiles.MINEONLINE_TEMP_FOLDER + soundVersion + ".zip").toString();

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

                    extractSoundsZip(new FileInputStream(soundsZip), soundVersion);

                    soundsZip.delete();
                } catch (FileNotFoundException ex) {
                    // Ignore missing zips for now.
                }
            }
        }


        ProgressDialog.setProgress(100);
    }
}
