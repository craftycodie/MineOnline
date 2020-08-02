package gg.codie.mineonline;

import gg.codie.utils.OSUtils;

import java.io.File;
import java.net.URL;

public class LauncherFiles {

    public static final String MINEONLINE_FOLDER = getMinecraftDirectory() + File.separator + "mineonline" + File.separator;


    public static final String MINEONLINE_PROPS_FILE = MINEONLINE_FOLDER + "settings.json";

    public static final String MINEONLINE_LIBRARY_FOLDER = MINEONLINE_FOLDER + "lib" + File.separator;
  
    public static final String LWJGL_JAR = MINEONLINE_LIBRARY_FOLDER + "lwjgl-modded.jar";
    public static final String LWJGL_UTIL_JAR = MINEONLINE_LIBRARY_FOLDER + "lwjgl_util.jar";
    public static final String PATCH_AGENT_JAR = MINEONLINE_LIBRARY_FOLDER + "byte-buddy-agent.jar";
    public static final String JSON_JAR = MINEONLINE_LIBRARY_FOLDER + "json.jar";
    public static final String BYTEBUDDY_JAR = MINEONLINE_LIBRARY_FOLDER + "byte-buddy-1.10.14.jar";
    public static final String BYTEBUDDY_DEP_JAR = MINEONLINE_LIBRARY_FOLDER + "byte-buddy-dep-1.10.14.jar";


    public static final String MINEONLNE_NATIVES_FOLDER = MINEONLINE_LIBRARY_FOLDER + "native" + File.separator + OSUtils.getPlatform().toString();

    public static final String MINEONLINE_CACHE_FOLDER = MINEONLINE_FOLDER + "cache" + File.separator;

    public static final String CACHED_SKIN_PATH = MINEONLINE_CACHE_FOLDER + "skin.png";
    public static final String CACHED_CLOAK_PATH = MINEONLINE_CACHE_FOLDER + "cloak.png";
    public static final String CACHED_VERSION_INFO_PATH = MINEONLINE_CACHE_FOLDER + "version-info.json";

    public static final String LAST_LOGIN_PATH = MINEONLINE_CACHE_FOLDER + "lastlogin";
    public static final String CUSTOM_VERSION_INFO_PATH = MINEONLINE_FOLDER + "custom-version-info.json";

    public static final String MINECRAFT_RESOURCES_PATH = getMinecraftDirectory() + File.separator + "resources" + File.separator;
    public static final String MINECRAFT_ASSETS_PATH = getMinecraftDirectory() + File.separator + "assets" + File.separator;
    public static final String MINECRAFT_SCREENSHOTS_PATH = getMinecraftDirectory() + File.separator + "screenshots" + File.separator;
    public static final String MINECRAFT_OPTIONS_PATH = getMinecraftDirectory() + File.separator + "options.txt";
    public static final String MINECRAFT_VERSIONS_PATH = getMinecraftDirectory() + File.separator + "versions" + File.separator;
    public static final String MINECRAFT_LIBRARIES_PATH = getMinecraftDirectory() + File.separator + "libraries" + File.separator;

