package gg.codie.mineonline.server;

import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.discord.DiscordChatBridge;
import gg.codie.mineonline.discord.MessageRecievedListener;
import gg.codie.mineonline.utils.Logging;
import gg.codie.utils.MD5Checksum;
import org.lwjgl.Sys;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class MinecraftServerLauncher {

    public final String jarPath;
    private String md5;
    protected Properties serverProperties;
    public String serverlistAddress;
    public String serverlistPort;
    protected MinecraftVersion minecraftVersion;
    int users = 0;
    static String[] playerNames = new String[0];
    protected String serverUUID;
    // If the player count was requested by MineOnline we remove that from stdout to avoid spamming logs.
    // Since the server might be responding slowly, we count the amount of times this has been requested,
    // to ensure each is removed.
    int playerCountRequested = 0;
    BufferedWriter writer;
    DiscordChatBridge discord;
    String[] line;
    String[] content;
    String username;
    String message;
    String ver;

    public MinecraftServerLauncher(String[] args) throws Exception {

        this.jarPath = args[0];
        md5 = MD5Checksum.getMD5ChecksumForFile(jarPath);
        minecraftVersion = MinecraftVersionRepository.getSingleton(true).getVersionByMD5(md5);

        try {
            serverProperties = new Properties(jarPath);
        } catch (Exception ex) {
            serverProperties = new Properties(null);
        }

        if (serverProperties.versionMD5() != null) {
            md5 = serverProperties.versionMD5();
            if (minecraftVersion == null) {
                minecraftVersion = MinecraftVersionRepository.getSingleton().getVersionByMD5(md5);
            }
        }

        if (minecraftVersion != null)
            ver = minecraftVersion.name;
        else
            ver = "";

        if (serverProperties.discordToken() != null && serverProperties.discordChan() != null && serverProperties.discordWebhookUrl() != null) { // Create the discord bot if token and channel are present
            discord = new DiscordChatBridge(ver, serverProperties.discordChan(), serverProperties.discordToken(), serverProperties.discordWebhookUrl(), new MessageRecievedListener() {
                @Override
                public void onMessageRecieved(String message) {
                    serverCommand(message);
                }
            });
        } else if (serverProperties.discordToken() != null && serverProperties.discordChan() != null && serverProperties.discordWebhookUrl() == null) { // Create the discord bot if token and channel are present
            discord = new DiscordChatBridge(ver, serverProperties.discordChan(), serverProperties.discordToken(), "", new MessageRecievedListener() {
                @Override
                public void onMessageRecieved(String message) {
                    serverCommand(message);
                }
            });
        }

        if (minecraftVersion != null){
            if (serverProperties.discordToken() != null) {
                discord.sendDiscordMessage("", "Launching " + minecraftVersion.name + " server: **" + serverProperties.serverName() + "**");
            }
            System.out.println("Launching Server " + minecraftVersion.name);
        }
        else
            if (serverProperties.discordToken() != null) {
                discord.sendDiscordMessage("", "Launching " + this.jarPath + " server: **" + serverProperties.serverName() + "**");
            }
            System.out.println("Launching Server " + this.jarPath);



        serverlistAddress = serverProperties.serverIP();
        serverlistPort = "" + serverProperties.serverPort();

        Process serverProcess = MinecraftServerProcess.startMinecraftServer(args);

        Thread closeLauncher = new Thread() {
            public void run() {
                Runtime.getRuntime().halt(0);
            }
        };

        Runtime.getRuntime().addShutdownHook(closeLauncher);

        redirectOutput(serverProcess.getInputStream(), System.out);

        OutputStream stdin = serverProcess.getOutputStream();

        writer = new BufferedWriter(new OutputStreamWriter(stdin));

        if (!serverProperties.isPublic()) {
            System.out.println("Your server is not public and will not be shown on the server list.\n" +
                    "You can change this by setting public to true in the server.properties file.\n" +
                    "Players can connect via " + serverProperties.serverIP() + ":" + serverProperties.serverPort());
        }

        Scanner scanner = new Scanner(System.in);
        while(serverProcess.isAlive()) {
            boolean reading = true;
            while (reading) {
                if (System.in.available() > 0) {
                    writer.write(scanner.nextLine());
                    writer.newLine();
                    writer.flush();
                } else {
                    reading = false;
                }
            }

            if(serverProperties.isPublic()) {
                handleBroadcast(writer);
            }
        }

        if (serverUUID != null)
            MineOnlineAPI.deleteServerListing(serverUUID);

        scanner.close();

        try {
            Runtime.getRuntime().halt(serverProcess.exitValue());
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            Runtime.getRuntime().halt(1);
        }
    }
    protected void redirectOutput(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                Scanner inScanner = new Scanner(src);
                while(true) {
                    if(inScanner.hasNext()) {
                        String nextLine = inScanner.nextLine();
                        String[] prevPlayers = playerNames.clone();

                        try {
                            if (minecraftVersion != null && minecraftVersion.hasHeartbeat) {

                                if (nextLine.length() > 15) {
                                    if (nextLine.contains(" logged in as ")) {
                                        playerNames = Arrays.copyOf(playerNames, playerNames.length + 1);
                                        playerNames[playerNames.length - 1] = nextLine.substring(nextLine.indexOf(" logged in as ") + " logged in as".length() + 1);
                                        updatedPlayerCount = true;
                                    } else if (nextLine.endsWith("disconnected")) {
                                        LinkedList<String> linkedList = new LinkedList<>();
                                        Collections.addAll(linkedList, playerNames);
                                        linkedList.remove(nextLine.substring(15, nextLine.indexOf("(") - 1));
                                        playerNames = linkedList.toArray(new String[linkedList.size()]);
                                        updatedPlayerCount = true;
                                    }
                                }

                            } else {
                                if (nextLine.contains("logged in with entity id") || nextLine.contains(" lost connection: ") || nextLine.contains(" logged in as ") || nextLine.contains(" disconnected")) {
                                    updatePlayerCount = true;
                                }

                                if (nextLine.length() > 27 && nextLine.substring(27).startsWith("Connected players: ")) {
                                    users = 0;
                                    if (nextLine.length() > 46) {
                                        users = nextLine.split(",").length;
                                    }

                                    playerNames = new String[0];
                                    if (users == 1) {
                                        playerNames = new String[]{nextLine.substring(46)};
                                    } else if (users > 1) {
                                        playerNames = nextLine.substring(46).split(", ");
                                    }
                                    if (updatingPlayerCount) {
                                        updatingPlayerCount = false;
                                        updatedPlayerCount = true;
                                    }

                                }
                                // Craftbukkit
                                else if (nextLine.length() > 16 && nextLine.substring(16).startsWith("Connected players: ")) {
                                    users = 0;
                                    if (nextLine.length() > 35) {
                                        users = nextLine.substring(35).replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "").split(",").length;
                                    }

                                    playerNames = new String[0];
                                    if (users == 1) {
                                        playerNames = new String[]{nextLine.replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "").substring(35)};
                                    } else if (users > 1) {
                                        playerNames = nextLine.substring(35).replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "").split(", ");
                                    }
                                    if (updatingPlayerCount) {
                                        updatingPlayerCount = false;
                                        updatedPlayerCount = true;
                                    }

                                    if (Arrays.equals(playerNames, new String[] { "" })) {
                                        users = 0;
                                        playerNames = new String[0];
                                    }
                                }
                                else if (nextLine.length() > 33 && nextLine.substring(33).startsWith("There are ") && nextLine.substring(33).contains(" players online")) {
                                    try {

                                        users = Integer.parseInt(nextLine.substring(33).split(" ")[2]);
                                        playerNames = new String[0];
                                        if (users == 1)
                                            playerNames = new String[]{nextLine.split(": ")[2]};
                                        else if (users > 1)
                                            playerNames = nextLine.split(": ")[2].replace(", ", " ").split(" ");


                                    } catch (NumberFormatException ex) {
                                        // ignore.
                                    }

                                    if (updatingPlayerCount) {
                                        updatingPlayerCount = false;
                                        updatedPlayerCount = true;
                                    }
                                }
                            }

                            if(playerCountRequested > 0) {
                                playerCountRequested--;
                            }
                            else {
                                dest.write(nextLine.getBytes("UTF-8"));
                                dest.write("\n".getBytes());
                            }

                            // Discord Chat Bridge ( mc chat listener )
                            if (serverProperties.discordToken() != null && !nextLine.startsWith("say") && !nextLine.contains("[CONSOLE]")) {
                                if (nextLine.length() > 15 && nextLine.contains(" says: ")) { // For classic
                                    line = nextLine.substring(15).replace("\u001B[0m", "").split(" says: ");
                                    discord.sendDiscordMessage(line[0], line[1]);
                                }

                                if (nextLine.contains("INFO] <")) { // For not classic
                                    line = nextLine.replace("\u001B[0m", "").split("INFO] <");
                                    content = line[1].split("> ");
                                    discord.sendDiscordMessage(content[0], content[1]);
                                }

                                if (nextLine.contains("INFO]: <")) { // For release
                                    line = nextLine.replace("\u001B[0m", "").split("INFO]: <");
                                    content = line[1].split("> ");
                                    discord.sendDiscordMessage(content[0], content[1]);
                                }
                            }

                            if(serverProperties.discordToken() != null) { // Send discord message when player joins or leaves the server
                                for (String names : playerNames) {
                                    boolean join = Arrays.stream(prevPlayers).anyMatch(names::equals);
                                    if (!join) {
                                        discord.sendDiscordMessage("","**" + names + "** joined **" + serverProperties.serverName() + "**");
                                    }
                                }
                                for (String names : prevPlayers) {
                                    boolean left = Arrays.stream(playerNames).anyMatch(names::equals);
                                    if (!left) {
                                        discord.sendDiscordMessage("","**" + names + "** left **" + serverProperties.serverName() + "**");
                                    }
                                }
                            }

                        } catch (Exception e) { // just exit
                            if(playerCountRequested > 0) {
                                playerCountRequested--;
                            }
                            else {
                                try {
                                    dest.write(nextLine.getBytes("UTF-8"));
                                    dest.write("\n".getBytes());
                                } catch (Exception ex) {
                                    // ignore.
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private long lastPing;
    private boolean updatePlayerCount;
    private boolean updatingPlayerCount;
    private boolean updatedPlayerCount;

    public void serverCommand(String command){ // Send a command to the server
        try {
            writer.newLine();
            writer.write(command);
            writer.newLine();
            writer.flush();
        } catch (Exception ex) {

        }
    }

    void handleBroadcast(BufferedWriter writer) {
        if(updatePlayerCount) {
            if(minecraftVersion != null && minecraftVersion.hasHeartbeat) {
                updatedPlayerCount = true;
            } else {
                playerCountRequested++;
                serverCommand("list");
                updatePlayerCount = false;
                updatingPlayerCount = true;
            }
        }

        if (System.currentTimeMillis() - lastPing > 45000 || updatedPlayerCount) {
            updatedPlayerCount = false;
            try {
                try {
                    serverProperties = new Properties(jarPath);
                } catch (Exception ex) {
                    serverProperties = new Properties(null);
                }

                if (!updatingPlayerCount && !(minecraftVersion != null && minecraftVersion.hasHeartbeat)) {
                    playerCountRequested++;
                    serverCommand("list");
                }

                boolean whitelisted = serverProperties.isWhitelisted();

                serverUUID = MineOnlineAPI.listServer(
                        serverProperties.serverIP(),
                        "" + serverProperties.serverPort(),
                        playerNames.length,
                        serverProperties.maxPlayers(),
                        serverProperties.serverName(),
                        serverProperties.onlineMode(),
                        md5,
                        whitelisted,
                        playerNames
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            lastPing = System.currentTimeMillis();
        }
    }

    public static void main(String[] args) throws Exception{
        Logging.enableLogging();

        File jarFile = new File(args[0]);
        if(!jarFile.exists()) {
            System.err.println("Couldn't find jar file " + args[0]);
            System.exit(1);
        }

        new MinecraftServerLauncher(args);
    }
}
