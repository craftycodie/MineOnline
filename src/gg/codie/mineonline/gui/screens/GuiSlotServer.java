package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.client.gui.Tessellator;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.server.ThreadPollServers;
import gg.codie.mineonline.gui.rendering.FontRenderer;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class GuiSlotServer extends GuiSlot
{

    public GuiSlotServer(GuiMultiplayer guimultiplayer)
    {
        super(guimultiplayer.getWidth(), guimultiplayer.getHeight(), 32, guimultiplayer.getHeight() - 55, 36, 304);
        guiMultiplayer = guimultiplayer;
    }

    protected int getSize()
    {
        return guiMultiplayer != null ? guiMultiplayer.getServers().size() : 0;
    }

    protected void elementClicked(int i, boolean flag)
    {
        guiMultiplayer.select(i);
        boolean flag1 = guiMultiplayer.getSelectedIndex() >= 0 && guiMultiplayer.getSelectedIndex() < getSize();
        guiMultiplayer.getConnectButton().enabled = flag1;
        if(flag && flag1)
        {
            guiMultiplayer.joinServer(i);
        }
    }

    protected boolean isSelected(int i)
    {
        return i == guiMultiplayer.getSelectedIndex();
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
        resize(guiMultiplayer.getWidth(), guiMultiplayer.getHeight(), 32, (guiMultiplayer.getHeight() - 55));

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
        }

//        TexturePackBase texturepackbase = MinecraftTexturePackRepository.singleton.getTexturePacks().get(i);
//        texturepackbase.bindThumbnailTexture();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.UNKNOWN_PACK));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(0xffffff);
        tessellator.addVertexWithUV(j, k + l, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(j + 32, k + l, 0.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(j + 32, k, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(j, k, 0.0D, 0.0D, 0.0D);
        tessellator.draw();

        guiMultiplayer.drawString(server.name, j + 32 + 2, k + 1, 0xffffff);
        guiMultiplayer.drawString(versionName, j + 32 + 2, k + 12, 0x808080);
        String users = server.isMineOnline ? "" + server.users : "?";
        guiMultiplayer.drawString(users + "/" + server.maxUsers, (j + slotWidth - 4) - FontRenderer.minecraftFontRenderer.getStringWidth(users + "/" + server.maxUsers), k + 12, 0x808080);

        if (server.motd != null)
            guiMultiplayer.drawString(server.motd, j + 32 + 2, k + 12 + 11, 0x808080);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.MINEONLINE_GUI_ICONS));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        if (server.featured)
            guiMultiplayer.drawTexturedModalRect(j + slotWidth - 29, k, 20, 184, 10, 8);
        else if (server.onlineMode)
            guiMultiplayer.drawTexturedModalRect(j + slotWidth - 29, k, 20, 176, 10, 8);


        int connectionIconTypeIndex;
        int connectionIconIndex;
        String tooltipText;
        Long latency;
        if(ThreadPollServers.serverLatencies.containsKey(server.connectAddress + ":" + server.port) && (latency = ThreadPollServers.serverLatencies.get(server.connectAddress + ":" + server.port)) != -2L)
        {
            connectionIconTypeIndex = 0;
            if(latency < 0L)
            {
                connectionIconIndex = 5;
            } else
            if(latency < 150L)
            {
                connectionIconIndex = 0;
            } else
            if(latency< 300L)
            {
                connectionIconIndex = 1;
            } else
            if(latency < 600L)
            {
                connectionIconIndex = 2;
            } else
            if(latency < 1000L)
            {
                connectionIconIndex = 3;
            } else
            {
                connectionIconIndex = 4;
            }
            if(latency < 0L)
            {
                tooltipText = "(no connection)";
            } else
            {
                tooltipText = (new StringBuilder()).append(latency).append("ms").toString();
            }
        } else
        {
            connectionIconTypeIndex = 1;
            connectionIconIndex = (int)(System.currentTimeMillis() / 100L + (long)(i * 2) & 7L);
            if(connectionIconIndex > 4)
            {
                connectionIconIndex = 8 - connectionIconIndex;
            }
            tooltipText = "Polling..";
        }
        guiMultiplayer.drawTexturedModalRect(j + slotWidth - 14, k, 0 + connectionIconTypeIndex * 10, 176 + connectionIconIndex * 8, 10, 8);
        byte byte0 = 4;
        if (field_35409_k >= (j + slotWidth - 14) - byte0 && field_35408_l >= k - byte0 && field_35409_k <= j + (slotWidth - 14) + 10 + byte0 && field_35408_l <= k + 8 + byte0)
        {
            guiMultiplayer.setTooltip(tooltipText);
        }

        if (field_35409_k >= (j + slotWidth - 29) - byte0 && field_35408_l >= k - byte0 && field_35409_k <= j + (slotWidth - 29) + 10 + byte0 && field_35408_l <= k + 8 + byte0)
        {
            if (server.featured)
                guiMultiplayer.setTooltip("Featured Server");
            else if (server.onlineMode)
                guiMultiplayer.setTooltip("Online Mode (Secured)");
        }
        // TODO: Players Tooltip
//        if(field_35409_k >= (j + 205) - byte0 && field_35408_l >= k && field_35409_k <= j + 205 + 10 + byte0 && field_35408_l <= k + 12 + byte0)
//        {
//            parent.setTooltip(Arrays.toString(server.players).replace("[", "").replace("]", "").replace(",", "\n"));
//        }
    }

    final GuiMultiplayer guiMultiplayer; /* synthetic field */
}
