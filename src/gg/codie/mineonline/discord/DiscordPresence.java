package gg.codie.mineonline.discord;

import gg.codie.mineonline.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DiscordPresence {
    // Other threads/processes can write a file to update presence on the main RPC thread.
    public static void play(String versionName, String serverIP, String serverPort) {
        try {
            new File(LauncherFiles.MINEONLINE_RICH_PRESENCE_FILE).getParentFile().mkdirs();
            Files.write(Paths.get(LauncherFiles.MINEONLINE_RICH_PRESENCE_FILE), (versionName + "\n" + serverIP + "\n" + serverPort + "\n" + Session.session.getUsername() + "\n" + Session.session.getUuid()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