    public static String MINECRAFT_LIBRARIES_VECMATH_PATH = MINECRAFT_LIBRARIES_PATH + "java3d" + File.separator + "vecmath" + File.separator + "1.3.1" + File.separator + "vecmath-1.3.1.jar";
    public static String MINECRAFT_LIBRARIES_TROVE4J_PATH = MINECRAFT_LIBRARIES_PATH + "net" + File.separator + "sf" + File.separator + "trove4j" + File.separator + "trove4j" + File.separator + "3.0.3" + File.separator + "trove4j-3.0.3.jar";
    public static String MINECRAFT_LIBRARIES_ICU4J_CORE_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "ibm" + File.separator + "icu" + File.separator + "icu4j-core-mojang" + File.separator + "51.2" + File.separator + "icu4j-core-mojang-51.2.jar";
    public static String MINECRAFT_LIBRARIES_JOPT_SIMPLE_PATH = MINECRAFT_LIBRARIES_PATH + "net" + File.separator + "sf" + File.separator + "jopt-simple" + File.separator + "jopt-simple" + File.separator + "4.5" + File.separator + "jopt-simple-4.5.jar";
    public static String MINECRAFT_LIBRARIES_CODEC_JORBIS_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "paulscode" + File.separator + "codecjorbis" + File.separator + "20101023" + File.separator + "codecjorbis-20101023.jar";
    public static String MINECRAFT_LIBRARIES_CODEC_WAV_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "paulscode" + File.separator + "codecwav" + File.separator + "20101023" + File.separator + "codecwav-20101023.jar";
    public static String MINECRAFT_LIBRARIES_LIBRARY_JAVA_SOUND_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "paulscode" + File.separator + "libraryjavasound" + File.separator + "20101123" + File.separator + "libraryjavasound-20101123.jar";
    public static String MINECRAFT_LIBRARIES_LIBRARY_LWJGL_OPENAL_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "paulscode" + File.separator + "librarylwjglopenal" + File.separator + "20100824" + File.separator + "librarylwjglopenal-20100824.jar";
    public static String MINECRAFT_LIBRARIES_SOUNDSYSTEM_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "paulscode" + File.separator + "soundsystem" + File.separator + "20120107" + File.separator + "soundsystem-20120107.jar";
    public static String MINECRAFT_LIBRARIES_NETTY_ALL_PATH = MINECRAFT_LIBRARIES_PATH + "io" + File.separator + "netty" + File.separator + "netty-all" + File.separator + "4.1.25.Final" + File.separator + "netty-all-4.1.25.Final.jar";
    public static String MINECRAFT_LIBRARIES_GUAVA_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "google" + File.separator + "guava" + File.separator + "guava" + File.separator + "21.0" + File.separator + "guava-21.0.jar";
    public static String MINECRAFT_LIBRARIES_COMMONS_LANG3_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "apache" + File.separator + "commons" + File.separator + "commons-lang3" + File.separator + "3.5" + File.separator + "commons-lang3-3.5.jar";
    public static String MINECRAFT_LIBRARIES_COMMONS_IO_PATH = MINECRAFT_LIBRARIES_PATH + "commons-io" + File.separator + "commons-io" + File.separator + "2.4" + File.separator + "commons-io-2.4.jar";
    public static String MINECRAFT_LIBRARIES_COMMONS_CODEC_PATH = MINECRAFT_LIBRARIES_PATH + "commons-codec" + File.separator + "commons-codec" + File.separator + "1.10" + File.separator + "commons-codec-1.10.jar";
    public static String MINECRAFT_LIBRARIES_JINPUT_PATH = MINECRAFT_LIBRARIES_PATH + "net" + File.separator + "java" + File.separator + "jinput" + File.separator + "jinput" + File.separator + "2.0.5" + File.separator + "jinput-2.0.5.jar";
    public static String MINECRAFT_LIBRARIES_JUTILS_PATH = MINECRAFT_LIBRARIES_PATH + "net" + File.separator + "java" + File.separator + "jutils" + File.separator + "jutils" + File.separator + "1.0.0" + File.separator + "jutils-1.0.0.jar";
    public static String MINECRAFT_LIBRARIES_GSON_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "google" + File.separator + "code" + File.separator + "gson" + File.separator + "gson" + File.separator + "2.8.0" + File.separator + "gson-2.8.0.jar";
    public static String MINECRAFT_LIBRARIES_AUTHLIB_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "mojang" + File.separator + "authlib" + File.separator + "1.6.25" + File.separator + "authlib-1.6.25.jar";
    public static String MINECRAFT_LIBRARIES_BRIGADIER_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "mojang" + File.separator + "brigadier" + File.separator + "1.0.17" + File.separator + "brigadier-1.0.17.jar";
    public static String MINECRAFT_LIBRARIES_DATAFIXERUPPER_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "mojang" + File.separator + "datafixerupper" + File.separator + "3.0.25" + File.separator + "datafixerupper-3.0.25.jar";
    public static String MINECRAFT_LIBRARIES_JAVABRIDGE_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "mojang" + File.separator + "javabridge" + File.separator + "1.0.22" + File.separator + "javabridge-1.0.22.jar";
    public static String MINECRAFT_LIBRARIES_PATCHY_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "mojang" + File.separator + "patchy" + File.separator + "1.1" + File.separator + "patchy-1.1.jar";
    public static String MINECRAFT_LIBRARIES_REALMS_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "mojang" + File.separator + "realms" + File.separator + "1.14.17" + File.separator + "realms-1.14.17.jar";
    public static String MINECRAFT_LIBRARIES_TEXT2SPEECH_PATH = MINECRAFT_LIBRARIES_PATH + "com" + File.separator + "mojang" + File.separator + "text2speech" + File.separator + "1.11.3" + File.separator + "text2speech-1.11.3.jar";
    public static String MINECRAFT_LIBRARIES_ARGO_PATH = MINECRAFT_LIBRARIES_PATH + "argo" + File.separator + "argo" + File.separator + "2.25_fixed" + File.separator + "argo-2.25_fixed.jar";
    public static String MINECRAFT_LIBRARIES_BCPROV_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "bouncycastle" + File.separator + "bcprov-jdk15on" + File.separator + "1.47" + File.separator + "bcprov-jdk15on-1.47.jar";
    public static String MINECRAFT_LIBRARIES_LOG4J_CORE_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "apache" + File.separator + "logging" + File.separator + "log4j" + File.separator + "log4j-core" + File.separator + "2.8.1" + File.separator + "log4j-core-2.8.1.jar";
    public static String MINECRAFT_LIBRARIES_LOG4J_API_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "apache" + File.separator + "logging" + File.separator + "log4j" + File.separator + "log4j-api" + File.separator + "2.8.1" + File.separator + "log4j-api-2.8.1.jar";
    public static String MINECRAFT_LIBRARIES_FASTUTIL_PATH = MINECRAFT_LIBRARIES_PATH + "it" + File.separator + "unimi" + File.separator + "dsi" + File.separator + "fastutil" + File.separator + "8.2.1" + File.separator + "fastutil-8.2.1.jar";
    public static String MINECRAFT_LIBRARIES_LWJGL_GLFW_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-glfw" + File.separator + "3.2.2" + File.separator + "lwjgl-glfw-3.2.2.jar";
    public static String MINECRAFT_LIBRARIES_LWJGL_GLFW_NATIVES_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-glfw" + File.separator + "3.2.2" + File.separator + "lwjgl-glfw-3.2.2-natives-windows.jar";
    public static String MINECRAFT_LIBRARIES_LWJGL_OPENGL_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-opengl" + File.separator + "3.2.2" + File.separator + "lwjgl-opengl-3.2.2.jar";
    public static String MINECRAFT_LIBRARIES_LWJGL_OPENGL_NATIVES_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-opengl" + File.separator + "3.2.2" + File.separator + "lwjgl-opengl-3.2.2-natives-windows.jar";
    public static String MINECRAFT_LIBRARIES_LWJGL_OPENAL_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-openal" + File.separator + "3.2.2" + File.separator + "lwjgl-openal-3.2.2.jar";
    public static String MINECRAFT_LIBRARIES_LWJGL_OPENAL_NATIVES_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-openal" + File.separator + "3.2.2" + File.separator + "lwjgl-openal-3.2.2-natives-windows.jar";
    public static String MINECRAFT_LIBRARIES_LWJGL_STB_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-stb" + File.separator + "3.2.2" + File.separator + "lwjgl-stb-3.2.2.jar";
    public static String MINECRAFT_LIBRARIES_LWJGL_STB_NATIVES_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "lwjgl" + File.separator + "lwjgl-stb" + File.separator + "3.2.2" + File.separator + "lwjgl-stb-3.2.2-natives-windows.jar";
    public static String MINECRAFT_LIBRARIES_LWJGL_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "lwjgl" + File.separator + "lwjgl" + File.separator + "3.2.2" + File.separator + "lwjgl-3.2.2.jar";
    public static String MINECRAFT_LIBRARIES_LWJGL_NATIVES_PATH = MINECRAFT_LIBRARIES_PATH + "org" + File.separator + "lwjgl" + File.separator + "lwjgl" + File.separator + "3.2.2" + File.separator + "lwjgl-3.2.2-natives-windows.jar";



