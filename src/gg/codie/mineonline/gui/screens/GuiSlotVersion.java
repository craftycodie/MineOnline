package gg.codie.mineonline.gui.screens;

import gg.codie.minecraft.client.gui.Tessellator;
import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;

import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class GuiSlotVersion extends GuiSlot
{
    public interface ISelectableVersionCompare {
        boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion);
    }

    public static class SelectableVersion {
        public final MinecraftVersion version;
        public final String path;

        public SelectableVersion(MinecraftVersion version, String path) {
            this.version = version;
            this.path = path;
        }
    }

    public GuiSlotVersion(GuiVersions parent, List<SelectableVersion> versions, ISelectableVersionCompare compare)
    {
        super(parent.getWidth(), parent.getHeight(), 32, parent.getHeight() - 55, 36);
        this.parent = parent;
        this.versions = versions;

        if (compare != null) {
            for (int i = 0; i < versions.size(); i++) {
                if (compare.isDefault(versions.get(i))) {
                    selectedIndex = i;
                    break;
                }
            }
        }
    }

    protected int getSize()
    {
        return versions != null ? versions.size() : 0;
    }

    protected void elementClicked(int i, boolean flag)
    {
        selectedIndex = i;
//        parent.select(i);
//        boolean flag1 = parent.getSelectedIndex() >= 0 && parent.getSelectedIndex() < getSize();
//        parent.getConnectButton().enabled = flag1;
//        if(flag && flag1)
//        {
//            parent.joinServer(i);
//        }
    }

    public String getSelectedPath() {
        // TODO: Download jars with no path.
        return versions.get(selectedIndex).path;
    }

    public SelectableVersion getSelected() {
        return selectedIndex < versions.size() ? versions.get(selectedIndex) : null;
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
        SelectableVersion selectableVersion = versions.get(i);

        parent.drawString(selectableVersion.version != null ? selectableVersion.version.name : Paths.get(selectableVersion.path).getFileName().toString(), j + 2, k + 1, 0xffffff);
        parent.drawString(selectableVersion.path != null ? Paths.get(selectableVersion.path).getFileName().toString() : "Download", j + 2, k + 12, 0x808080);
        parent.drawString(selectableVersion.version != null ? selectableVersion.version.info : "", j + 2, k + 12 + 11, 0x808080);
    }

    final GuiVersions parent; /* synthetic field */
    private int selectedIndex;
    private List<SelectableVersion> versions = new LinkedList<>();
}
