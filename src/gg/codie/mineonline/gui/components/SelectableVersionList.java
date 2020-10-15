package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.MinecraftVersion;
import gg.codie.mineonline.MinecraftVersionRepository;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SelectableVersionList extends GUIObject {

    GUIObject scrollBarBackground;
    GUIObject scrollBar;
    GUIObject background;

    GUIText emptyText;

    IOnClickListener doubleClickListener;
    DropTargetAdapter dropTargetAdapter;

    static DropTarget dropTarget = new DropTarget();

    // Queue of jars to add if jars were selected from a non opengl thread (drag n drop).
    LinkedList<String[]> jarsToAdd = new LinkedList<>();

    public SelectableVersionList(String name, Vector3f localPosition, Vector3f rotation, Vector3f scale, IOnClickListener doubleClickListener) {
        super(name, localPosition, rotation, scale);

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

                                if(minecraftVersion != null) {
                                    jarsToAdd.add(new String[] { minecraftVersion.name, file.getPath(), minecraftVersion.info });
                                } else {
                                    jarsToAdd.add(new String[] { "Unknown Version", file.getPath(), null });
                                }

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

        float viewportHeight = DisplayManager.getDefaultHeight() - (69 * 2);
//        float contentHeight = 72 * (getGUIChildren().size());

        this.doubleClickListener = doubleClickListener;

        RawModel scrollBackgroundModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), viewportHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1)));
        ModelTexture scrollBackgroundTexture = new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")));
        TexturedModel texuredScrollBackgroundModel =  new TexturedModel(scrollBackgroundModel, scrollBackgroundTexture);
        scrollBarBackground = new GUIObject("scroll", texuredScrollBackgroundModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

//        System.out.println(viewportHeight);
//        System.out.println(contentHeight);
//        System.out.println(viewportHeight / contentHeight);

        RawModel scrollModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), 0), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(1, 129), new Vector2f(1, 1)));
        ModelTexture scrollTexture = new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")));
        TexturedModel texuredScrollModel =  new TexturedModel(scrollModel, scrollTexture);
        scrollBar = new GUIObject("scroll", texuredScrollModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        RawModel backgroundModel = Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1)));
        ModelTexture backgroundTexture = new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")));
        TexturedModel texuredBackgroundModel =  new TexturedModel(backgroundModel, backgroundTexture);
        background = new GUIObject("background", texuredBackgroundModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        emptyText = new GUIText("Browse to find a minecraft jar, or drag and drop jars onto the window!", 1.5f, TextMaster.minecraftFont, new Vector2f(200, (DisplayManager.getDefaultHeight() / 2) - 32 ), DisplayManager.getDefaultWidth() - 400, true, true);
        emptyText.setMaxLines(0);
        emptyText.setColour(0.5f, 0.5f, 0.5f);

        List<String> jarPaths = MinecraftVersionRepository.getSingleton().getInstalledJars().keySet().stream().sorted((String jarPath1, String jarPath2) -> {
            MinecraftVersion version1 = MinecraftVersionRepository.getSingleton().getInstalledJars().get(jarPath1);
            MinecraftVersion version2 = MinecraftVersionRepository.getSingleton().getInstalledJars().get(jarPath2);

            String versionName1 = version1 != null ? version1.name : "Unknown Version";
            String versionName2 = version2 != null ? version2.name : "Unknown Version";

            return versionName1.compareTo(versionName2);
        }).collect(Collectors.toList());

        for (String path : jarPaths) {
            File file = new File(path);

            MinecraftVersion minecraftVersion = MinecraftVersionRepository.getSingleton().getInstalledJars().get(path);

//            try {
//                if (!MinecraftVersion.isPlayableJar(file.getPath())) {
//                    continue;
//                }
//            } catch (IOException ex) {
//                continue;
//            }

            if(minecraftVersion != null) {
                if(!minecraftVersion.type.equals("client"))
                    continue;
                addVersion(minecraftVersion.name, file.getPath(), minecraftVersion.info);
            } else {
                addVersion("Unknown Version", file.getPath(), null);
            }
        }

        String selectedJar = MinecraftVersionRepository.getSingleton().getLastSelectedJarPath();
        if (selectedJar != null && !selectedJar.isEmpty()) {
            selectVersion(selectedJar);
        }
    }

    public void addVersion(String name, String path, String info) {

        if(emptyText != null) {
            emptyText.remove();
            emptyText = null;
        }

        int buffer = (72) * getVersions().size();

        SelectableVersion selectableVersion = new SelectableVersion(path, new Vector2f((DisplayManager.getDefaultWidth() / 2) - 220, 140 + buffer), name, path, info, this, new IOnClickListener() {
            @Override
            public void onClick() {
                if (doubleClickListener != null)
                    doubleClickListener.onClick();
            }
        });

        addChild(selectableVersion);

        // y tho
        resize();
    }

    @Override
    public void addChild(GameObject child) {
        super.addChild(child);
        if(child.getClass() == SelectableVersion.class) {
            versions.add((SelectableVersion)child);
        }
    }

    private LinkedList<SelectableVersion> versions = new LinkedList<>();

    public LinkedList<SelectableVersion> getVersions() {
        return versions;
    }

    // Where 0 is scrollbar at top, 1 is at bottom.
    float scrollbarPosition = 0;
    boolean holdingScroll = false;
    public void update() {
        for(SelectableVersion child : getVersions()) {
            child.update();
        }

        while(jarsToAdd.size() > 0) {
            String[] jarToAdd = jarsToAdd.pop();
            addVersion(jarToAdd[0], jarToAdd[1], jarToAdd[2]);
            if (jarsToAdd.size() == 0)
                selectVersion(jarToAdd[1]);
        }

        float viewportHeight = Display.getHeight() - DisplayManager.scaledHeight(138) - (DisplayManager.getYBuffer() * 2);
        float contentHeight = DisplayManager.scaledHeight(72) * (float)this.getVersions().size();

        // For some reason this doesn't scale right unless i do this.
//        double contentScale = (DisplayManager.getScale() / 2) + 0.5;
//        contentHeight = (float)((72 * this.getVersions().size()) * (1 / contentScale));

        scrollbarHeight = (viewportHeight / contentHeight) * viewportHeight;
        scrollableHeight = viewportHeight - scrollbarHeight;

        float viewportStartY = DisplayManager.getYBuffer() + DisplayManager.scaledHeight(69) + viewportHeight;
        float scrollBarYOffset = viewportStartY - (scrollableHeight * scrollbarPosition) - scrollbarHeight;


        int x = Mouse.getX();
        int y = Mouse.getY();


        boolean mouseIsOver =
                x - (Display.getWidth() / 2) - DisplayManager.scaledWidth(240) <= DisplayManager.scaledWidth(10)
                        && x - (Display.getWidth() / 2) - DisplayManager.scaledWidth(240) >= 0
                        && y - scrollBarYOffset <= scrollbarHeight
                        && y - scrollBarYOffset >= 0;

        if(mouseIsOver || holdingScroll) {
            //System.out.println("over!");
            if(Mouse.isButtonDown(0)) {
                if(!holdingScroll) {
                    holdingScroll = true;
                }

//                System.out.println(Mouse.getDY());
                //scrollBar.translate(new Vector3f(0, (Mouse.getDY() * scale) , 0));
                int dy = Mouse.getDY();

                if(dy != 0) {
                    //System.out.println(dy / scrollableHeight);
                    scrollbarPosition -= dy / scrollableHeight;

                    if (scrollbarPosition < 0) {
                        scrollbarPosition = 0;
                    } else if (scrollbarPosition > 1) {
                        scrollbarPosition = 1;
                    }

                    scroll();

                    // This is how much the content items have been scrolled up in pixels.
                    // scrollbarPosition (eg 0.9) multiplied by the height content which is offscreen.
                    float contentOffsetPx = (contentHeight - viewportHeight) * scrollbarPosition;
//                    System.out.println(contentHeight);
//                    System.out.println(contentOffsetPx);


                    for (int i = 0; i < getVersions().size(); i++) {
                        getVersions().get(i).scroll(
                                -(contentOffsetPx - 1)
                        );
                        //System.out.println(-((float)((i * 72) + contentOffsetPx) / DisplayManager.getDefaultHeight()));
                    }
                }
            } else if(holdingScroll) {
                holdingScroll = false;
            }
        } else {
            if (holdingScroll) {
                holdingScroll = false;
            }

            float scrollWheel = (float)(Mouse.getDWheel())/10;

            if(scrollWheel != 0) {
                //System.out.println(dy / scrollableHeight);
                scrollbarPosition -= scrollWheel / scrollableHeight;

                if (scrollbarPosition < 0) {
                    scrollbarPosition = 0;
                } else if (scrollbarPosition > 1) {
                    scrollbarPosition = 1;
                }

                scroll();

                // This is how much the content items have been scrolled up in pixels.
                // scrollbarPosition (eg 0.9) multiplied by the height content which is offscreen.
                float contentOffsetPx = (contentHeight - viewportHeight) * scrollbarPosition;
//                    System.out.println(contentHeight);
//                    System.out.println(contentOffsetPx);

                LinkedList<SelectableVersion> children = getVersions();
                for (int i = 0; i < children.size(); i++) {
                    children.get(i).scroll(
                            -(contentOffsetPx - 1)
                    );
                    //System.out.println(-((float)((i * 72) + contentOffsetPx) / DisplayManager.getDefaultHeight()));
                }
            }
        }
    }

    private float scrollbarHeight = 0;
    private float scrollableHeight = 0;
    public void resize() {
//        for(SelectableVersion child : getVersions()) {
//            child.resize();
//        }

        background.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));

        float viewportHeight = DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - 138);
        float contentHeight = DisplayManager.scaledHeight(72 * this.getVersions().size());

        scrollbarHeight = (viewportHeight / contentHeight) * viewportHeight;
        scrollableHeight = viewportHeight - scrollbarHeight;

        float viewportStartY = DisplayManager.getYBuffer() + DisplayManager.scaledHeight(69) + viewportHeight;
        float scrollBarYOffset = viewportStartY - (scrollableHeight * scrollbarPosition) - scrollbarHeight;

