package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.MinecraftVersionInfo;
import gg.codie.mineonline.Properties;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.Renderer;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.utils.JSONUtils;
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
                                MinecraftVersionInfo.MinecraftVersion minecraftVersion = MinecraftVersionInfo.getVersion(file.getPath());

                                try {
                                    if (!MinecraftVersionInfo.isRunnableJar(file.getPath())) {
                                        continue;
                                    }
                                } catch (IOException ex) {
                                    continue;
                                }

                                String[] existingJars = Properties.properties.has("minecraftJars") ? JSONUtils.getStringArray(Properties.properties.getJSONArray("minecraftJars")) : new String[0];
                                String[] newJars = new String[existingJars.length + 1];

                                for (int i = 0; i < existingJars.length; i++) {
                                    if(existingJars[i].equals(file.getPath())) {
                                        selectVersion(file.getPath());
                                        return;
                                    } else {
                                        newJars[i] = existingJars[i];
                                    }
                                }
                                newJars[newJars.length - 1] = file.getPath();

                                Properties.properties.put("minecraftJars", newJars);
                                Properties.saveProperties();

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

        Properties.loadProperties();
        String[] minecraftJars = Properties.properties.has("minecraftJars") ? JSONUtils.getStringArray(Properties.properties.getJSONArray("minecraftJars")) : new String[0];
        for (String path : minecraftJars) {
            File file = new File(path);

            MinecraftVersionInfo.MinecraftVersion minecraftVersion = MinecraftVersionInfo.getVersion(path);

            try {
                if (!MinecraftVersionInfo.isRunnableJar(file.getPath())) {
                    continue;
                }
            } catch (IOException ex) {
                continue;
            }

            if(minecraftVersion != null) {
                addVersion(minecraftVersion.name, file.getPath(), minecraftVersion.info);
            } else {
                addVersion("Unknown Version", file.getPath(), null);
            }
        }

        String selectedJar = Properties.properties.has("selectedJar") ? Properties.properties.getString("selectedJar") : null;
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
        super.addChild(
                new SelectableVersion(path, new Vector2f((DisplayManager.getDefaultWidth() / 2) - 220, 140 + buffer), name, path, info, this, new IOnClickListener() {
                    @Override
                    public void onClick() {
                        if (doubleClickListener != null)
                            doubleClickListener.onClick();
                    }
                })
        );

        resize();
    }

    public LinkedList<SelectableVersion> getVersions() {
        LinkedList<SelectableVersion> guiObjects = new LinkedList<>();
        for(Object obj: super.getChildren()) {
            guiObjects.add((SelectableVersion)obj);
        }
        return guiObjects;
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

                    resize();

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

                resize();

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


        //float scroll = (float)(-Mouse.getDWheel())/1000;
//        float viewportHeight = DisplayManager.getDefaultHeight() - (69 * 2);
//        float contentHeight = 72 * (getGUIChildren().size());
//
//        if(contentHeight <= viewportHeight) {
//            return;
//        }
//
////        if (scroll < 0) {
////            if (position - scroll > 0) {
////                scroll = position;
////            }
////            position -= scroll;
////        }
////
//        float maxPosition = -((contentHeight) / viewportHeight);
////        //float maxPosition = -2.3999996f;
////
////        if (scroll > 0) {
////            if (position - scroll < maxPosition){
////                scroll = -(maxPosition - position);
////                position = maxPosition;
////            } else {
////                position -= scroll;
////            }
//////            System.out.println(-(((contentHeight - 72) - viewportHeight) / viewportHeight));
//////            System.out.println(maxPosition);
//////            System.out.println(position);
//////            System.out.println(scroll);
////        }
////
//        for(SelectableVersion child : getVersions()) {
//            child.update();
////            child.translate(new Vector3f(0f, scroll, 0f));;
//        }
////
////        scrollBar.setLocalPosition(new Vector3f(0, -DisplayManager.scaledHeight(((position * (viewportHeight - (viewportHeight / contentHeight) * viewportHeight)) / maxPosition) / DisplayManager.getDefaultHeight()) * 2, 0));
////
////        double scale = 1 - (DisplayManager.getScale() - 1);
//
//        int x = Mouse.getX();
//        int y = Mouse.getY();
//
//        ///System.out.println(position);
//
//        Vector2f scrollBarStart = new Vector2f(
//                DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(),
//                69 + (viewportHeight + (position / 2 * viewportHeight)) - ((viewportHeight / contentHeight) * viewportHeight)
//        );
//        //System.out.println(240 + (position / 2 * viewportHeight));
//
////        System.out.println(viewportHeight);
////        System.out.println(contentHeight);
//        Vector2f scrollBarSize = new Vector2f(DisplayManager.scaledWidth(10), (viewportHeight / contentHeight) * viewportHeight);
//
//        // Max Position: -2.7368422
//        // Bottom: -2.3999996
//        //System.out.println("Position: " + position);
////        System.out.println("Scrollbar Y: " + DisplayManager.scaledHeight((69 + ((position * (viewportHeight - (viewportHeight / contentHeight) * viewportHeight))) / maxPosition) / DisplayManager.getDefaultHeight()));
//
////        System.out.println(new Vector2f(x, y));
//        //System.out.println(scrollBarStart);
////        System.out.println(new Vector2f((scrollBarStart.x + DisplayManager.getXBuffer()), (scrollBarStart.y) + DisplayManager.getYBuffer()));
//        //System.out.println(scrollBarSize);
////        System.out.println(new Vector2f(x - (scrollBarStart.x + DisplayManager.getXBuffer()), (y - (DisplayManager.getDefaultHeight() - scrollBarStart.y) - DisplayManager.getYBuffer())));
//
//        //System.out.println(scrollBarStart);
////        System.out.println(contentHeight);
////        System.out.println(viewportHeight / contentHeight);
//
////        if(debug1 != null) {
////            debug1.remove();
////            debug2.remove();
////        }
////
////        debug1 = new GUIText("-", 1, TextMaster.minecraftFont, new Vector2f(scrollBarStart.x, scrollBarStart.y - DisplayManager.getYBuffer()), 300, false, true);
////        debug2 = new GUIText("-", 1, TextMaster.minecraftFont, new Vector2f(scrollBarStart.x, scrollBarStart.y - scrollBarSize.y - DisplayManager.getYBuffer()), 300, false, true);
//
//        boolean mouseIsOver =
//               x - scrollBarStart.x <= scrollBarSize.x
//            && x - scrollBarStart.x >= 0
//            && y - scrollBarStart.y <= scrollBarSize.y
//            && y - scrollBarStart.y >= 0;
//
//        if(mouseIsOver || holdingScroll) {
//            //System.out.println("over!");
//            if(Mouse.isButtonDown(0)) {
//                if(!holdingScroll) {
//                    holdingScroll = true;
//                }
//
////                System.out.println(Mouse.getDY());
//                //scrollBar.translate(new Vector3f(0, (Mouse.getDY() * scale) , 0));
//                int dy = Mouse.getDY();
//
//                if(dy != 0) {
//
////                scrollBar.translate(new Vector3f(0, DisplayManager.scaledHeight((float)(dy)) / Display.getHeight() * 2, 0));
//                    System.out.println(scrollBarStart.y );
//                    System.out.println(scrollBarStart.y / (float) Display.getHeight());
//                    System.out.println((scrollBarStart.y / (float) Display.getHeight()) + ((-dy * 2) / (float)Display.getHeight()));
//                    //scrollBar.translate(new Vector3f(0, DisplayManager.scaledHeight((float)(dy)) / Display.getHeight() * 2, 0));
//
//                    position += dy / (float)Display.getHeight() * 5;
//
//                    scrollBarStart = new Vector2f(
//                            DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(),
//                            (viewportHeight + (position / 2 * viewportHeight)) - ((viewportHeight / contentHeight) * viewportHeight) - (69 * 2)
//                    );
//
//                    scrollBar.setLocalPosition(new Vector3f(scrollBar.getLocalPosition().x, (scrollBarStart.y / (float) Display.getHeight()) , 0));
//
////
//                    if (scrollBar.getLocalPosition().y > 0) {
//                        scrollBar.setLocalPosition(new Vector3f(scrollBar.getLocalPosition().x, 0, 0));
//                        position = 0;
//                    }
//
////                System.out.println((viewportHeight - (viewportHeight / contentHeight) * viewportHeight)/ Display.getHeight());
////                System.out.println(scrollBar.getLocalPosition().y);
//
//                    if (scrollBar.getLocalPosition().y < -((viewportHeight - (viewportHeight / contentHeight) * viewportHeight) / Display.getHeight()) * 2) {
//                        position = -((viewportHeight - (viewportHeight / contentHeight) * viewportHeight) / Display.getHeight() * 2);
//                        scrollBarStart = new Vector2f(
//                                DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(),
//                                (viewportHeight + (position / 2 * viewportHeight)) - ((viewportHeight / contentHeight) * viewportHeight)
//                        );
//
//                        scrollBar.setLocalPosition(new Vector3f(scrollBar.getLocalPosition().x, (scrollBarStart.y / (float) Display.getHeight()) , 0));
//                    }
//
//                    LinkedList<SelectableVersion> children = getVersions();
//
//                    int scrollOffsetPx = (int) (((-scrollBar.getLocalPosition().y) * DisplayManager.getDefaultHeight()) / 2);
//                    float scrollOffset = scrollOffsetPx / (viewportHeight - (viewportHeight / contentHeight) * viewportHeight);
//                    int contentOffsetPx = (int) (contentHeight * scrollOffset);
//                    float contentOffset = (((float) contentOffsetPx / DisplayManager.getDefaultHeight()) / 2) * maxPosition;
////                    position = contentOffset;
//
//                    //System.out.println(contentOffsetPx);
//
//
//                    //System.out.println(scrollOffset);
//
//                    for (int i = 0; i < children.size(); i++) {
//                        children.get(i).scroll(
//                                (float) -(contentOffsetPx / 2) + 1
//                        );
//                        //System.out.println(-((float)((i * 72) + contentOffsetPx) / DisplayManager.getDefaultHeight()));
//                    }
//                }
//                //System.out.println(DisplayManager.scaledHeight((float)(dy)) / Display.getHeight() * 2);
//            } else if(holdingScroll) {
//                holdingScroll = false;
//            }
//        } else if(holdingScroll) {
//            holdingScroll = false;
//        }

//        if(scroll != 0) {
//            System.out.println(scroll);
//            System.out.println(-(position - scroll) * DisplayManager.getDefaultHeight());
//            System.out.println(72 * getVersions().size());
//        }
    }

    private float scrollbarHeight = 0;
    private float scrollableHeight = 0;
    public void resize() {
        for(SelectableVersion child : getVersions()) {
            child.resize();
        }

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
