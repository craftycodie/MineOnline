package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.textures.EGUITexture;
import gg.codie.mineonline.server.ThreadPollServers;
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
        if (MinecraftVersionRepository.getSingleton().isLoadingInstalledVersions())
            return 0;

        return guiMultiplayer != null ? guiMultiplayer.getServers().size() : 0;
    }

    protected void elementClicked(int slotIndex, boolean doubleClicked)
    {
        guiMultiplayer.select(slotIndex);
        boolean flag1 = guiMultiplayer.getSelectedIndex() >= 0 && guiMultiplayer.getSelectedIndex() < getSize();
        guiMultiplayer.getConnectButton().enabled = flag1;
        if(doubleClicked && flag1)
        {
            guiMultiplayer.joinServer(slotIndex);
        }
    }

    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == guiMultiplayer.getSelectedIndex();
    }

    protected int getContentHeight()
    {
        return getSize() * 36;
    }

    protected void drawBackground()
    {
        guiMultiplayer.drawDefaultBackground();
    }

    @Override
    public void drawScreen(int mousex, int mousey) {
        super.drawScreen(mousex, mousey);

        if (MinecraftVersionRepository.getSingleton().isLoadingInstalledVersions())
            Font.minecraftFont.drawCenteredStringWithShadow("Loading versions...", guiMultiplayer.getWidth() / 2, guiMultiplayer.getHeight() / 2, 0x808080);
    }

    protected void drawSlot(int slotIndex, int xPos, int yPos, int zPos)
    {
        resize(guiMultiplayer.getWidth(), guiMultiplayer.getHeight(), 32, (guiMultiplayer.getHeight() - 55));

        MineOnlineServer server = guiMultiplayer.getServers().get(slotIndex);

        MinecraftVersion version = MinecraftVersionRepository.getSingleton().getVersionByMD5(server.clientMD5);
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

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.UNKNOWN_PACK));

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Renderer.singleton.startDrawingQuads();
        Renderer.singleton.setColorRGBA(255, 255, 255, 255);
        Renderer.singleton.addVertexWithUV(xPos, yPos + zPos, 0.0D, 0.0D, 1.0D);
        Renderer.singleton.addVertexWithUV(xPos + 32, yPos + zPos, 0.0D, 1.0D, 1.0D);
        Renderer.singleton.addVertexWithUV(xPos + 32, yPos, 0.0D, 1.0D, 0.0D);
        Renderer.singleton.addVertexWithUV(xPos, yPos, 0.0D, 0.0D, 0.0D);
        Renderer.singleton.draw();

        Font.minecraftFont.drawString(server.name, xPos + 32 + 2, yPos + 1, 0xffffff);
        Font.minecraftFont.drawString(versionName, xPos + 32 + 2, yPos + 12, 0x808080);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.MINEONLINE_GUI_ICONS));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        int connectionIconTypeIndex;
        int connectionIconIndex;
        String tooltipText;
        Long latency;
        if(ThreadPollServers.serverLatencies.containsKey(server.address) && (latency = ThreadPollServers.serverLatencies.get(server.address)) != -2L)
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
            connectionIconIndex = (int)(System.currentTimeMillis() / 100L + (long)(slotIndex * 2) & 7L);
            if(connectionIconIndex > 4)
            {
                connectionIconIndex = 8 - connectionIconIndex;
            }
            tooltipText = "Polling..";
        }
        Renderer.singleton.drawSprite(xPos + slotWidth - 14, yPos, 0 + connectionIconTypeIndex * 10, 176 + connectionIconIndex * 8, 10, 8);
        byte byte0 = 4;
        if (mouseX >= (xPos + slotWidth - 14) - byte0 && mouseY >= yPos - byte0 && mouseX <= xPos + (slotWidth - 14) + 10 + byte0 && mouseY <= yPos + 8 + byte0)
        {
            guiMultiplayer.setTooltip(tooltipText);
        }
    }

    final GuiMultiplayer guiMultiplayer;
}
