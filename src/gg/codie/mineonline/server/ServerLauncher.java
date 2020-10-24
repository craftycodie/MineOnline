package gg.codie.mineonline.server;

import gg.codie.minecraft.server.Players;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.utils.MD5Checksum;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public abstract class ServerLauncher {

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
    protected final String classicPlayersPath;


    public ServerLauncher(String jarPath) throws Exception {
        this.jarPath = jarPath;
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
            System.out.println("Launching Server " + minecraftVersion.name);
        else
            System.out.println("Launching Server " + this.jarPath);

        serverlistAddress = serverProperties.serverIP();
        serverlistPort = "" + serverProperties.serverPort();

        classicPlayersPath = jarPath.replace(Paths.get(jarPath).getFileName().toString(), "players.txt");
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
                while(true) {
                    if(inScanner.hasNext()) {
                        String nextLine = inScanner.nextLine();
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
                                        users = nextLine.substring(35).replaceAll(
                                                "\u001B\\[[;\\d]*[ -/]*[@-~]", "").split(",").length;
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
                        } catch (Exception e) { // just exit
                            if(playerCountRequested > 0) {
                                playerCountRequested--;
                            }
                            else {
                                try {
                                    dest.write(nextLine.getBytes("UTF-8"));
                                    dest.write("\n".getBytes());
                                } catch (Exception ex) {
                                    // ingore.
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

    void handleBroadcast(BufferedWriter writer) {
        if(updatePlayerCount) {
            if(minecraftVersion != null && minecraftVersion.hasHeartbeat) {
                updatePlayerCount = false;
                updatedPlayerCount = true;
            } else {
                playerCountRequested++;
                try {
                    writer.newLine();
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
                    serverProperties = new Properties(jarPath);
                } catch (Exception ex) {
                    serverProperties = new Properties(null);
                }

                if (!updatingPlayerCount) {
                    playerCountRequested++;
                    writer.newLine();
                    writer.write("list");
                    writer.newLine();
                    writer.flush();
                }

                boolean whitelisted = serverProperties.isWhitelisted();

                if (minecraftVersion != null && minecraftVersion.readPlayersFile) {
                    playerNames = Players.readClassicPlayersFile(classicPlayersPath);
                    users = playerNames.length;
                }

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
}
