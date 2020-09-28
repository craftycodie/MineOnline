package gg.codie.mineonline.discord;

import gg.codie.mineonline.*;
import gg.codie.mineonline.api.MineOnlineAPI;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class DiscordPresence {
    public static String lastVersion = null;

    // Other threads/processes can write a file to update presence on the main RPC thread.
    public static void play(String versionName, String serverIP, String serverPort) {
        try {
            lastVersion = versionName;

            new File(LauncherFiles.MINEONLINE_RICH_PRESENCE_FILE).getParentFile().mkdirs();
            new File(LauncherFiles.MINEONLINE_RICH_PRESENCE_FILE).delete();

            AsynchronousFileChannel fc = AsynchronousFileChannel.open(Paths.get(LauncherFiles.MINEONLINE_RICH_PRESENCE_FILE),
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.DELETE_ON_CLOSE ,
                    StandardOpenOption.READ, StandardOpenOption.WRITE,
                    StandardOpenOption.WRITE, StandardOpenOption.SYNC);

            fc.write(ByteBuffer.wrap((versionName + "\n" + serverIP + "\n" + serverPort + "\n" + Session.session.getUsername() + "\n" + Session.session.getUuid()).getBytes(StandardCharsets.UTF_8)), 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void updateServer(String serverIP, String serverPort) {
        try {
            String hostAddress = InetAddress.getByName(serverIP).getHostAddress();
            if (hostAddress.equals(InetAddress.getLocalHost().getHostAddress()) || hostAddress.equals(InetAddress.getLoopbackAddress().getHostAddress())) {
                String externalIP = MineOnlineAPI.getExternalIP();
                if(externalIP != null && !externalIP.isEmpty()) {
                    play(lastVersion, externalIP, serverPort);
                } else {
                    play(lastVersion, serverIP, serverPort);
                }
            } else {
                play(lastVersion, serverIP, serverPort);
            }
        } catch (UnknownHostException ex) {
            // ignore
            play(lastVersion, serverIP, serverPort);
        }
    }
}
