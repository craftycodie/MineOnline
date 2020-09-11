package gg.codie.mineonline;

import gg.codie.minecraft.server.Files;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.utils.ArrayUtils;
import gg.codie.utils.MD5Checksum;

import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

public abstract class ServerLauncher {

    public final String jarPath;
    public final String md5;
    protected Properties serverProperties;
    public String serverlistAddress;
    public String serverlistPort;
    protected final MinecraftVersion minecraftVersion;
    int users = 0;
    static String[] playerNames = new String[0];
    protected String serverUUID;
    // If the player count was requested by MineOnline we remove that from stdout to avoid spamming logs.
    // Since the server might be responding slowly, we count the amount of times this has been requested,
    // to ensure each is removed.
    int playerCountRequested = 0;

    protected final String classicPlayersPath;
    protected final String whitelistPath;
    protected final String whitelistPlayersJSONPath;
    protected final String bannedPath;
    protected final String bannedIpsPath;
    protected final String bannedIpsJSONPath;
    protected final String bannedPlayersJSONPath;

    public ServerLauncher(String jarPath) throws Exception {
        this.jarPath = jarPath;
        md5 = MD5Checksum.getMD5ChecksumForFile(jarPath);
        minecraftVersion = MinecraftVersionRepository.getSingleton().getVersionByMD5(md5);

        try {
            serverProperties = gg.codie.minecraft.server.Properties.loadServerProperties(jarPath);
        } catch (Exception ex) {
            serverProperties = new Properties();
        }

        if (serverProperties.containsKey("serverlist-ip")) {
            serverlistAddress = serverProperties.getProperty("serverlist-ip");
        }

        if (serverProperties.containsKey("serverlist-port")) {
            serverlistPort = serverProperties.getProperty("serverlist-port");
        }

        classicPlayersPath = jarPath.replace(Paths.get(jarPath).getFileName().toString(), "players.txt");
        whitelistPath = jarPath.replace(Paths.get(jarPath).getFileName().toString(), "whitelist.txt");
        whitelistPlayersJSONPath = jarPath.replace(Paths.get(jarPath).getFileName().toString(), "whitelist.json");
        bannedPath = jarPath.replace(Paths.get(jarPath).getFileName().toString(), "banned-players.txt");
        bannedIpsPath = jarPath.replace(Paths.get(jarPath).getFileName().toString(), "banned-ips.txt");
        bannedIpsJSONPath = jarPath.replace(Paths.get(jarPath).getFileName().toString(), "banned-ips.json");
        bannedPlayersJSONPath = jarPath.replace(Paths.get(jarPath).getFileName().toString(), "banned-players.json");

        File classicUsers = new File(classicPlayersPath);
        if(classicUsers.exists()) {
            classicUsers.delete();
            classicUsers.createNewFile();
        }
    }

    protected void redirectOutput(final InputStream src, final PrintStream dest) {
        new Thread(new Runnable() {
            public void run() {
                Scanner inScanner = new Scanner(src);
                try {
                    while(true) {
                        if(inScanner.hasNext()) {
                            String nextLine = inScanner.nextLine();

                            if(nextLine.contains("logged in with entity id") || nextLine.contains(" lost connection: ") || nextLine.contains(" logged in as ") || nextLine.contains(" disconnected")) {
                                updatePlayerCount = true;
                            }

                            if(nextLine.length() > 27 && nextLine.substring(27).startsWith("Connected players: ")) {
                                users = 0;
                                if(nextLine.length() > 46){
                                    users = nextLine.split(",").length;
                                }

                                playerNames = new String[0];
                                if (users == 1) {
                                    playerNames = new String[] {nextLine.substring(46)};
                                } else if (users > 1) {
                                    playerNames = nextLine.substring(46).split(", ");
                                }
                                if(updatingPlayerCount) {
                                    updatingPlayerCount = false;
                                    updatedPlayerCount = true;
                                }

                            } else if(nextLine.length() > 33 && nextLine.substring(33).startsWith("There are ") && nextLine.substring(33).contains(" players online")) {
                                try {
                                    users = Integer.parseInt(nextLine.substring(33).split(" ")[2]);
                                    playerNames = new String[0];
                                    if(users == 1)
                                        playerNames = new String[] { nextLine.split(": ")[2] };
                                    else if (users > 1)
                                        playerNames = nextLine.split(": ")[2].replace(", ", " ").split(" ");
                                } catch (NumberFormatException ex) {
                                    // ignore.
                                }
                                if(updatingPlayerCount) {
                                    updatingPlayerCount = false;
                                    updatedPlayerCount = true;
                                }
                            }
                            if(playerCountRequested > 0) {
                                playerCountRequested--;
                            }
                            else {
                                dest.write(nextLine.getBytes("UTF-8"));
                                dest.write("\n".getBytes());
                            }
                        }
                    }
                } catch (IOException e) { // just exit
                }
            }
        }).start();
    }

