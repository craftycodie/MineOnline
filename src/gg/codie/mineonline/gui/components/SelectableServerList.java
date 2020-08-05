package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.MinecraftVersionInfo;
import gg.codie.mineonline.Settings;
import gg.codie.mineonline.Session;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.api.MinecraftAPI;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

public class SelectableServerList extends GUIObject {

    GUIObject scrollBarBackground;
    GUIObject scrollBar;
    GUIObject background;

    GUIText emptyText;

    IOnClickListener doubleClickListener;

    public SelectableServerList(String name, Vector3f localPosition, Vector3f rotation, Vector3f scale, IOnClickListener doubleClickListener) {
        super(name, localPosition, rotation, scale);

        float viewportHeight = DisplayManager.getDefaultHeight() - (69 * 2);
//        float contentHeight = 72 * (getGUIChildren().size());

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

        emptyText = new GUIText("Can't find any servers :(", 1.5f, TextMaster.minecraftFont, new Vector2f(200, (DisplayManager.getDefaultHeight() / 2) - 32 ), DisplayManager.getDefaultWidth() - 400, true, true);
        emptyText.setMaxLines(0);
        emptyText.setColour(0.5f, 0.5f, 0.5f);

        try {
            LinkedList<MineOnlineServer> servers = MinecraftAPI.listServers(Session.session.getUuid(), Session.session.getSessionToken());

            Settings.loadSettings();
            String[] minecraftJars = Settings.settings.has(Settings.MINECRAFT_JARS) ? JSONUtils.getStringArray(Settings.settings.getJSONArray(Settings.MINECRAFT_JARS)) : new String[0];
            LinkedList<MinecraftVersionInfo.MinecraftVersion> installedClients = new LinkedList<>();

            for (String path : minecraftJars) {
                File file = new File(path);

                MinecraftVersionInfo.MinecraftVersion minecraftVersion = MinecraftVersionInfo.getVersion(path);

                try {
                    if (!MinecraftVersionInfo.isPlayableJar(file.getPath())) {
                        continue;
                    }
                } catch (IOException ex) {
                    continue;
                }

                if(minecraftVersion != null)
                    installedClients.add(minecraftVersion);
            }

            for(MineOnlineServer server : servers) {
                MinecraftVersionInfo.MinecraftVersion version = MinecraftVersionInfo.getVersionByMD5(server.md5);
                String info2 = "Unknown Version";
                if(version != null) {
                    if(version.clientVersions.length > 0) {
                        info2 = Arrays.toString(version.clientVersions).replace("[", "").replace("]", "");
                    } else {
                        info2 = version.name;
                    }

                    boolean clientInstalled = false;

                    found:
                    for (String clientversion : version.clientVersions) {
                        for(MinecraftVersionInfo.MinecraftVersion installedClient : installedClients) {
                            if(installedClient.baseVersion.equals(clientversion)) {
                                clientInstalled = true;
                                break found;
                            }
                        }
                    }

                    if(!clientInstalled) {
                        info2 = info2 + " - Not Installed!";
                    }
                }

                if(server.isMineOnline)
                    addServer(server.name, "Players: " + server.users + "/" + server.maxUsers, info2, server);
                else
                    addServer(server.name, "Featured Server", info2, server);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void addServer(String name, String info1, String info2, MineOnlineServer server) {

        if(emptyText != null) {
            emptyText.remove();
            emptyText = null;
        }

        int buffer = (72) * getServers().size();
        SelectableServer selectableServer = new SelectableServer(name, new Vector2f((DisplayManager.getDefaultWidth() / 2) - 220, 140 + buffer), name, info1, info2, server.status, server, this, new IOnClickListener() {
            @Override
            public void onClick() {
                if (doubleClickListener != null)
                    doubleClickListener.onClick();
            }
        });
        super.addChild(selectableServer);
        selectableServers.add(selectableServer);

        resize();
    }

    LinkedList<SelectableServer> selectableServers = new LinkedList<>();
    public LinkedList<SelectableServer> getServers() {
        return selectableServers;
    }

    // Where 0 is scrollbar at top, 1 is at bottom.
    float scrollbarPosition = 0;
    boolean holdingScroll = false;
    public void update() {
        for(SelectableServer child : getServers()) {
            child.update();
        }

        float viewportHeight = Display.getHeight() - DisplayManager.scaledHeight(138) - (DisplayManager.getYBuffer() * 2);
        float contentHeight = DisplayManager.scaledHeight(72) * (float)this.getServers().size();

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

                    LinkedList<SelectableServer> children = getServers();
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
                scrollbarPosition -= scrollWheel / viewportHeight;

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

                LinkedList<SelectableServer> children = getServers();
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
        for(SelectableServer child : getServers()) {
            child.resize();
        }

        background.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));

        float viewportHeight = DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - 138);
        float contentHeight = DisplayManager.scaledHeight(72 * this.getServers().size());

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
        float contentHeight = DisplayManager.scaledHeight(72 * this.getServers().size());

        scrollbarHeight = (viewportHeight / contentHeight) * viewportHeight;
        scrollableHeight = viewportHeight - scrollbarHeight;

        float viewportStartY = DisplayManager.getYBuffer() + DisplayManager.scaledHeight(69) + viewportHeight;
        float scrollBarYOffset = viewportStartY - (scrollableHeight * scrollbarPosition) - scrollbarHeight;

        scrollBar.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), scrollBarYOffset), new Vector2f(DisplayManager.scaledWidth(10), scrollbarHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(1, 129), new Vector2f(1, 1))));
        scrollBarBackground.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), viewportHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));
    }

    public void render(Renderer renderer, GUIShader shader) {
        renderer.renderGUI(background, shader);

        for(SelectableServer child : getServers()) {
            child.render(renderer, shader);
        }

        float viewportHeight = DisplayManager.getDefaultHeight() - (69 * 2);
        float contentHeight = DisplayManager.scaledHeight(72 * this.getServers().size());

        if(contentHeight > viewportHeight) {
            renderer.renderGUI(scrollBarBackground, shader);
            renderer.renderGUI(scrollBar, shader);
        }
    }

    public void selectServer(MineOnlineServer server) {
        for(SelectableServer child : getServers()) {
            if (child.server == server) {
                child.focused = true;
            } else {
                child.focused = false;
            }
        }
    }

    public SelectableServer getSelected() {
        for(SelectableServer child : getServers()) {
            if(child.focused) {
                return child;
            }
        }
        return null;
    }

    public void cleanUp() {
        for(SelectableServer server : getServers()) {
            server.cleanUp();
        }

        if(emptyText != null)
            emptyText.remove();
    }
}
