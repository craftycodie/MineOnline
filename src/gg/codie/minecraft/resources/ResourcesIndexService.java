package gg.codie.minecraft.resources;

import gg.codie.mineonline.LauncherFiles;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourcesIndexService {
    public URL getResourcesIndex(String version) throws Exception{
        File resourcesFolder = new File(LauncherFiles.MINEONLINE_RESOURCES_PATH + version);
        if (!resourcesFolder.exists()) {
            resourcesFolder = new File(LauncherFiles.MINEONLINE_RESOURCES_PATH + "default");
            if (!resourcesFolder.exists())
                throw new Exception("Sounds not available.");
        }

        File indexFile = new File(resourcesFolder.getPath() + File.separator + "index.txt");
        if (!indexFile.exists())
            createResourcesIndex(resourcesFolder);
        return indexFile.toURI().toURL();
    }

    private void createResourcesIndex(File resourcesFolder) throws IOException {
        File indexFile = new File(resourcesFolder.getPath() + File.separator + "index.txt");
        FileWriter fileWriter = new FileWriter(indexFile);
        Files.find(resourcesFolder.toPath(),
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile())
                .forEach((path) -> {
                    try {
                        if (path.toString().endsWith(".ogg") || path.toString().endsWith(".mus"))
                        fileWriter.write(path.toString().replace(resourcesFolder.getPath(), "").substring(1).replace("\\", "/") + "," + Files.size(path) + ",0\n");
                    } catch (Exception ex) {

                    }
                });
        fileWriter.flush();
        fileWriter.close();
    }
}