    private long lastPing = System.currentTimeMillis();
    private boolean updatePlayerCount;
    private boolean updatingPlayerCount;
    private boolean updatedPlayerCount;

    void handleBroadcast(BufferedWriter writer) {
        if(updatePlayerCount) {
            if(minecraftVersion != null && minecraftVersion.hasHeartbeat) {
                updatePlayerCount = false;
                updatedPlayerCount = true;
            } else {
                playerCountRequested++;
                try {
                    writer.write("list");
                    writer.newLine();
                    writer.flush();
                } catch (Exception ex) {

                }
                updatePlayerCount = false;
                updatingPlayerCount = true;
            }
        }

        if (System.currentTimeMillis() - lastPing > 45000 || updatedPlayerCount) {
            updatedPlayerCount = false;
            try {
                try {
                    serverProperties = gg.codie.minecraft.server.Properties.loadServerProperties(jarPath);
                } catch (Exception ex) {
                    serverProperties = new Properties();
                }

                String[] whitelistedPlayers = new String[0];
                String[] whitelistedIPs = new String[0];
                String[] whitelistedUUIDs = new String[0];
                String[] bannedPlayers ;
                String[] bannedIPs;
                String[] bannedUUIDs;

                if (!updatingPlayerCount) {
                    playerCountRequested++;
                    writer.write("list");
                    writer.newLine();
                    writer.flush();
                }

                boolean whitelisted = false;
                if (serverProperties.containsKey("white-list")) {
                    whitelisted = serverProperties.getProperty("white-list").equals("true");
                }

                if(whitelisted) {
                    whitelistedPlayers = Files.readUsersFile(whitelistPath);
                    String[][] jsonData = Files.readPlayersJSON(whitelistPlayersJSONPath);
                    whitelistedPlayers = ArrayUtils.concatenate(whitelistedPlayers, jsonData[1]);
                    whitelistedUUIDs = jsonData[0];
                }

                bannedPlayers = Files.readUsersFile(bannedPath);
                String[][] jsonData = Files.readPlayersJSON(bannedPlayersJSONPath);
                bannedPlayers = ArrayUtils.concatenate(bannedPlayers, jsonData[1]);
                bannedUUIDs = jsonData[0];

                bannedIPs = Files.readUsersFile(bannedIpsPath);
                bannedIPs = ArrayUtils.concatenate(bannedIPs, Files.readBannedIPsJSON(bannedIpsJSONPath));

                if (minecraftVersion != null && minecraftVersion.hasHeartbeat) {
                    playerNames = Files.readUsersFile(classicPlayersPath);
                }

                serverUUID = MineOnlineAPI.listServer(
                        serverlistAddress != null && !serverlistAddress.isEmpty() ? serverlistAddress : serverProperties.getProperty("server-ip", null),
                        serverlistPort != null && !serverlistPort.isEmpty() ? serverlistPort : serverProperties.getProperty("server-port", serverProperties.getProperty("port", "25565")),
                        minecraftVersion != null && minecraftVersion.hasHeartbeat ? -1 : users,
                        Integer.parseInt(serverProperties.getProperty("max-players")),
                        serverProperties.getProperty("server-name", "Untitled Server"),
                        serverProperties.getProperty("online-mode", serverProperties.getProperty("verify-names", "true")).equals("true"),
                        md5,
                        whitelisted,
                        whitelistedPlayers,
                        whitelistedIPs,
                        whitelistedUUIDs,
                        bannedPlayers,
                        bannedIPs,
                        bannedUUIDs,
                        serverProperties.getProperty("owner", null),
                        playerNames
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            lastPing = System.currentTimeMillis();
        }
    }
}
