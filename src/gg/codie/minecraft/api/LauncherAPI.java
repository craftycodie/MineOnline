package gg.codie.minecraft.api;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.gui.ProgressDialog;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class LauncherAPI {
    private static final String BASE_URL = "https://api.mojang.com";

    public static JSONObject getVersionManifests() throws IOException {
        HttpURLConnection connection;

        URL url = new URL( "https://launchermeta.mojang.com/mc/game/version_manifest.json");
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(false);

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();

        try {
            return new JSONObject(response.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            JSONObject errorObject = new JSONObject();
            errorObject.put("error", response.toString());
            return errorObject;
        }
    }

    public static JSONObject getVersionManifest(String manifestURL) throws IOException {
        HttpURLConnection connection;

        URL url = new URL( manifestURL);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(false);

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();

        try {
            return new JSONObject(response.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            JSONObject errorObject = new JSONObject();
            errorObject.put("error", response.toString());
            return errorObject;
        }
    }

    public static void downloadVersion(String baseVersion) throws IOException {
        try {
            JSONObject versionManifests = getVersionManifests();

            for (Object version : versionManifests.getJSONArray("versions")) {
                if (!(version instanceof JSONObject))
                    continue;

                if (((JSONObject) version).getString("id").equals(baseVersion)) {
//                    ProgressDialog.showProgress("Downloading", null);
//                    ProgressDialog.setMessage("Minecraft " + baseVersion);
                    JSONObject versionManifest = getVersionManifest(((JSONObject) version).getString("url"));
                    URL jarUrl = new URL(versionManifest.getJSONObject("downloads").getJSONObject("client").getString("url"));

                    HttpURLConnection httpConnection = (java.net.HttpURLConnection) (jarUrl.openConnection());

                    long completeFileSize = httpConnection.getContentLength();
                    InputStream in = httpConnection.getInputStream();

                    File clientJar = new File(LauncherFiles.MINECRAFT_VERSIONS_PATH + baseVersion + File.separator + "client.jar");
                    clientJar.getParentFile().mkdirs();
                    OutputStream out = new java.io.FileOutputStream(LauncherFiles.MINECRAFT_VERSIONS_PATH + baseVersion + File.separator + "client.jar", false);

                    final byte[] data = new byte[1024];
                    long downloadedFileSize = 0;
                    int count;
                    while ((count = in.read(data, 0, 1024)) != -1) {
                        downloadedFileSize += count;

                        final int currentProgress = (int) (((double) downloadedFileSize) / ((double) completeFileSize) * 100d);

                        ProgressDialog.setProgress(currentProgress);

                        out.write(data, 0, count);
                    }
                }
            }
            ProgressDialog.setProgress(100);
        } catch (Exception ex) {
            ProgressDialog.setProgress(100);
            throw ex;
        }
    }
}
