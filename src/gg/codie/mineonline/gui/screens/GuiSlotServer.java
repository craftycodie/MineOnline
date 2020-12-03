package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.gui.rendering.FontRenderer;
import gg.codie.mineonline.gui.rendering.Loader;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.textures.EGUITexture;
import gg.codie.mineonline.server.ThreadPollServers;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;

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
        return guiMultiplayer.getServers().size() * 36;
    }

    protected void drawBackground()
    {
        guiMultiplayer.drawDefaultBackground();
    }

    protected void drawSlot(int slotIndex, int xPos, int yPos, int zPos)
    {
        resize(guiMultiplayer.getWidth(), guiMultiplayer.getHeight(), 32, (guiMultiplayer.getHeight() - 55));

        MineOnlineServer server = guiMultiplayer.getServers().get(slotIndex);

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

        if (server.serverIcon != null) {
            BufferedImage image;
            byte[] imageByte;
            try {
                Base64.Decoder decoder = Base64.getDecoder();
                imageByte = decoder.decode(server.serverIcon);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                image = ImageIO.read(bis);
                bis.close();

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(image, "png", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.loadTexture("/servers/" + server.ip + ":" + server.port + "/server-icon.png", is));
            } catch (Exception e) {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.UNKNOWN_PACK));
                e.printStackTrace();
            }
        } else {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.UNKNOWN_PACK));
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Renderer.singleton.startDrawingQuads();
        Renderer.singleton.setColorRGBA(255, 255, 255, 255);
        Renderer.singleton.addVertexWithUV(xPos, yPos + zPos, 0.0D, 0.0D, 1.0D);
        Renderer.singleton.addVertexWithUV(xPos + 32, yPos + zPos, 0.0D, 1.0D, 1.0D);
        Renderer.singleton.addVertexWithUV(xPos + 32, yPos, 0.0D, 1.0D, 0.0D);
        Renderer.singleton.addVertexWithUV(xPos, yPos, 0.0D, 0.0D, 0.0D);
        Renderer.singleton.draw();

        FontRenderer.minecraftFontRenderer.drawString(server.name, xPos + 32 + 2, yPos + 1, 0xffffff);
        FontRenderer.minecraftFontRenderer.drawString(versionName, xPos + 32 + 2, yPos + 12, 0x808080);
        String users = server.isMineOnline ? "" + server.users : "?";
        FontRenderer.minecraftFontRenderer.drawString(users + "/" + server.maxUsers, (xPos + slotWidth - 4) - FontRenderer.minecraftFontRenderer.getStringWidth(users + "/" + server.maxUsers), yPos + 12, 0x808080);

        if (server.motd != null)
            FontRenderer.minecraftFontRenderer.drawString(server.motd, xPos + 32 + 2, yPos + 12 + 11, 0x808080);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Loader.singleton.getGuiTexture(EGUITexture.MINEONLINE_GUI_ICONS));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        if (server.featured)
            Renderer.singleton.drawSprite(xPos + slotWidth - 29, yPos, 20, 184, 10, 8);
        else if (server.onlineMode)
            Renderer.singleton.drawSprite(xPos + slotWidth - 29, yPos, 20, 176, 10, 8);

        if (server.whitelisted)
            if (server.featured || server.onlineMode)
                Renderer.singleton.drawSprite(xPos + slotWidth - 44, yPos, 20, 192, 10, 8);
            else
                Renderer.singleton.drawSprite(xPos + slotWidth - 29, yPos, 20, 192, 10, 8);


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

        if (mouseX >= (xPos + slotWidth - 29) - byte0 && mouseY >= yPos - byte0 && mouseX <= xPos + (slotWidth - 29) + 10 + byte0 && mouseY <= yPos + 8 + byte0)
        {
            if (server.featured)
                guiMultiplayer.setTooltip("Featured");
            else if (server.onlineMode)
                guiMultiplayer.setTooltip("Online Mode");
            else if (server.whitelisted)
                guiMultiplayer.setTooltip("Whitelisted");
        }

        if (mouseX >= (xPos + slotWidth - 44) - byte0 && mouseY >= yPos - byte0 && mouseX <= xPos + (slotWidth - 44) + 10 + byte0 && mouseY <= yPos + 8 + byte0)
        {
            if (server.whitelisted && (server.onlineMode || server.featured))
                guiMultiplayer.setTooltip("Whitelisted");
        }
        // TODO: Players Tooltip
//        if(mouseX >= (j + 205) - byte0 && mouseY >= k && mouseX <= j + 205 + 10 + byte0 && mouseY <= k + 12 + byte0)
//        {
//            parent.setTooltip(Arrays.toString(server.players).replace("[", "").replace("]", "").replace(",", "\n"));
//        }
    }

    final GuiMultiplayer guiMultiplayer;
}
