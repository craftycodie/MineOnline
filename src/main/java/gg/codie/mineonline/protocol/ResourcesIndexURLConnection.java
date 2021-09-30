package gg.codie.mineonline.protocol;

import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.client.LegacyGameManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ResourcesIndexURLConnection extends HttpURLConnection {
    public ResourcesIndexURLConnection(URL url) {
        super(url);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect() throws IOException {

    }

    @Override
    public InputStream getInputStream() {
        String resourcesVersion = LegacyGameManager.getVersion() != null ? LegacyGameManager.getVersion().resourcesVersion : "default";

        StringBuilder response = new StringBuilder();

        response.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                "<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">\n"+
                "<Name>MinecraftResources</Name>\n"+
                "<Prefix></Prefix>\n"+
                "<Marker></Marker>\n"+
                "<MaxKeys>1000</MaxKeys>\n"+
                "<IsTruncated>false</IsTruncated>\n");

        File resourcesFolder = new File(LauncherFiles.MINEONLINE_RESOURCES_PATH + resourcesVersion);
        try {
            Files.find(resourcesFolder.toPath(),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .forEach((path) -> {
                        try {
                            if (path.toString().endsWith(".ogg") || path.toString().endsWith(".mus")) {
                                long fileSize = Files.size(path);
                                FileTime modifiedTime = Files.getLastModifiedTime(path);
                                String filename = path.toString().replace(resourcesFolder.getPath(), "").substring(1).replace("\\", "/");
                                response.append("<Contents>\n"+
                                        "<Key>" + filename + "</Key>\n"+
                                "<LastModified>" + formatDateTime(modifiedTime) + "</LastModified>\n"+
                            "<Size>" + fileSize + "</Size>\n"+
                        "</Contents>\n");
                            }
                        } catch (Exception ex) {

                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.append("</ListBucketResult>");

        System.out.println(response);

        return new ByteArrayInputStream(response.toString().getBytes());
    }

    public static String formatDateTime(FileTime fileTime) {

        LocalDateTime localDateTime = fileTime
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }


}
