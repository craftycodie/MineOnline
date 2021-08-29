package gg.codie.mineonline;

import java.util.Properties;

public class Globals {
    static {
        final Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(".properties"));
            LAUNCHER_VERSION = properties.getProperty("version");
            DEV = Boolean.parseBoolean(properties.getProperty("devMode"));
            DISCORD_APP_ID = properties.getProperty("discordClientId");
            BRANCH = properties.getProperty("branch");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static String LAUNCHER_VERSION;
    public static boolean DEV;
    public static String DISCORD_APP_ID;
    public static String BRANCH;
}