//        System.out.println(viewportHeight);
//        System.out.println(contentHeight);
//        System.out.println(viewportHeight / contentHeight);

        scrollBar.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), scrollBarYOffset), new Vector2f(DisplayManager.scaledWidth(10), scrollbarHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(1, 129), new Vector2f(1, 1))));
        scrollBarBackground.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), viewportHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));
        //        scrollBar.model.setRawModel();
    }

    public void scroll() {
        background.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));

        float viewportHeight = DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - 138);
        float contentHeight = DisplayManager.scaledHeight(72 * this.getVersions().size());

        scrollbarHeight = (viewportHeight / contentHeight) * viewportHeight;
        scrollableHeight = viewportHeight - scrollbarHeight;

        float viewportStartY = DisplayManager.getYBuffer() + DisplayManager.scaledHeight(69) + viewportHeight;
        float scrollBarYOffset = viewportStartY - (scrollableHeight * scrollbarPosition) - scrollbarHeight;

//        System.out.println(viewportHeight);
//        System.out.println(contentHeight);
//        System.out.println(viewportHeight / contentHeight);

        scrollBar.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), scrollBarYOffset), new Vector2f(DisplayManager.scaledWidth(10), scrollbarHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(1, 129), new Vector2f(1, 1))));
        scrollBarBackground.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), viewportHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));
        //        scrollBar.model.setRawModel();
    }

    public void render(Renderer renderer, GUIShader shader) {
        renderer.renderGUI(background, shader);

        for(SelectableVersion child : getVersions()) {
            child.render(renderer, shader);
        }

        float viewportHeight = DisplayManager.getDefaultHeight() - (69 * 2);
        float contentHeight = DisplayManager.scaledHeight(72 * this.getVersions().size());

        if(contentHeight > viewportHeight) {
            renderer.renderGUI(scrollBarBackground, shader);
            renderer.renderGUI(scrollBar, shader);
        }
    }

    public void selectVersion(String path) {
        System.out.println(path);
        for(SelectableVersion child : getVersions()) {
            if (child.path.equals(path)) {
                child.focused = true;
            } else {
                child.focused = false;
            }
        }
    }

    public String getSelected() {
        for(SelectableVersion child : getVersions()) {
            if(child.focused) {
                return child.path;
            }
        }
        return null;
    }

    public void cleanUp() {
        for(SelectableVersion version : getVersions()) {
            version.cleanUp();
        }

        if(emptyText != null)
            emptyText.remove();

        dropTarget.removeDropTargetListener(dropTargetAdapter);
    }
}
