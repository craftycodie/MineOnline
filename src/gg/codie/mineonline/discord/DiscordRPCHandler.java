package gg.codie.mineonline.discord;

import gg.codie.common.utils.OSUtils;
import gg.codie.minecraft.server.MinecraftColorCodeProvider;
import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.LibraryManager;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.utils.JREUtils;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.JoinGameCallback;
import net.arikia.dev.drpc.callbacks.JoinRequestCallback;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Map;

// This is handled on the startup thread, that's the only way to implement joining.
public class DiscordRPCHandler {
    private static String serverIP;
    private static String serverPort;
    private static String versionName;
    private static String username;
    private static String uuid;

    private static long lastServerUpdate = System.currentTimeMillis();
    private static long startTimestamp = System.currentTimeMillis() / 1000;

    private static boolean hasUpdate;

    // Other threads/processes can write a file to update presence on the main RPC thread.
    public static void play(String versionName, String serverIP, String serverPort) {
        try {
            DiscordRPCHandler.versionName = versionName;
            DiscordRPCHandler.serverIP = serverIP;
            DiscordRPCHandler.serverPort = serverPort;
            hasUpdate = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void updateServer(String serverIP, String serverPort) {
        if(serverIP != null) {
            try {
                String hostAddress = InetAddress.getByName(serverIP).getHostAddress();
                if (hostAddress.equals(InetAddress.getLocalHost().getHostAddress()) || hostAddress.equals(InetAddress.getLoopbackAddress().getHostAddress())) {
                    String externalIP = MineOnlineAPI.getExternalIP();
                    if (externalIP != null && !externalIP.isEmpty()) {
                        DiscordRPCHandler.serverIP = externalIP;
                    } else {
                        DiscordRPCHandler.serverIP = serverIP;
                    }
                } else {
                    DiscordRPCHandler.serverIP = serverIP;
                }
                DiscordRPCHandler.serverPort = serverPort;
            } catch (UnknownHostException ex) {
                DiscordRPCHandler.serverIP = serverIP;
                DiscordRPCHandler.serverPort = serverPort;
            }
        } else {
            DiscordRPCHandler.serverIP = null;
            DiscordRPCHandler.serverPort = null;
        }
        hasUpdate = true;
    }

    private static void play(String versionName, String serverIP, String serverPort, String username, String uuid) {
        boolean isUpdate = false;

        DiscordRPCHandler.uuid = uuid;

        if (versionName.equals(DiscordRPCHandler.versionName)
                && ((serverIP == null && DiscordRPCHandler.serverIP == null) || (serverIP != null && serverIP.equals(DiscordRPCHandler.serverIP)))
                && ((serverPort == null && DiscordRPCHandler.serverPort == null) || (serverPort != null && serverPort.equals(DiscordRPCHandler.serverPort))))
            isUpdate = true;

        if (!isUpdate) {
            startTimestamp = System.currentTimeMillis() / 1000;
        }

        DiscordRPCHandler.serverIP = serverIP;
        DiscordRPCHandler.serverPort = serverPort;
        DiscordRPCHandler.versionName = versionName;
        DiscordRPCHandler.username = username;

        if (DiscordRPCHandler.serverIP != null && (DiscordRPCHandler.serverIP.isEmpty() || DiscordRPCHandler.serverIP.equals("null")))
            DiscordRPCHandler.serverIP = null;
        if (DiscordRPCHandler.serverPort != null && (DiscordRPCHandler.serverPort.isEmpty() || DiscordRPCHandler.serverPort.equals("null")))
            DiscordRPCHandler.serverPort = null;

        if(DiscordRPCHandler.serverIP == null) {
            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Playing Single-Player");
            presence.setDetails(DiscordRPCHandler.versionName);
            presence.setStartTimestamps(startTimestamp);
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
                if (ex.getClass() != FileNotFoundException.class)
                    ex.printStackTrace();
            }

            DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(server != null ? new MinecraftColorCodeProvider().removeColorCodes(server.name) : (DiscordRPCHandler.serverIP + (!DiscordRPCHandler.serverPort.equals("25565") ? (":" + DiscordRPCHandler.serverPort) : "")));
            presence.setDetails(DiscordRPCHandler.versionName);
            presence.setStartTimestamps(startTimestamp);
            presence.setSecrets(DiscordRPCHandler.serverIP + ", " + DiscordRPCHandler.serverPort, null);
            try {
                if (InetAddress.getByName(DiscordRPCHandler.serverIP).isAnyLocalAddress())
                    presence.setParty(externalIP + ":" + DiscordRPCHandler.serverPort, server != null ? (server.users > 0 ? server.users : 1) : 1, server != null ? server.maxUsers : 24);
                else
                    presence.setParty(DiscordRPCHandler.serverIP + ":" + DiscordRPCHandler.serverPort, server != null ? (server.users > 0 ? server.users : 1) : 1, server != null ? server.maxUsers : 24);
            } catch (Exception ex) {
                presence.setParty(DiscordRPCHandler.serverIP + ":" + DiscordRPCHandler.serverPort, server != null ? (server.users > 0 ? server.users : 1) : 1, server != null ? server.maxUsers : 24);

            }
            presence.setBigImage("keyart", null);
            presence.setSmallImage("block", DiscordRPCHandler.username);

            DiscordRPC.discordUpdatePresence(presence.build());
        }
        DiscordRPCHandler.lastServerUpdate = System.currentTimeMillis();
    }

    static String externalIP;

    private static Thread discordThread;

    public static void initialize(){
        externalIP = MineOnlineAPI.getExternalIP();

        discordThread = new Thread(() -> {
            DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
                System.out.println("Discord logged in " + user.username + "#" + user.discriminator + "!");
                DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("In the launcher.");
                presence.setDetails("Version " + Globals.LAUNCHER_VERSION + (Globals.DEV ? " Dev" : ""));
                presence.setBigImage("block", null);
                DiscordRPC.discordUpdatePresence(presence.build());
            })
            .setJoinGameEventHandler(new JoinGameCallback() {
                @Override
                public void apply(String s) {
                    try {
                        System.out.println("Joining " + s);

                        LinkedList<String> launchArgs = new LinkedList();
                        launchArgs.add(JREUtils.getRunningJavaExecutable());
                        launchArgs.add("-javaagent:" + LauncherFiles.PATCH_AGENT_JAR);
                        launchArgs.add("-Djava.util.Arrays.useLegacyMergeSort=true");
                        launchArgs.add("-cp");
                        launchArgs.add(LibraryManager.getClasspath(true, true, new String[]{new File(MenuManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath(), LauncherFiles.DISCORD_RPC_JAR}));
                        launchArgs.add(MenuManager.class.getCanonicalName());
                        launchArgs.add("-server");
                        launchArgs.add(s.replace(", ", ":"));

                        java.util.Properties props = System.getProperties();
                        ProcessBuilder processBuilder = new ProcessBuilder(launchArgs);

                        Map<String, String> env = processBuilder.environment();
                        for(String prop : props.stringPropertyNames()) {
                            env.put(prop, props.getProperty(prop));
                        }
                        processBuilder.directory(new File(System.getProperty("user.dir")));

                        processBuilder.inheritIO().start();

                        if (LegacyGameManager.isInGame()) {
                            LegacyGameManager.closeGame();
                        }

                        System.exit(0);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Failed to join game.");
                    }
                }
            })
            .setJoinRequestEventHandler(new JoinRequestCallback() {
                @Override
                public void apply(DiscordUser discordUser) {
                    DiscordRPC.discordRespond(discordUser.userId, DiscordRPC.DiscordReply.YES);
                }
            })
            .build();
            DiscordRPC.discordInitialize(Globals.DISCORD_APP_ID, handlers, false);
            try {
                String launchJava = "\"" + System.getProperty("java.home") + File.separator + "bin" + File.separator + "javaw.exe\" -jar \"";
                if (!OSUtils.isWindows())
                    launchJava.replace(".exe", "s");
                DiscordRPC.discordRegister(Globals.DISCORD_APP_ID, launchJava + Paths.get(LibraryManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString() + "\"");
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            while (!Thread.currentThread().isInterrupted()) {
                DiscordRPC.discordRunCallbacks();

                if (hasUpdate || DiscordRPCHandler.serverIP != null &&  System.currentTimeMillis() - DiscordRPCHandler.lastServerUpdate > 60000) {
                    play(DiscordRPCHandler.versionName, DiscordRPCHandler.serverIP, DiscordRPCHandler.serverPort, DiscordRPCHandler.username, DiscordRPCHandler.uuid);
                    hasUpdate = false;
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "Join-Callback-Handler");

        discordThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> discordThread.interrupt()));
    }

    public static void stop() {
        if (discordThread != null && !discordThread.isInterrupted())
            discordThread.interrupt();
    }
}
