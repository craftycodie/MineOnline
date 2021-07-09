package gg.codie.mineonline;

import gg.codie.common.utils.ArrayUtils;
import gg.codie.common.utils.JSONUtils;
import gg.codie.common.utils.MD5Checksum;
import gg.codie.mineonline.gui.ProgressDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MinecraftVersionRepository {

    private MinecraftVersion[] versions = new MinecraftVersion[0];
    private MinecraftVersion[] customVersions = new MinecraftVersion[0];
    private Map<String, MinecraftVersion> installedVersions = new HashMap<>();
    boolean loadingInstalledVersions = true;

    JSONObject installedVersionJSON = new JSONObject();

    private static final String MINEONLINE_JARS_JSON_FILE = LauncherFiles.MINEONLINE_FOLDER + "minecraft-jars.json";
    private static final String INSTALLED_VERSIONS = "installedJars";
    private static final String SELECTED_VERSION = "lastSelected";

    public MinecraftVersionRepository(String loadJar) {
        loadVersions(loadJar);
    }

    public boolean isLoadingInstalledVersions() {
        return loadingInstalledVersions;
    }

    private static MinecraftVersionRepository singleton;

    public static MinecraftVersionRepository getSingleton() {
        return getSingleton(null);
    }

    public static MinecraftVersionRepository getSingleton(String loadJar) {
        if(singleton == null) {
            singleton = new MinecraftVersionRepository(loadJar);
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

        boolean knownJar = installedVersions.containsKey(jarPath);

        installedVersions.put(jarPath, version);
        if(!knownJar) {
            String[] jarPaths = installedVersionJSON.has(INSTALLED_VERSIONS) ? JSONUtils.getStringArray(installedVersionJSON.getJSONArray(INSTALLED_VERSIONS)) : new String[0];
            installedVersionJSON.put(INSTALLED_VERSIONS, ArrayUtils.concatenate(jarPaths, new String[]{jarPath}));
        }
        saveInstalledVersions();
    }

    public String getLastSelectedJarPath() {
        String jarPath =  installedVersionJSON.has(SELECTED_VERSION) ? installedVersionJSON.getString(SELECTED_VERSION) : null;
        if (jarPath != null && !new File(jarPath).exists())
            jarPath = null;

        return jarPath;
    }

    public void selectJar(String jarPath) {
        installedVersionJSON.put(SELECTED_VERSION, jarPath);
        saveInstalledVersions();
    }

    public Map<String, MinecraftVersion> getInstalledJars() {
        return installedVersions;
    }

    public LinkedList<MinecraftVersion> getInstalledClients() {
        return installedVersions.values().stream().filter(version -> version != null).filter(version -> version.type.equals("client") || version.type.equals("launcher") || version.type.equals("rubydung")).distinct().collect(Collectors.toCollection(LinkedList::new));
    }

    public LinkedList<MinecraftVersion> getDownloadableClients() {
        LinkedList<MinecraftVersion> allVersions = new LinkedList<>(Arrays.asList(versions));
        allVersions.addAll(new LinkedList<>(Arrays.asList(customVersions)));
        return allVersions.stream().filter(version -> version != null).filter(version -> (version.type.equals("client") || version.type.equals("launcher") || version.type.equals("rubydung")) && version.downloadURL != null).distinct().collect(Collectors.toCollection(LinkedList::new));
    }

    private void loadJar(String path) {
        File jar = new File(path);

        if (!jar.exists())
            return;

        MinecraftVersion version = getVersion(path);

        if(version == null) {
            try {
                if (!MinecraftVersion.isPlayableJar(path) && !MinecraftVersion.isLegacyJar(path)) {
                    return;
                }
            } catch (Exception ex) {
                return;
            }
        }

        installedVersions.put(path, version);
    }

    // This is kinda heavy, that's why it's cached. So avoid it as much as possible.
    private void loadInstalledVersions() {
        String[] jarPaths = installedVersionJSON.has(INSTALLED_VERSIONS) ? JSONUtils.getStringArray(installedVersionJSON.getJSONArray(INSTALLED_VERSIONS)) : new String[0];

        for(String jarPath : jarPaths) {
            File jar = new File(jarPath);
            if (!jar.exists())
                continue;

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
    }

    private static LinkedList<String> getOfficialLauncherJars(LinkedList<String> fileNames, Path dir) {
        if(fileNames == null)
            fileNames = new LinkedList<>();

        if(dir == null)
            dir = Paths.get(LauncherFiles.MINECRAFT_VERSIONS_PATH);

        File versionsFolder = new File(dir.toString());
        if (!versionsFolder.exists() || !versionsFolder.isDirectory())
            return new LinkedList<>();

        try {

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path path : stream) {
                    if (path.toFile().isDirectory()) {
                        getOfficialLauncherJars(fileNames, path);
                    } else if (path.toAbsolutePath().toString().endsWith(".jar")) {
                        String fileName = path.toAbsolutePath().getFileName().toString();
                        if (fileName.equals("lwjgl.jar") || fileName.equals("lwjgl_util.jar") || fileName.equals("jinput.jar"))
                            continue;

                        fileNames.add(path.toAbsolutePath().toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return fileNames;
        } catch (Exception ex) {
            return new LinkedList<>();
        }
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

    private void loadVersions(String loadJarPath) {
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
        } catch (IOException ex) {
            saveInstalledVersions();
        }

        ProgressDialog.setSubMessage("Extracting version information...");
        ProgressDialog.setProgress(40);
        for (MinecraftVersion version : getResourceVersions()) {
            try {
                File target = new File(LauncherFiles.MINEONLINE_VERSION_INFO_FOLDER + version.type + File.separator + version.name + " " + version.md5 + ".json");
                if (!target.exists() || MinecraftVersionRepository.class.getResource("/version-info/" + version.type + "/" + version.name + " " + version.md5 + ".json").openConnection().getLastModified() > Files.getLastModifiedTime(Paths.get(LauncherFiles.MINEONLINE_VERSION_INFO_FOLDER + version.type + File.separator + version.name + " " + version.md5 + ".json")).toMillis()) {
                    System.out.println("Extracting version " + version.name + " " + version.md5);
                    target.getParentFile().mkdirs();
                    Files.copy(MinecraftVersionRepository.class.getResourceAsStream("/version-info/" + version.type + "/" + version.name + " " + version.md5 + ".json"), Paths.get(target.toURI()), StandardCopyOption.REPLACE_EXISTING);
                    target.setLastModified(MinecraftVersionRepository.class.getResource("/version-info/" + version.type + "/" + version.name + " " + version.md5 + ".json").openConnection().getLastModified());
                }
            } catch (Exception ex) {
                System.out.println("Failed to extract version " + version.md5);
                ex.printStackTrace();
            }
        }
        // Load cached versions
        versions = getVersions(LauncherFiles.MINEONLINE_VERSION_INFO_FOLDER);
        // Load custom versions
        customVersions = getVersions(LauncherFiles.MINEONLINE_CUSTOM_VERSION_INFO_FOLDER);
        //Load installed versions
        if (loadJarPath != null)
            loadJar(loadJarPath);
        if (getLastSelectedJarPath() != null)
            loadJar(getLastSelectedJarPath());
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Load installed versions
                loadInstalledVersions();
                // Load official launcher installed versions
//                loadOfficialLauncherVersions();
                ProgressDialog.setSubMessage(null);
                loadingInstalledVersions = false;
            }
        }).start();
    }

    public MinecraftVersion getVersionByMD5(String md5) {
        return getVersionByMD5(md5, ArrayUtils.concatenate(versions, customVersions));
    }

    public List<MinecraftVersion> getVersionsByBaseVersion(String baseVersion) {
        return getVersionsByBaseVersion(baseVersion, ArrayUtils.concatenate(versions, customVersions));
    }

    private static List<MinecraftVersion> getVersionsByBaseVersion(String baseVersion, MinecraftVersion[] versions) {
        List<MinecraftVersion> matches = Arrays.stream(versions).filter(version -> version.baseVersion.equals(baseVersion)).collect(Collectors.toList());
        return matches;
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
                if (!file.getName().startsWith("version-info")) {
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
                } catch (IOException | JSONException ex) {
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
