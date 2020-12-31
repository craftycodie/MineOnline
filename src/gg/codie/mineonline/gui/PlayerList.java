package gg.codie.mineonline.gui;

import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MineOnlineAPI;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.patches.SocketConstructAdvice;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerList
{
    static ArrayList<String> players;
    static int maxPlayers;
    boolean lastRequestDone = true;
    static long lastRequest = 0;

    public PlayerList() {
        requestPlayers();
    }

    public void requestPlayers() {
        if(lastRequestDone && lastRequest < System.currentTimeMillis() - 10000 && SocketConstructAdvice.serverAddress != null && LegacyGameManager.getVersion() != null && LegacyGameManager.getVersion().usePlayerList) {
            lastRequestDone = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        MineOnlineServer server = MineOnlineAPI.getServer(SocketConstructAdvice.serverAddress.getHostAddress(), "" + SocketConstructAdvice.serverPort);
                        players = new ArrayList<>(Arrays.asList(server.players));
                        if (!players.contains(Session.session.getUsername()))
                            players.add(Session.session.getUsername());
                        maxPlayers = server.maxUsers;
                    } catch (IOException ex) {
                        // ignore.
                    }
                    lastRequestDone = true;
                    lastRequest = System.currentTimeMillis();
                }
            }).start();
        }
    }

    public void drawScreen()
    {
        if (!Display.isActive())
            return;

        if (!Mouse.isGrabbed())
            return;

        if (LegacyGameManager.getVersion() == null || !LegacyGameManager.getVersion().usePlayerList)
            return;

        requestPlayers();

        if((SocketConstructAdvice.serverAddress != null) && players != null)
        {
            int playerCount = players.size(); // used to be max players, looks better like this though.
            int rows = playerCount;
            int cols = 1;
            for(; rows > 20; rows = ((playerCount + cols) - 1) / cols)
            {
                cols++;
            }

            int nameWidth = 300 / cols;
            if(nameWidth > 150)
            {
                nameWidth = 150;
            }
            int x = (GUIScale.lastScaledWidth() - cols * nameWidth) / 2;
            int y = 10;
            Renderer.singleton.drawRect(x - 1, y - 1, x + nameWidth * cols, y + 9 * rows, 0x80000000);
            for(int playerIndex = 0; playerIndex < playerCount; playerIndex++)
            {
                int nameX = x + (playerIndex % cols) * nameWidth;
                int nameY = y + (playerIndex / cols) * 9;
                Renderer.singleton.drawRect(nameX, nameY, (nameX + nameWidth) - 1, nameY + 8, 0x20ffffff);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                if(playerIndex >= players.size())
                {
                    continue;
                }
                Font.minecraftFont.drawStringWithShadow(players.get(playerIndex), nameX, nameY, 0xffffff);
            }

        }
    }
}
