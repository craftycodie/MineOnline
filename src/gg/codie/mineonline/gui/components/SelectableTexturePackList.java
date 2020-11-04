package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.Globals;
import gg.codie.mineonline.LauncherFiles;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.common.utils.FolderChangeListener;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SelectableTexturePackList extends GUIObject {

    GUIObject scrollBarBackground;
    GUIObject scrollBar;
    GUIObject background;

    IOnClickListener doubleClickListener;
    FolderChangeListener texturePacksChangedListener;

    boolean folderChanged;

    public SelectableTexturePackList(String name, Vector3f localPosition, Vector3f rotation, Vector3f scale, IOnClickListener doubleClickListener) {
        super(name, localPosition, rotation, scale);

        float viewportHeight = DisplayManager.getDefaultHeight() - (69 * 2);

        this.doubleClickListener = doubleClickListener;

        RawModel scrollBackgroundModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), viewportHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1)));
        ModelTexture scrollBackgroundTexture = new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")));
        TexturedModel texuredScrollBackgroundModel =  new TexturedModel(scrollBackgroundModel, scrollBackgroundTexture);
        scrollBarBackground = new GUIObject("scroll", texuredScrollBackgroundModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        RawModel scrollModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), 0), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(1, 129), new Vector2f(1, 1)));
        ModelTexture scrollTexture = new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")));
        TexturedModel texuredScrollModel =  new TexturedModel(scrollModel, scrollTexture);
        scrollBar = new GUIObject("scroll", texuredScrollModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        RawModel backgroundModel = Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1)));
        ModelTexture backgroundTexture = new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")));
        TexturedModel texuredBackgroundModel =  new TexturedModel(backgroundModel, backgroundTexture);
        background = new GUIObject("background", texuredBackgroundModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        resetList();

        texturePacksChangedListener = new FolderChangeListener(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH, new FolderChangeListener.FolderChangeEvent() {
            @Override
            public void onFolderChange() {
                // Let the render thread handle updating the list.
                folderChanged = true;
            }
        });

        new Thread(texturePacksChangedListener).start();
    }

    public void addTexturePack(String name, String info, int packIcon) {
        int buffer = (72) * getTexturePacks().size();

        SelectableTexturePack selectableTexturePack = new SelectableTexturePack(name, new Vector2f((DisplayManager.getDefaultWidth() / 2) - 220, 140 + buffer), name, info, this, packIcon, new IOnClickListener() {
            @Override
            public void onClick() {
                if (doubleClickListener != null)
                    doubleClickListener.onClick();
            }
        });

        addChild(selectableTexturePack);

        // y tho
        resize();
    }

    @Override
    public void addChild(GameObject child) {
        super.addChild(child);
        if(child.getClass() == SelectableTexturePack.class) {
            texturePacks.add((SelectableTexturePack) child);
        }
    }

    public void resetList() {
        for (SelectableTexturePack texturePack : texturePacks) {
            removeChild(texturePack);
            texturePack.cleanUp();
        }
        texturePacks.clear();

        addTexturePack("Default", "The default look of Minecraft", Loader.singleton.loadTexture(MenuManager.class.getResource("/img/pack.png")));

        File texturePacksFolder = new File(LauncherFiles.MINECRAFT_TEXTURE_PACKS_PATH);

        if (texturePacksFolder.exists()) {
            for (File texturePack : texturePacksFolder.listFiles()) {
                if (!texturePack.getName().endsWith(".zip"))
                    continue;

                String info = "";
                int packIcon = -1;

                try {
                    ZipFile texturePackZip = new ZipFile(texturePack.getPath());
                    ZipEntry infoFile = texturePackZip.getEntry("pack.txt");
                    if (infoFile != null) {
                        info = new BufferedReader(new InputStreamReader(texturePackZip.getInputStream(infoFile)))
                                .lines().collect(Collectors.joining("\n"));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    ZipFile texturePackZip = new ZipFile(texturePack.getPath());
                    ZipEntry packPng = texturePackZip.getEntry("pack.png");
                    if (packPng != null) {
                        packIcon = Loader.singleton.loadTexture(texturePackZip.getInputStream(packPng));
                    }
                } catch (Exception ex) {
                    System.err.println("Failed to load pack png for texture pack " + texturePack);
                    if (Globals.DEV)
                        ex.printStackTrace();
                }

                addTexturePack(texturePack.getName(), info, packIcon);
            }
        } else {
            try {
                texturePacksFolder.mkdirs();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        Settings.singleton.loadSettings();
        selectTexturePack(Settings.singleton.getTexturePack());
    }

    private LinkedList<SelectableTexturePack> texturePacks = new LinkedList<>();

    public LinkedList<SelectableTexturePack> getTexturePacks() {
        return texturePacks;
    }

    // Where 0 is scrollbar at top, 1 is at bottom.
    float scrollbarPosition = 0;
    boolean holdingScroll = false;
    public void update() {
        if (folderChanged) {
            resetList();
            folderChanged = false;
        }

        for(SelectableTexturePack child : getTexturePacks()) {
            child.update();
        }

        float viewportHeight = Display.getHeight() - DisplayManager.scaledHeight(138) - (DisplayManager.getYBuffer() * 2);
        float contentHeight = DisplayManager.scaledHeight(72) * (float)this.getTexturePacks().size();

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


                    for (int i = 0; i < getTexturePacks().size(); i++) {
                        getTexturePacks().get(i).scroll(
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

                LinkedList<SelectableTexturePack> children = getTexturePacks();
                for (int i = 0; i < children.size(); i++) {
                    children.get(i).scroll(
                            -(contentOffsetPx - 1)
                    );
                }
            }
        }
    }

    private float scrollbarHeight = 0;
    private float scrollableHeight = 0;
    public void resize() {
        background.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));

        float viewportHeight = DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - 138);
        float contentHeight = DisplayManager.scaledHeight(72 * this.getTexturePacks().size());

        scrollbarHeight = (viewportHeight / contentHeight) * viewportHeight;
        scrollableHeight = viewportHeight - scrollbarHeight;

        float viewportStartY = DisplayManager.getYBuffer() + DisplayManager.scaledHeight(69) + viewportHeight;
        float scrollBarYOffset = viewportStartY - (scrollableHeight * scrollbarPosition) - scrollbarHeight;

        scrollBar.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), scrollBarYOffset), new Vector2f(DisplayManager.scaledWidth(10), scrollbarHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(1, 129), new Vector2f(1, 1))));
        scrollBarBackground.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), viewportHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));
    }

    public void scroll() {
        background.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));

        float viewportHeight = DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - 138);
        float contentHeight = DisplayManager.scaledHeight(72 * this.getTexturePacks().size());

        scrollbarHeight = (viewportHeight / contentHeight) * viewportHeight;
        scrollableHeight = viewportHeight - scrollbarHeight;

        float viewportStartY = DisplayManager.getYBuffer() + DisplayManager.scaledHeight(69) + viewportHeight;
        float scrollBarYOffset = viewportStartY - (scrollableHeight * scrollbarPosition) - scrollbarHeight;

        scrollBar.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), scrollBarYOffset), new Vector2f(DisplayManager.scaledWidth(10), scrollbarHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(1, 129), new Vector2f(1, 1))));
        scrollBarBackground.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), viewportHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));
        //        scrollBar.model.setRawModel();
    }

    public void render(Renderer renderer, GUIShader shader) {
        renderer.renderGUI(background, shader);

        for(SelectableTexturePack child : getTexturePacks()) {
            child.render(renderer);
        }

        float viewportHeight = DisplayManager.getDefaultHeight() - (69 * 2);
        float contentHeight = DisplayManager.scaledHeight(72 * this.getTexturePacks().size());

        if(contentHeight > viewportHeight) {
            renderer.renderGUI(scrollBarBackground, shader);
            renderer.renderGUI(scrollBar, shader);
        }
    }

    public void selectTexturePack(String name) {
        System.out.println(name);
        for(SelectableTexturePack child : getTexturePacks()) {
            if (child.texturePackName.equals(name)) {
                child.focused = true;
            } else {
                child.focused = false;
            }
        }
    }

    public String getSelected() {
        for(SelectableTexturePack child : getTexturePacks()) {
            if(child.focused) {
                return child.texturePackName;
            }
        }
        return null;
    }

    public void cleanUp() {
        for(SelectableTexturePack texturePack : getTexturePacks()) {
            texturePack.cleanUp();
        }

        texturePacksChangedListener.stop();
    }
}
