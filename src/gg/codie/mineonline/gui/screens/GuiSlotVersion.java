package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.client.gui.Tessellator;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;

import java.nio.file.Paths;

public class GuiSlotVersion extends GuiSlot
{

    public GuiSlotVersion(GuiVersions parent)
    {
        super(parent.getWidth(), parent.getHeight(), 32, parent.getHeight() - 55, 36);
        this.parent = parent;
    }

    protected int getSize()
    {
        return 0;
    }

    protected void elementClicked(int i, boolean flag)
    {
//        parent.select(i);
//        boolean flag1 = parent.getSelectedIndex() >= 0 && parent.getSelectedIndex() < getSize();
//        parent.getConnectButton().enabled = flag1;
//        if(flag && flag1)
//        {
//            parent.joinServer(i);
//        }
    }

    protected boolean isSelected(int i)
    {
        return i == selectedIndex;
    }

    public int select(int i)
    {
        return selectedIndex = i;
    }

    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    protected int getContentHeight()
    {
        return getSize() * 36;
    }

    protected void drawBackground()
    {
        parent.drawDefaultBackground();
    }

    protected void drawSlot(int i, int j, int k, int l, Tessellator tessellator)
    {
        String path = (String)MinecraftVersionRepository.getSingleton().getInstalledJars().keySet().toArray()[i];
        MinecraftVersion version = MinecraftVersionRepository.getSingleton().getInstalledJars().get(path);

        parent.drawString(version != null ? version.name : Paths.get(path).getFileName().toString(), j + 2, k + 1, 0xffffff);
//        parent.drawString(versionName, j + 2, k + 12, 0x808080);
//        String users = server.isMineOnline ? "" + server.users : "?";
//        parent.drawString(users + "/" + server.maxUsers, (j + 215) - FontRenderer.minecraftFontRenderer.getStringWidth(users + "/" + server.maxUsers), k + 12, 0x808080);
//        parent.drawString(server.onlineMode ? "Online Mode" : "", j + 2, k + 12 + 11, 0x55FF55);
        byte byte0 = 4;
        if(field_35409_k >= (j + 205) - byte0 && field_35408_l >= k - byte0 && field_35409_k <= j + 205 + 10 + byte0 && field_35408_l <= k + 8 + byte0)
        {
//            parent.setTooltip(s);
        }
        // TODO: Players Tooltip
//        if(field_35409_k >= (j + 205) - byte0 && field_35408_l >= k && field_35409_k <= j + 205 + 10 + byte0 && field_35408_l <= k + 12 + byte0)
//        {
//            parent.setTooltip(Arrays.toString(server.players).replace("[", "").replace("]", "").replace(",", "\n"));
//        }
    }

    final GuiVersions parent; /* synthetic field */
    private int selectedIndex;
}
