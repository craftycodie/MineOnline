package gg.codie.mineonline;

import com.sun.javaws.exceptions.BadJARFileException;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.utils.ArrayUtils;
import gg.codie.utils.JSONUtils;
import gg.codie.utils.MD5Checksum;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MinecraftVersionRepository {

    private MinecraftVersion[] versions = new MinecraftVersion[0];
    private MinecraftVersion[] customVersions = new MinecraftVersion[0];
    private Map<String, MinecraftVersion> installedVersions = new HashMap<>();

    JSONObject installedVersionJSON = new JSONObject();

    private static final String MINEONLINE_JARS_JSON_FILE = LauncherFiles.MINEONLINE_FOLDER + "minecraft-jars.json";
    private static final String INSTALLED_VERSIONS = "installedJars";
    private static final String SELECTED_VERSION = "lastSelected";

    public MinecraftVersionRepository() {
        loadVersions();
    }

    private static MinecraftVersionRepository singleton;

    public static MinecraftVersionRepository getSingleton() {
        if(singleton == null) {
            singleton = new MinecraftVersionRepository();
        }
        return singleton;
    }

    public void addInstalledVersion(String jarPath) {
        MinecraftVersion version = getVersion(jarPath);

        if(version == null) {
            try {
                if (!MinecraftVersion.isPlayableJar(jarPath)) {
                    return;
                }
            } catch (Exception ex) {
                return;
            }
        }

        installedVersions.put(jarPath, version);
        String[] jarPaths = installedVersionJSON.has(INSTALLED_VERSIONS) ? JSONUtils.getStringArray(installedVersionJSON.getJSONArray(INSTALLED_VERSIONS)) : new String[0];
        installedVersionJSON.put(INSTALLED_VERSIONS, ArrayUtils.concatenate(jarPaths, new String[] { jarPath }));
        saveInstalledVersions();
    }

    public String getLastSelectedJarPath() {
        return installedVersionJSON.has(SELECTED_VERSION) ? installedVersionJSON.getString(SELECTED_VERSION) : null;
    }

    public void selectJar(String jarPath) {
        installedVersionJSON.put(SELECTED_VERSION, jarPath);
        saveInstalledVersions();
    }

    public Map<String, MinecraftVersion> getInstalledJars() {
        return installedVersions;
    }

    public LinkedList<MinecraftVersion> getInstalledClients() {
        return installedVersions.values().stream().filter(version -> version != null).filter(version -> version.type.equals("client")).distinct().collect(Collectors.toCollection(LinkedList::new));
    }

    // This is kinda heavy, that's why it's cached. So avoid it as much as possible.
    private void loadInstalledVersions() {
        try (FileInputStream input = new FileInputStream(MINEONLINE_JARS_JSON_FILE)) {
            // load a settings file
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for (int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char) buffer[i]);
                }
            }

            installedVersionJSON = new JSONObject(stringBuffer.toString());

            String[] jarPaths = installedVersionJSON.has(INSTALLED_VERSIONS) ? JSONUtils.getStringArray(installedVersionJSON.getJSONArray(INSTALLED_VERSIONS)) : new String[0];

            for(String jarPath : jarPaths) {
                MinecraftVersion version = getVersion(jarPath);

                if(version == null) {
                    try {
                        if (!MinecraftVersion.isPlayableJar(jarPath)) {
                            continue;
                        }
                    } catch (Exception ex) {
                        continue;
                    }
                }

                installedVersions.put(jarPath, version);
            }
        } catch (IOException ex) {
            saveInstalledVersions();
        }
    }

    private static LinkedList<String> getOfficialLauncherJars(LinkedList<String> fileNames, Path dir) {
        if(fileNames == null)
            fileNames = new LinkedList<>();

        if(dir == null)
            dir = Paths.get(LauncherFiles.MINECRAFT_VERSIONS_PATH);

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    getOfficialLauncherJars(fileNames, path);
                } else if(path.toAbsolutePath().toString().endsWith(".jar")) {
                    fileNames.add(path.toAbsolutePath().toString());
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return fileNames;
    }

    private void loadOfficialLauncherVersions() {
        // Check .minecraft\versions for new jars.
        Set<String> knownJars = installedVersions.keySet();

        LinkedList<String> officialLauncherVersions = getOfficialLauncherJars(null, null);

        officialJarsLoop:
        for (String path : officialLauncherVersions) {
            for (String jar : knownJars) {
                if(jar.equals(path)) {
                    continue officialJarsLoop;
                }
            }

            addInstalledVersion(path);
        }
    }

    private void saveInstalledVersions() {
        try {
            FileWriter fileWriter = new FileWriter(MINEONLINE_JARS_JSON_FILE, false);
            fileWriter.write(installedVersionJSON.toString());
            fileWriter.close();

            FileInputStream input = new FileInputStream(MINEONLINE_JARS_JSON_FILE);
            byte[] buffer = new byte[8096];
            int bytes_read = 0;
            StringBuffer stringBuffer = new StringBuffer();
            while ((bytes_read = input.read(buffer, 0, 8096)) != -1) {
                for(int i = 0; i < bytes_read; i++) {
                    stringBuffer.append((char)buffer[i]);
                }
            }

            input.close();

            installedVersionJSON = new JSONObject(stringBuffer.toString());
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void fetchVersions() {
        MinecraftVersion[] cachedVersions = getVersions(LauncherFiles.MINEONLINE_VERSIONS_FOLDER);
        try {
            JSONObject index = MineOnlineAPI.getVersionIndex();
            JSONArray versionsPaths = index.getJSONArray("versions");
            for(Object versionPathObject : versionsPaths) {
                try {
                    String filename = ((JSONObject) versionPathObject).getString("name");
                    String jarMd5 = filename.substring(filename.length() - 37, filename.length() - 5);

                    boolean alreadyDownloaded = false;

                    MinecraftVersion existingVersion = getVersionByMD5(jarMd5, cachedVersions);
                    for (MinecraftVersion cachedVersion : cachedVersions) {
                        if (cachedVersion != null && cachedVersion.md5.equals(jarMd5)) {
                            long infoModified = ((JSONObject) versionPathObject).getLong("modified");
                            File cachedInfo = new File(LauncherFiles.MINEONLINE_VERSIONS_FOLDER + existingVersion.type + File.separator + existingVersion.name + " " + existingVersion.md5 + ".json");

                            if (cachedInfo.exists()) {
                                if (infoModified > cachedInfo.lastModified() / 1000) {
                                    cachedInfo.delete();
                                } else {
                                    alreadyDownloaded = true;
                                }
                            }
                        }

                    }

                    if (alreadyDownloaded)
                        continue;


                    System.out.println("Downloaded new version info " + ((JSONObject) versionPathObject).getString("name"));

                    String downloadVersionText = MineOnlineAPI.getVersionInfo(((JSONObject) versionPathObject).getString("url"));
                    MinecraftVersion downloadVersion = new MinecraftVersion(new JSONObject(downloadVersionText));

                    Path target = Paths.get(LauncherFiles.MINEONLINE_VERSIONS_FOLDER + downloadVersion.type + File.separator + downloadVersion.name + " " + downloadVersion.md5 + ".json");
                    File targetFile = new File(target.toUri());
                    targetFile.getParentFile().mkdirs();
                    if(!targetFile.exists())
                        targetFile.createNewFile();

                    Files.write(target, downloadVersionText.getBytes(), StandardOpenOption.WRITE);

                } catch (Exception ex) {
                    System.out.println("Bad version " + versionPathObject);
                    ex.printStackTrace();
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadVersions() {
        // If there's a resource version that's not in the cache, extract it.
        MinecraftVersion[] cachedVersions = getVersions(LauncherFiles.MINEONLINE_VERSIONS_FOLDER);
        for (MinecraftVersion version : getResourceVersions()) {
            if(getVersionByMD5(version.md5, cachedVersions) == null) {
                try {
                    System.out.println("Extracting version " + version.name + " " + version.md5);
                    File target = new File(LauncherFiles.MINEONLINE_VERSIONS_FOLDER + version.type + File.separator + version.name + " " + version.md5 + ".json");
                    target.getParentFile().mkdirs();
                    Files.copy(MinecraftVersionRepository.class.getResourceAsStream("/versions/" + version.type + "/" + version.name + " " + version.md5 + ".json"), Paths.get(target.toURI()), StandardCopyOption.REPLACE_EXISTING);
                    target.setLastModified(MinecraftVersionRepository.class.getResource("/versions/" + version.type + "/" + version.name + " " + version.md5 + ".json").openConnection().getLastModified());
                } catch (Exception ex) {
                    System.out.println("Failed to extract version " + version.md5);
                    ex.printStackTrace();
                }
            }
        }
        // Fetch latest versions from the API
        fetchVersions();
        // Load cached versions
        versions = getVersions(LauncherFiles.MINEONLINE_VERSIONS_FOLDER);
        // Load custom versions
        customVersions = getVersions(LauncherFiles.MINEONLINE_CUSTOM_VERSIONS_FOLDER);
        // Load installed versions
        loadInstalledVersions();
        // Load official launcher installed versions
        loadOfficialLauncherVersions();
    }

    public MinecraftVersion getVersionByMD5(String md5) {
        return getVersionByMD5(md5, ArrayUtils.concatenate(versions, customVersions));
    }

    private static MinecraftVersion getVersionByMD5(String md5, MinecraftVersion[] versions) {
        List<MinecraftVersion> matches = Arrays.stream(versions).filter(version -> version.md5.equals(md5)).collect(Collectors.toList());
        if(matches.size() < 1) {
            return null;
        }
        return matches.get(0);
    }

    private MinecraftVersion readVersionFile(File versionFile) throws IOException {
        return new MinecraftVersion(new JSONObject(String.join("\n", Files.readAllLines(Paths.get(versionFile.getPath())))));
    }

    private MinecraftVersion[] getVersions(String versionsFolder) {
        LinkedList versions = new LinkedList();

        File[] directories = new File(versionsFolder).listFiles(file -> file.isDirectory());

        if (directories == null)
            return new MinecraftVersion[0];

        for (File directory : directories) {
            File[] versionFiles = directory.listFiles();
            if(versions == null)
                continue;

            for (File versionFile : versionFiles) {
                if(versionFile.getName().length() < 37 || versionFile.isDirectory())
                    continue;

                try {
                    MinecraftVersion version = readVersionFile(versionFile);
                    versions.add(version);
                } catch (IOException ex) {
                    System.out.println("Bad version file " + versionFile.getPath());
                }
            }
        }

        return (MinecraftVersion[])versions.toArray(new MinecraftVersion[0]);
    }

    private MinecraftVersion[] getResourceVersions() {
        LinkedList versions = new LinkedList();

        try {
            File jarFile = new File(LibraryManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            if(!jarFile.exists() || jarFile.isDirectory())
                return new MinecraftVersion[0];

            java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile.getPath());
            java.util.Enumeration enumEntries = jar.entries();

            while (enumEntries.hasMoreElements()) {
                java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
                if (!file.getName().startsWith("versions")) {
                    continue;
                }

                if (versions == null)
                    continue;

                if (file.getName().length() < 37 || file.isDirectory())
                    continue;

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(jar.getInputStream(file)));
                    StringBuffer sb = new StringBuffer();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        sb.append(str);
                    }

                    MinecraftVersion version = new MinecraftVersion(new JSONObject(sb.toString()));
                    versions.add(version);
                } catch (IOException ex) {
                    System.out.println("Bad version file " + file.getName());
                }

            }
        } catch (Exception ioe) {
            System.out.println("Failed to load resource versions.");
            ioe.printStackTrace();
            return new MinecraftVersion[0];
        }

        return (MinecraftVersion[])versions.toArray(new MinecraftVersion[0]);
    }

    public MinecraftVersion getVersion(String path) {
        try {
            String md5 = MD5Checksum.getMD5ChecksumForFile(path);
            MinecraftVersion version = getVersionByMD5(md5);
            if(version == null) {
                version = MinecraftVersion.fromLauncherVersion(new File(path));
            }
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
