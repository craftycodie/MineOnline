package gg.codie.minecraft.launcher;

import java.util.Map;

public class VersionManifest {
    public Map<String, Object> arguments;
    public AssetIndex assetIndex;
    public String assets;
    public Map<String, Download> downloads;
    public String id;
    public String mainClass;
    public int minimumLauncherVersion;
    public String releaseTime;
    public String time;
    public String type;

    public static class AssetIndex {
        public String id;
        public String sha1;
        public int size;
        public int totalSize;
        public String url;
    }

    public static class Download {
        public String sha1;
        public int size;
        public String url;
    }

    public static class Library {
        public Map<String, Object> downloads;
        public String name;
        public Rule[] rules;
        public Extract extract;
        public Map<String, String> natives;

        public static class Download extends VersionManifest.Download {
            public String path;
        }

        public static class Extract {
            public String[] exclude;
        }
    }

    public static class Rule {
        public String action;
        public Map<String, Object> features;
        public OS os;

        public static class OS {
            public String name;
        }
    }
}
