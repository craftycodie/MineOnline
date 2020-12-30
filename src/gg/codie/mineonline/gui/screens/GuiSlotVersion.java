package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.gui.rendering.Font;

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
        super(parent.getWidth(), parent.getHeight(), 32, parent.getHeight() - 55, 36, 220);
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

        for (int i = 0; i < getSize();  i++) {
            if (isSelected(i)) {
                amountScrolled = 36 * (i - 1);
                break;
            }
        }
    }

    protected int getSize()
    {
        return versions != null ? versions.size() : 0;
    }

    protected void elementClicked(int slotIndex, boolean doubleClicked)
    {
        selectedIndex = slotIndex;
        boolean flag1 = selectedIndex >= 0 && selectedIndex < getSize();
        if(doubleClicked && flag1)
        {
            parent.versionSelected();
        }
    }

    public String getSelectedPath() throws Exception {
        SelectableVersion version = versions.get(selectedIndex);
        if (version.path == null) {
            return version.version.download();
        }

        return versions.get(selectedIndex).path;
    }

    public SelectableVersion getSelected() {
        return selectedIndex < versions.size() ? versions.get(selectedIndex) : null;
    }

    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == selectedIndex;
    }

    protected int getContentHeight()
    {
        return getSize() * 36;
    }

    protected void drawBackground()
    {
        parent.drawDefaultBackground();
    }

    protected void drawSlot(int slotIndex, int xPos, int yPos, int zPos)
    {
        SelectableVersion selectableVersion = versions.get(slotIndex);

        Font.minecraftFont.drawString(selectableVersion.version != null ? selectableVersion.version.name : "Unknown Version", xPos + 2, yPos + 1, 0xffffff);
        Font.minecraftFont.drawString(selectableVersion.path != null ? Paths.get(selectableVersion.path).getFileName().toString() : "Download", xPos + 2, yPos + 12, 0x808080);
        Font.minecraftFont.drawString(selectableVersion.version != null ? selectableVersion.version.info : "", xPos + 2, yPos + 12 + 11, 0x808080);
    }

    final GuiVersions parent;
    private int selectedIndex;
    private List<SelectableVersion> versions = new LinkedList<>();
}
