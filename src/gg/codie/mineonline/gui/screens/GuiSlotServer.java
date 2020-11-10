package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.client.gui.Tessellator;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.client.ThreadPollServers;
import gg.codie.mineonline.gui.rendering.FontRenderer;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class GuiSlotServer extends GuiSlot
{

    public GuiSlotServer(GuiMultiplayer guimultiplayer)
    {
        super(guimultiplayer.getWidth(), guimultiplayer.getHeight(), 32, guimultiplayer.getHeight() - 55, 36);
        guiMultiplayer = guimultiplayer;
    }

    protected int getSize()
    {
        return guiMultiplayer != null ? guiMultiplayer.getServers().size() : 0;
    }

    protected void elementClicked(int i, boolean flag)
    {
        GuiMultiplayer.func_35326_a(guiMultiplayer, i);
        boolean flag1 = GuiMultiplayer.func_35333_b(guiMultiplayer) >= 0 && GuiMultiplayer.func_35333_b(guiMultiplayer) < getSize();
        GuiMultiplayer.func_35329_c(guiMultiplayer).enabled = flag1;
        if(flag && flag1)
        {
            GuiMultiplayer.func_35332_b(guiMultiplayer, i);
        }
    }

    protected boolean isSelected(int i)
    {
        return i == GuiMultiplayer.func_35333_b(guiMultiplayer);
    }

    protected int getContentHeight()
    {
        return guiMultiplayer.getServers().size() * 36;
    }

    protected void drawBackground()
    {
        guiMultiplayer.drawDefaultBackground();
    }

    protected void drawSlot(int i, int j, int k, int l, Tessellator tessellator)
    {
//        ServerNBTStorage servernbtstorage = (ServerNBTStorage)GuiMultiplayer.getServers(guiMultiplayer).get(i);
//        synchronized(GuiMultiplayer.func_35321_g())
//        {
//            if(GuiMultiplayer.func_35338_m() < 5 && !servernbtstorage.field_35790_f)
//            {
//                servernbtstorage.field_35790_f = true;
//                servernbtstorage.field_35792_e = -2L;
//                servernbtstorage.field_35791_d = "";
//                servernbtstorage.field_35794_c = "";
//                GuiMultiplayer.func_35331_n();
//                (new ThreadPollServers(this, servernbtstorage)).start();
//            }
//        }
        MineOnlineServer server = guiMultiplayer.getServers().get(i);

        MinecraftVersion version = MinecraftVersionRepository.getSingleton().getVersionByMD5(server.md5);
        String versionName = "Unknown Version";
        if (version != null) {
            if (version.clientName != null) {
                versionName = version.clientName;
            } else if (version.clientVersions.length > 0) {
                versionName = Arrays.toString(version.clientVersions).replace("[", "").replace("]", "");
            } else {
                versionName = version.name;
            }


//                    boolean clientInstalled = false;
//
//                    found:
//                    for (String clientVersion : version.clientVersions) {
//                        for (MinecraftVersion installedClient : MinecraftVersionRepository.getSingleton().getInstalledClients()) {
//                            if (installedClient.baseVersion.equals(clientVersion)) {
//                                clientInstalled = true;
//                                break found;
//                            }
//                        }
//                    }

//                    if (!clientInstalled) {
//                        info2 = info2 + " - Not Installed!";
//                    }
        }

        guiMultiplayer.drawString(server.name, j + 2, k + 1, 0xffffff);
        guiMultiplayer.drawString(versionName, j + 2, k + 12, 0x808080);
        String users = server.isMineOnline ? "" + server.users : "?";
        guiMultiplayer.drawString(users + "/" + server.maxUsers, (j + 215) - FontRenderer.minecraftFontRenderer.getStringWidth(users + "/" + server.maxUsers), k + 12, 0x808080);
        guiMultiplayer.drawString(server.onlineMode ? "Online Mode" : "", j + 2, k + 12 + 11, 0x55FF55);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.GUI_ICONS));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        int i1;
        int j1;
        String s;
        Long latency;
        if(ThreadPollServers.serverLatencies.containsKey(server.connectAddress + ":" + server.port) && (latency = ThreadPollServers.serverLatencies.get(server.connectAddress + ":" + server.port)) != -2L)
        {
            i1 = 0;
            j1 = 0;
            if(latency < 0L)
            {
                j1 = 5;
            } else
            if(latency < 150L)
            {
                j1 = 0;
            } else
            if(latency< 300L)
            {
                j1 = 1;
            } else
            if(latency < 600L)
            {
                j1 = 2;
            } else
            if(latency < 1000L)
            {
                j1 = 3;
            } else
            {
                j1 = 4;
            }
            if(latency < 0L)
            {
                s = "(no connection)";
            } else
            {
                s = (new StringBuilder()).append(latency).append("ms").toString();
            }
        } else
        {
            i1 = 1;
            j1 = (int)(System.currentTimeMillis() / 100L + (long)(i * 2) & 7L);
            if(j1 > 4)
            {
                j1 = 8 - j1;
            }
            s = "Polling..";
        }
        guiMultiplayer.drawTexturedModalRect(j + 205, k, 0 + i1 * 10, 176 + j1 * 8, 10, 8);
        byte byte0 = 4;
        if(field_35409_k >= (j + 205) - byte0 && field_35408_l >= k - byte0 && field_35409_k <= j + 205 + 10 + byte0 && field_35408_l <= k + 8 + byte0)
        {
            guiMultiplayer.setTooltip(s);
        }
        // TODO: Players Tooltip
//        if(field_35409_k >= (j + 205) - byte0 && field_35408_l >= k && field_35409_k <= j + 205 + 10 + byte0 && field_35408_l <= k + 12 + byte0)
//        {
//            guiMultiplayer.setTooltip(Arrays.toString(server.players).replace("[", "").replace("]", "").replace(",", "\n"));
//        }
    }

    final GuiMultiplayer guiMultiplayer; /* synthetic field */
}