    public static final URL TEMPLATE_SKIN_PATH = LauncherFiles.class.getResource("/img/skin.png");
    public static final URL TEMPLATE_CLOAK_PATH = LauncherFiles.class.getResource("/img/cloak.png");
    public static final URL MISSING_TEXTURE = LauncherFiles.class.getResource("/img/missing.png");
    public static final URL VERSION_INFO_PATH = LauncherFiles.class.getResource("/version-info.json");


    public static File getMinecraftDirectory() {
        File workingDirectory;
        String applicationData, userHome = System.getProperty("user.home", ".");

        switch (OSUtils.getPlatform()) {
            case solaris:
                workingDirectory = new File(userHome, String.valueOf('.') + "minecraft/");
                break;
            case windows:
                applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    workingDirectory = new File(applicationData, "." + "minecraft/");
                    break;
                }
                workingDirectory = new File(userHome, String.valueOf('.') + "minecraft/");
            break;
                case macosx:
                workingDirectory = new File(userHome, "Library/Application Support/minecraft/");
                break;
            default:
                workingDirectory = new File(userHome, "minecraft/"); break;
        }
        if (!workingDirectory.exists() && !workingDirectory.mkdirs())
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);

        return workingDirectory;
    }

    static {
        new File(MINEONLINE_CACHE_FOLDER).mkdirs();
    }

}
