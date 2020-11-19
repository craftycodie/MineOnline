package gg.codie.mineonline.gui.screens;

import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.client.LegacyGameManager;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.components.GuiButton;
import gg.codie.mineonline.gui.components.GuiToggleButton;
import gg.codie.mineonline.gui.rendering.FontRenderer;
import org.lwjgl.input.Keyboard;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GuiVersions extends AbstractGuiScreen
{
    public interface IVersionSelectListener {
        void onSelect(String path);
    }

    public GuiVersions(AbstractGuiScreen guiscreen, Predicate<GuiSlotVersion.SelectableVersion> filter, IVersionSelectListener onSelect, GuiSlotVersion.ISelectableVersionCompare compare, boolean autoSelectSingleJar)
    {
        tooltip = null;
        parentScreen = guiscreen;
        this.filter = filter;
        this.onSelectListener = onSelect;
        this.compare = compare;

        initGui();

        List<GuiSlotVersion.SelectableVersion> filteredVersions = filteredVersions();

        // If there's only one jar in the list and the list can be skipped, skip.
        if (filteredVersions().size() == 1 && autoSelectSingleJar) {
            if (LegacyGameManager.isInGame())
                LegacyGameManager.setGUIScreen(parentScreen);
            else
                MenuManager.setMenuScreen(parentScreen);

            this.onSelectListener.onSelect(guiSlotVersion.getSelectedPath());
        }
    }

    public void updateScreen()
    {
    }

    private List<GuiSlotVersion.SelectableVersion> filteredVersions() {
        LinkedList<GuiSlotVersion.SelectableVersion> versions = new LinkedList<>();

        // Add installed jars to the list.
        MinecraftVersionRepository.getSingleton().getInstalledJars().forEach((String path, MinecraftVersion version) -> {
            for (GuiSlotVersion.SelectableVersion knownVersion : versions) {
                // If we already have a jar of the same version downloaded, skip.
                if (knownVersion.version == version && knownVersion.path != null)
                    return;

                // If we have a jar of the same version but it isn't downloaded, and this one is, remove the old one.
                if (knownVersion.version == version && path != null)
                    versions.remove(knownVersion);
            }
            versions.add(new GuiSlotVersion.SelectableVersion(version, path));
        });
        // Add downloadable jars to the list.
        MinecraftVersionRepository.getSingleton().getDownloadableClients().forEach((MinecraftVersion version) -> {
            for (GuiSlotVersion.SelectableVersion selectableVersion : versions) {
                if (selectableVersion.version == version)
                    return;
            }

            versions.add(new GuiSlotVersion.SelectableVersion(version, null));
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
                else if (releaseButton.enabled)
                    filteredVersions.add(selectableVersion);
            }
            else filteredVersions.add(selectableVersion);
        }

        if (filter != null)
            filteredVersions = filteredVersions.stream().filter(filter).collect(Collectors.toList());

        return filteredVersions;
    }

    public void initGui()
    {

        Keyboard.enableRepeatEvents(true);
        controlList.clear();
        func_35337_c();
        guiSlotVersion = new GuiSlotVersion(this, filteredVersions(), compare);
    }

    // Keep track of the selection inbetween filtering.
    GuiSlotVersion.SelectableVersion lastSelected = null;
    public void func_35337_c()
    {
        controlList.add(new GuiButton(1, getWidth() / 2 - 154, getHeight() - 48, 100, 20, "Back", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
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
                        return ((selectableVersion.version != null && lastSelected.version != null && selectableVersion.version.downloadURL != null && lastSelected.version.downloadURL != null) && selectableVersion.version.downloadURL.equals(lastSelected.version.downloadURL)) || lastSelected.path.equals(selectableVersion.path);
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
                        return ((selectableVersion.version != null && lastSelected.version != null && selectableVersion.version.downloadURL != null && lastSelected.version.downloadURL != null) && selectableVersion.version.downloadURL.equals(lastSelected.version.downloadURL)) || lastSelected.path.equals(selectableVersion.path);
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
                        return ((selectableVersion.version != null && lastSelected.version != null && selectableVersion.version.downloadURL != null && lastSelected.version.downloadURL != null) && selectableVersion.version.downloadURL.equals(lastSelected.version.downloadURL)) || lastSelected.path.equals(selectableVersion.path);
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
                        return ((selectableVersion.version != null && lastSelected.version != null && selectableVersion.version.downloadURL != null && lastSelected.version.downloadURL != null) && selectableVersion.version.downloadURL.equals(lastSelected.version.downloadURL)) || lastSelected.path.equals(selectableVersion.path);
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
                        return ((selectableVersion.version != null && lastSelected.version != null && selectableVersion.version.downloadURL != null && lastSelected.version.downloadURL != null) && selectableVersion.version.downloadURL.equals(lastSelected.version.downloadURL)) || lastSelected.path.equals(selectableVersion.path);
                    }
                };
                guiSlotVersion = new GuiSlotVersion(thisScreen, filteredVersions(), compare);
            }
        }));
        releaseButton.enabled = false;
        controlList.add(new GuiButton(3, getWidth() / 2 + 4 + 50, getHeight() - 48, 100, 20, "Play", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
                thisScreen.onSelectListener.onSelect(guiSlotVersion.getSelectedPath());
            }
        }));
        controlList.add(new GuiButton(5, (getWidth() / 2) - 50, getHeight() - 48, 100, 20, "Browse", new GuiButton.GuiButtonListener() {
            @Override
            public void OnButtonPress() {
            }
        }));
    }

    public void resizeGui() {
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

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void keyTyped(char c, int i)
    {
        if(c == '\r')
        {
            actionPerformed((GuiButton)controlList.get(2));
        }
    }

    protected void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
    }

    public void drawScreen(int i, int j)
    {
        resizeGui();

        tooltip = null;
        drawDefaultBackground();
        guiSlotVersion.drawScreen(i, j);
        drawCenteredString("Select Version", getWidth() / 2, 20, 0xffffff);
        super.drawScreen(i, j);

        if (i >= getWidth() / 2 + 54 && j >= 12 && i <= getWidth() / 2 + 74 && j <= 30)
        {
            setTooltip("Classic / RubyDung");
        }

        if (i >= getWidth() / 2 + 74 && j >= 12 && i <= getWidth() / 2 + 94 && j <= 30)
        {
            setTooltip("Indev / Infdev");
        }

        if (i >= getWidth() / 2 + 94 && j >= 12 && i <= getWidth() / 2 + 114 && j <= 30)
        {
            setTooltip("Alpha");
        }

        if (i >= getWidth() / 2 + 114 && j >= 12 && i <= getWidth() / 2 + 134 && j <= 30)
        {
            setTooltip("Beta");
        }

        if (i >= getWidth() / 2 + 135 && j >= 12 && i <= getWidth() / 2 + 154 && j <= 30)
        {
            setTooltip("Release / Other");
        }

        if(tooltip != null)
        {
            renderTooltip(tooltip, i, j);
        }
    }

    protected void renderTooltip(String s, int i, int j)
    {
        if(s == null)
        {
            return;
        } else
        {
            int k = i + 12;
            int l = j - 12;
            int i1 = FontRenderer.minecraftFontRenderer.getStringWidth(s);
            drawGradientRect(k - 3, l - 3, k + i1 + 3, l + 8 + 3, 0xc0000000, 0xc0000000);
            FontRenderer.minecraftFontRenderer.drawStringWithShadow(s, k, l, -1);
            return;
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

    GuiToggleButton classicButton;
    GuiToggleButton indevButton;
    GuiToggleButton alphaButton;
    GuiToggleButton betaButton;
    GuiToggleButton releaseButton;
}
