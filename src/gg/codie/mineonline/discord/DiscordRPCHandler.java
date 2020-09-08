package gg.codie.mineonline.discord;

import gg.codie.minecraft.client.Options;
import gg.codie.mineonline.*;
import gg.codie.mineonline.api.LegacyAPI;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.utils.LastLogin;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.JoinGameCallback;
import net.arikia.dev.drpc.callbacks.JoinRequestCallback;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

// This is handled on the startup thread, that's the only way to implement joining.
public class DiscordRPCHandler {
    private static String serverIP;
    private static String serverPort;
    private static String versionName;
    private static String username;

    private static long lastServerUpdate = System.currentTimeMillis() / 1000;

    private static void play(String versionName, String serverIP, String serverPort, String username) {
        DiscordRPCHandler.serverIP = serverIP;
        DiscordRPCHandler.serverPort = serverPort;
        DiscordRPCHandler.versionName = versionName;
        DiscordRPCHandler.username = username;

        if(DiscordRPCHandler.serverIP.isEmpty() || DiscordRPCHandler.serverIP.equals("null"))
            DiscordRPCHandler.serverIP = null;
        if(DiscordRPCHandler.serverPort.isEmpty() || DiscordRPCHandler.serverPort.equals("null"))
            DiscordRPCHandler.serverPort = null;

        if(DiscordRPCHandler.serverIP == null) {
            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Playing Single-Player");
            presence.setDetails(DiscordRPCHandler.versionName);
            presence.setStartTimestamps(System.currentTimeMillis() / 1000);
            presence.setBigImage("keyart", null);
            presence.setSmallImage("block", DiscordRPCHandler.username);
            DiscordRPC.discordUpdatePresence(presence.build());
        }
        else {
            if(DiscordRPCHandler.serverPort == null)
                DiscordRPCHandler.serverPort = "25565";

            MineOnlineServer server = null;

            try {
                server = MineOnlineAPI.getServer(DiscordRPCHandler.serverIP, DiscordRPCHandler.serverPort);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(server != null ? server.name : (DiscordRPCHandler.serverIP + ":" + (!DiscordRPCHandler.serverPort.equals("25565") ? DiscordRPCHandler.serverPort : "")));
            presence.setDetails(DiscordRPCHandler.versionName);
            presence.setStartTimestamps(System.currentTimeMillis() / 1000);
            presence.setSecrets(DiscordRPCHandler.serverIP + ", " + DiscordRPCHandler.serverPort, null);
            presence.setParty(DiscordRPCHandler.serverIP + ":" + DiscordRPCHandler.serverPort, server != null ? (server.users > 0 ? server.users : 1) : 1, server != null ? server.maxUsers : 24);
            //presence.setSecrets(serverIP + ":" + serverPort, null);
            presence.setBigImage("keyart", null);
            presence.setSmallImage("block", DiscordRPCHandler.username);

            DiscordRPC.discordUpdatePresence(presence.build());
            DiscordRPCHandler.lastServerUpdate = System.currentTimeMillis();
        }
    }

    public static void initialize(){
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Discord logged in " + user.username + "#" + user.discriminator + "!");
            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("In the launcher.");
            presence.setDetails("");
            presence.setBigImage("block", null);
            DiscordRPC.discordUpdatePresence(presence.build());
        })
        .setJoinGameEventHandler(new JoinGameCallback() {
            @Override
            public void apply(String s) {
                Startup.joinDiscord(s.replace(", ", ":"));
            }
        })
        .setJoinRequestEventHandler(new JoinRequestCallback() {
            @Override
            public void apply(DiscordUser discordUser) {
                DiscordRPC.discordRespond(discordUser.userId, DiscordRPC.DiscordReply.YES);
            }
        })
        .build();
        DiscordRPC.discordInitialize("718163542783819818", handlers, false);
        try {
            DiscordRPC.discordRegister("718163542783819818", "javaw -jar " + Paths.get(LibraryManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                DiscordRPC.discordRunCallbacks();

                File presenceFile = new File(LauncherFiles.MINEONLINE_RICH_PRESENCE_FILE);
                if(presenceFile.exists()) {
                    try {
                        List<String> lines = Files.readAllLines(Paths.get(LauncherFiles.MINEONLINE_RICH_PRESENCE_FILE));
                        play(lines.get(0), lines.get(1), lines.get(2), lines.get(3));
                    } catch (Exception ex) {

                    }
                    presenceFile.delete();
                }

                if (DiscordRPCHandler.serverIP != null &&  System.currentTimeMillis() - DiscordRPCHandler.lastServerUpdate > 60000)
                    play(DiscordRPCHandler.versionName, DiscordRPCHandler.serverIP, DiscordRPCHandler.serverPort, DiscordRPCHandler.username);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "Join-Callback-Handler").start();
    }
}
