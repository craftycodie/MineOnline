package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiToggleButton;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.Font;
import gg.codie.mineonline.gui.rendering.Renderer;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GuiVersions extends AbstractGuiScreen
{
    DropTargetAdapter dropTargetAdapter;
    boolean showReleaseOnOpen;

    public interface IVersionSelectListener {
        void onSelect(String path);
    }

    public void onGuiClosed() {
        dropTarget.removeDropTargetListener(dropTargetAdapter);
        Keyboard.enableRepeatEvents(false);
    }

    public void setSelectButtonText(String text) {
        playButton.displayString = text;
    }

    public GuiVersions(AbstractGuiScreen guiscreen, Predicate<GuiSlotVersion.SelectableVersion> filter, IVersionSelectListener onSelect, GuiSlotVersion.ISelectableVersionCompare compare, boolean autoSelectSingleJar, boolean showReleaseOnOpen)
    {
        this.showReleaseOnOpen = showReleaseOnOpen;

        dropTarget.setComponent(DisplayManager.getCanvas());

        dropTargetAdapter = new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                // Accept copy drops
                event.acceptDrop(DnDConstants.ACTION_COPY);

                // Get the transfer which can provide the dropped item data
                Transferable transferable = event.getTransferable();

                // Get the data formats of the dropped item
                DataFlavor[] flavors = transferable.getTransferDataFlavors();

                // Loop through the flavors
                for (DataFlavor flavor : flavors) {

                    try {

                        // If the drop items are files
                        if (flavor.isFlavorJavaFileListType()) {

                            // Get all of the dropped files
                            List<File> files = (List<File>) transferable.getTransferData(flavor);

                            // Loop them through
                            for (File file : files) {
                                MinecraftVersion minecraftVersion = MinecraftVersionRepository.getSingleton().getVersion(file.getPath());

                                try {
                                    if (!MinecraftVersion.isPlayableJar(file.getPath())) {
                                        continue;
                                    }
                                } catch (IOException ex) {
                                    continue;
                                }

                                MinecraftVersionRepository.getSingleton().addInstalledVersion(file.getPath());
                                MinecraftVersionRepository.getSingleton().selectJar(file.getPath());
                                reloadList = true;
                            }

                        }

                    } catch (Exception e) {

                        // Print out the error stack
                        e.printStackTrace();

                    }
                }

                // Inform that the drop is complete
                event.dropComplete(true);
            }
        };

        try {
            dropTarget.addDropTargetListener(dropTargetAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tooltip = null;
        parentScreen = guiscreen;
        this.filter = filter;
        this.onSelectListener = onSelect;
        this.compare = compare;

        fileChooser.setFileHidingEnabled(false);

        initGui();

        // If there's only one jar in the list and the list can be skipped, skip.
        if (filteredVersions().size() == 1 && autoSelectSingleJar) {
            try {
                dropTarget.removeDropTargetListener(dropTargetAdapter);
                this.onSelectListener.onSelect(guiSlotVersion.getSelectedPath());
            } catch (Exception ex) { }
        }
    }

    public void updateScreen()
    {
        guiSlotVersion.update();
    }

    protected void keyTyped(char c, int i)
    {
        guiSlotVersion.keyTyped(c, i);

        if(c == '\r')
        {
            try {
                dropTarget.removeDropTargetListener(dropTargetAdapter);
                this.onSelectListener.onSelect(guiSlotVersion.getSelectedPath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if (i == Keyboard.KEY_ESCAPE) {
            dropTarget.removeDropTargetListener(dropTargetAdapter);

            if (LegacyGameManager.isInGame())
                LegacyGameManager.setGUIScreen(parentScreen);
            else
                MenuManager.setMenuScreen(parentScreen);
        }
    }

    public void versionSelected() {
        try {
            dropTarget.removeDropTargetListener(dropTargetAdapter);
            onSelectListener.onSelect(guiSlotVersion.getSelectedPath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<GuiSlotVersion.SelectableVersion> filteredVersions() {
        LinkedList<GuiSlotVersion.SelectableVersion> versions = new LinkedList<>();

        // Add installed jars to the list.
        MinecraftVersionRepository.getSingleton().getInstalledJars().forEach((String path, MinecraftVersion version) -> {
            synchronized (versions) {
                for (GuiSlotVersion.SelectableVersion knownVersion : versions) {
                    // If we already have a jar of the same version downloaded, skip.
                    if (knownVersion.version == version && knownVersion.path != null)
                        return;

                    // If we have a jar of the same version but it isn't downloaded, and this one is, remove the old one.
                    if (knownVersion.version == version && path != null)
                        versions.remove(knownVersion);
                }
                versions.add(new GuiSlotVersion.SelectableVersion(version, path));
            }
        });
        // Add downloadable jars to the list.
        MinecraftVersionRepository.getSingleton().getDownloadableClients().forEach((MinecraftVersion version) -> {
            synchronized (versions) {
                for (GuiSlotVersion.SelectableVersion selectableVersion : versions) {
                    if (selectableVersion.version == version)
                        return;
                }

                versions.add(new GuiSlotVersion.SelectableVersion(version, null));
            }
        });

        List<GuiSlotVersion.SelectableVersion> filteredVersions = new LinkedList<>();

        for (GuiSlotVersion.SelectableVersion selectableVersion : versions) {
            if (selectableVersion.version != null) {
                if (selectableVersion.version.baseVersion.startsWith("rd") && classicButton.enabled)
                    filteredVersions.add(selectableVersion);
                else if (selectableVersion.version.baseVersion.startsWith("c") && classicButton.enabled)
                    filteredVersions.add(selectableVersion);
                else if (selectableVersion.version.baseVersion.startsWith("in") && indevButton.enabled)
                    filteredVersions.add(selectableVersion);
                else if (selectableVersion.version.baseVersion.startsWith("a") && alphaButton.enabled)
                    filteredVersions.add(selectableVersion);
                else if (selectableVersion.version.baseVersion.startsWith("b") && betaButton.enabled)
                    filteredVersions.add(selectableVersion);
                else if (releaseButton.enabled
                        && !selectableVersion.version.baseVersion.startsWith("rd")
                        && !selectableVersion.version.baseVersion.startsWith("c")
                        && !selectableVersion.version.baseVersion.startsWith("a")
                        && !selectableVersion.version.baseVersion.startsWith("b")
                        && !selectableVersion.version.baseVersion.startsWith("in"))
                    filteredVersions.add(selectableVersion);
            }
            else filteredVersions.add(selectableVersion);
        }

        if (filter != null)
            filteredVersions = filteredVersions.stream().filter(filter).collect(Collectors.toList());

        playButton.enabled = filteredVersions.size() > 0;

        filteredVersions = filteredVersions.stream().sorted(new Comparator<GuiSlotVersion.SelectableVersion>() {
            @Override
            public int compare(GuiSlotVersion.SelectableVersion o1, GuiSlotVersion.SelectableVersion o2) {
                String o1Name = o1.version != null ? o1.version.name : o1.path;
                String o2Name = o2.version != null ? o2.version.name : o2.path;

                return o1Name.compareTo(o2Name);
            }
        }).collect(Collectors.toList());

        return filteredVersions;
    }

    boolean versionsWereLoaded;
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        controlList.clear();
        addComponents();

        if (MinecraftVersionRepository.getSingleton().isLoadingInstalledVersions())
            guiSlotVersion = new GuiSlotVersion(this, new LinkedList<>(), compare);
        else {
            guiSlotVersion = new GuiSlotVersion(this, filteredVersions(), compare);
            versionsWereLoaded = true;
        }
    }

    // Keep track of the selection inbetween filtering.
    GuiSlotVersion.SelectableVersion lastSelected = null;
    public void addComponents()
    {
        controlList.add(new GuiButton(1, getWidth() / 2 - 154, getHeight() - 48, 100, 20, "Back", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                dropTarget.removeDropTargetListener(dropTargetAdapter);

                if (LegacyGameManager.isInGame())
                    LegacyGameManager.setGUIScreen(parentScreen);
                else
                    MenuManager.setMenuScreen(parentScreen);
            }
        }));
        GuiVersions thisScreen = this;
        controlList.add(classicButton = new GuiToggleButton(4, getWidth() / 2 + 54, 12, 20, 20, "c", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                classicButton.enabled = !classicButton.enabled;
                if (guiSlotVersion.getSelected() != null)
                    lastSelected = guiSlotVersion.getSelected();
                compare = new GuiSlotVersion.ISelectableVersionCompare() {
                    @Override
                    public boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion) {
                        return ((selectableVersion.version != null && lastSelected != null && lastSelected.version != null && selectableVersion.version.downloadURL != null && lastSelected.version.downloadURL != null) && selectableVersion.version.downloadURL.equals(lastSelected.version.downloadURL)) || (lastSelected != null && selectableVersion.path != null && lastSelected.path != null && lastSelected.path.equals(selectableVersion.path));
                    }
                };
                guiSlotVersion = new GuiSlotVersion(thisScreen, filteredVersions(), compare);
            }
        }));
        controlList.add(indevButton = new GuiToggleButton(4, getWidth() / 2 + 74, 12, 20, 20, "in", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                indevButton.enabled = !indevButton.enabled;
                if (guiSlotVersion.getSelected() != null)
                    lastSelected = guiSlotVersion.getSelected();
                compare = new GuiSlotVersion.ISelectableVersionCompare() {
                    @Override
                    public boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion) {
                        return ((selectableVersion.version != null && lastSelected != null && lastSelected.version != null && selectableVersion.version.downloadURL != null && lastSelected.version.downloadURL != null) && selectableVersion.version.downloadURL.equals(lastSelected.version.downloadURL)) || (lastSelected != null && selectableVersion.path != null && lastSelected.path != null && lastSelected.path.equals(selectableVersion.path));
                    }
                };
                guiSlotVersion = new GuiSlotVersion(thisScreen, filteredVersions(), compare);
            }
        }));
        controlList.add(alphaButton = new GuiToggleButton(4, getWidth() / 2 + 94, 12, 20, 20, "\u00F0", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                alphaButton.enabled = !alphaButton.enabled;
                if (guiSlotVersion.getSelected() != null)
                    lastSelected = guiSlotVersion.getSelected();
                compare = new GuiSlotVersion.ISelectableVersionCompare() {
                    @Override
                    public boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion) {
                        return ((selectableVersion.version != null && lastSelected != null && lastSelected.version != null && selectableVersion.version.downloadURL != null && lastSelected.version.downloadURL != null) && selectableVersion.version.downloadURL.equals(lastSelected.version.downloadURL)) || (lastSelected != null && selectableVersion.path != null && lastSelected.path != null && lastSelected.path.equals(selectableVersion.path));
                    }
                };
                guiSlotVersion = new GuiSlotVersion(thisScreen, filteredVersions(), compare);
            }
        }));
        controlList.add(betaButton = new GuiToggleButton(4, getWidth() / 2 + 114, 12, 20, 20, "\u00F1", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                betaButton.enabled = !betaButton.enabled;
                if (guiSlotVersion.getSelected() != null)
                    lastSelected = guiSlotVersion.getSelected();
                compare = new GuiSlotVersion.ISelectableVersionCompare() {
                    @Override
                    public boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion) {
                        return ((selectableVersion.version != null && lastSelected != null && lastSelected.version != null && selectableVersion.version.downloadURL != null && lastSelected.version.downloadURL != null) && selectableVersion.version.downloadURL.equals(lastSelected.version.downloadURL)) || (lastSelected != null && selectableVersion.path != null && lastSelected.path != null && lastSelected.path.equals(selectableVersion.path));
                    }
                };
                guiSlotVersion = new GuiSlotVersion(thisScreen, filteredVersions(), compare);
            }
        }));
        controlList.add(releaseButton = new GuiToggleButton(4, getWidth() / 2 + 134, 12, 20, 20, "+", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                releaseButton.enabled = !releaseButton.enabled;
                if (guiSlotVersion.getSelected() != null)
                    lastSelected = guiSlotVersion.getSelected();
                compare = new GuiSlotVersion.ISelectableVersionCompare() {
                    @Override
                    public boolean isDefault(GuiSlotVersion.SelectableVersion selectableVersion) {
                        return ((selectableVersion.version != null && lastSelected != null && lastSelected.version != null && selectableVersion.version.downloadURL != null && lastSelected.version.downloadURL != null) && selectableVersion.version.downloadURL.equals(lastSelected.version.downloadURL)) || (lastSelected != null && selectableVersion.path != null && lastSelected.path != null && lastSelected.path.equals(selectableVersion.path));
                    }
                };
                guiSlotVersion = new GuiSlotVersion(thisScreen, filteredVersions(), compare);
            }
        }));
        releaseButton.enabled = showReleaseOnOpen;
        controlList.add(playButton = new GuiButton(3, getWidth() / 2 + 4 + 50, getHeight() - 48, 100, 20, "Play", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                try {
                    dropTarget.removeDropTargetListener(dropTargetAdapter);
                    thisScreen.onSelectListener.onSelect(guiSlotVersion.getSelectedPath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }));
        controlList.add(new GuiButton(5, (getWidth() / 2) - 50, getHeight() - 48, 100, 20, "Browse", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int returnVal = fileChooser.showOpenDialog(DisplayManager.getCanvas());

                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();

                            try {
                                if (!MinecraftVersion.isPlayableJar(file.getPath())) {
                                    JOptionPane.showMessageDialog(null, "This jar file is incompatible:\nNo applet or main class found.");
                                    return;
                                }
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "This jar file is incompatible:\nFailed to open.");
                                return;
                            }

                            MinecraftVersionRepository.getSingleton().addInstalledVersion(file.getPath());
                            MinecraftVersionRepository.getSingleton().selectJar(file.getPath());

                            reloadList = true;
                        }
                    }
                });
            }
        }));
    }

    public void resize() {
        controlList.get(0).resize(getWidth() / 2 - 154, getHeight() - 48);
        controlList.get(1).resize(getWidth() / 2 + 54, 12);
        controlList.get(2).resize(getWidth() / 2 + 74, 12);
        controlList.get(3).resize(getWidth() / 2 + 94, 12);
        controlList.get(4).resize(getWidth() / 2 + 114, 12);
        controlList.get(5).resize(getWidth() / 2 + 134, 12);
        controlList.get(6).resize(getWidth() / 2 + 4 + 50, getHeight() - 48);
        controlList.get(7).resize((getWidth() / 2) - 50, getHeight() - 48);
        guiSlotVersion.resize(getWidth(), getHeight(), 32, getHeight() - 55);
    }

    boolean reloadList;
    public void drawScreen(int mouseX, int mouseY)
    {
        resize();

        tooltip = null;
        drawDefaultBackground();

        playButton.enabled = guiSlotVersion != null && guiSlotVersion.getSelected() != null;

        if (MinecraftVersionRepository.getSingleton().isLoadingInstalledVersions()) {
            guiSlotVersion.drawScreen(mouseX, mouseY);
            Font.minecraftFont.drawCenteredStringWithShadow("Loading versions...", getWidth() / 2, getHeight() / 2, 0x808080);
        } else if (!versionsWereLoaded) {
            guiSlotVersion = new GuiSlotVersion(this, filteredVersions(), compare);
            guiSlotVersion.drawScreen(mouseX, mouseY);
            versionsWereLoaded = true;
        } else {
            if (reloadList) {
                guiSlotVersion = new GuiSlotVersion(this, filteredVersions(), compare);
                reloadList = false;
            }
            guiSlotVersion.drawScreen(mouseX, mouseY);
        }

        if (guiSlotVersion.getSize() < 1 && versionsWereLoaded) {
            Font.minecraftFont.drawCenteredStringWithShadow("No versions found.", getWidth() / 2, getHeight() / 2, 0x808080);
        }

        Font.minecraftFont.drawCenteredStringWithShadow("Select Version", getWidth() / 2, 20, 0xffffff);
        super.drawScreen(mouseX, mouseY);

        if (mouseX >= getWidth() / 2 + 54 && mouseY >= 12 && mouseX <= getWidth() / 2 + 74 && mouseY <= 30)
        {
            setTooltip("Classic / RubyDung");
        }

        if (mouseX >= getWidth() / 2 + 74 && mouseY >= 12 && mouseX <= getWidth() / 2 + 94 && mouseY <= 30)
        {
            setTooltip("Indev / Infdev");
        }

        if (mouseX >= getWidth() / 2 + 94 && mouseY >= 12 && mouseX <= getWidth() / 2 + 114 && mouseY <= 30)
        {
            setTooltip("Alpha");
        }

        if (mouseX >= getWidth() / 2 + 114 && mouseY >= 12 && mouseX <= getWidth() / 2 + 134 && mouseY <= 30)
        {
            setTooltip("Beta");
        }

        if (mouseX >= getWidth() / 2 + 135 && mouseY >= 12 && mouseX <= getWidth() / 2 + 154 && mouseY <= 30)
        {
            setTooltip("Other");
        }

        if(tooltip != null)
        {
            Renderer.singleton.renderTooltip(tooltip, mouseX, mouseY);
        }
    }

    public String setTooltip(String s)
    {
        return tooltip = s;
    }

    private AbstractGuiScreen parentScreen;
    private GuiSlotVersion guiSlotVersion;
    private String tooltip;
    private Predicate<GuiSlotVersion.SelectableVersion> filter;
    private IVersionSelectListener onSelectListener;
    private GuiSlotVersion.ISelectableVersionCompare compare;
    JFileChooser fileChooser = new JFileChooser();

    GuiToggleButton classicButton;
    GuiToggleButton indevButton;
    GuiToggleButton alphaButton;
    GuiToggleButton betaButton;
    GuiToggleButton releaseButton;
    GuiButton playButton;
    static DropTarget dropTarget = new DropTarget();

}
