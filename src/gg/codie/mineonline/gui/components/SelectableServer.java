package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.api.EMineOnlineServerStatus;
import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.gui.MenuManager;
import gg.codie.mineonline.gui.MouseHandler;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.shaders.SelectableVersionShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class SelectableServer extends GUIObject {

    Vector2f originalPosition;
    Vector2f currentPosition;

    String versionName;
    String info1;
    String info2;
    EMineOnlineServerStatus status;

    GUIText nameText;
    GUIText info1Text;
    GUIText info2Text;
    GUIText statusText;

    private SelectableServerList parent;
    private IOnClickListener doubleClickListener;

    public final MineOnlineServer server;

    public SelectableServer(String name, Vector2f position, String versionName, String info1, String info2, EMineOnlineServerStatus status, MineOnlineServer server, SelectableServerList parent, IOnClickListener doubleClickListener) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(440), DisplayManager.scaledHeight(72)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 130), new Vector2f(220, 36))), new ModelTexture(Loader.singleton.loadTexture(MenuManager.class.getResource("/img/gui.png")))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        this.originalPosition = new Vector2f(position.x, position.y);
        this.currentPosition = position;
        this.versionName = versionName;
        this.info1 = info1;
        this.parent = parent;
        this.info2 = info2;
        this.status = status;
        this.doubleClickListener = doubleClickListener;
        this.server = server;

        nameText = new GUIText(this.versionName, 1.5f, TextMaster.minecraftFont, new Vector2f(currentPosition.x + 8, currentPosition.y - 70), 440, false, true);
        nameText.setYBounds(new Vector2f(69 , 69));

        if (this.status != null && this.status != EMineOnlineServerStatus.NONE) {
            info1Text = new GUIText(this.info1 + " ", 1.5f, TextMaster.minecraftFont, new Vector2f(currentPosition.x + 8, currentPosition.y - 48), 440, false, true);
            info1Text.setColour(0.5F, 0.5F, 0.5F);
            info1Text.setYBounds(new Vector2f(69, 69));

            int info1Length = info1Text.getLineLength();
            statusText = new GUIText(this.status.toString(), 1.5f, TextMaster.minecraftFont, new Vector2f(currentPosition.x + 8 + info1Length, currentPosition.y - 48), 440 - info1Length, false, true);
            Vector3f statusColor = this.status.getColor();
            statusText.setColour(statusColor.x, statusColor.y, statusColor.z);
            statusText.setYBounds(new Vector2f(69, 69));
        } else {
            info1Text = new GUIText(this.info1, 1.5f, TextMaster.minecraftFont, new Vector2f(currentPosition.x + 8, currentPosition.y - 48), 440, false, true);
            info1Text.setColour(0.5F, 0.5F, 0.5F);
            info1Text.setYBounds(new Vector2f(69, 69));
        }

        if(this.info2 != null) {
            info2Text = new GUIText(this.info2, 1.5f, TextMaster.minecraftFont, new Vector2f(currentPosition.x + 8, currentPosition.y - 26), 440, false, true);
            info2Text.setColour(0.7F, 0.7F, 0.7F);
            info2Text.setYBounds(new Vector2f(69, 69));
        }
    }

    public void render(Renderer renderer, GUIShader shader) {
        if(focused) {
            SelectableVersionShader.singleton.start();
            SelectableVersionShader.singleton.loadViewMatrix(Camera.singleton);
            renderer.renderGUI(this, SelectableVersionShader.singleton);
            SelectableVersionShader.singleton.stop();
        }
    }

    public void resize() {
        this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(currentPosition.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - currentPosition.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(440), DisplayManager.scaledHeight(72)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 130), new Vector2f(220, 36))));
    }

    boolean focused = false;
    long lastClickTime;
    boolean mouseWasOver = false;
    public void update() {
        int x = Mouse.getX();
        int y = Mouse.getY();

        float viewportHeight = Display.getHeight() - DisplayManager.scaledHeight(138) - (DisplayManager.getYBuffer() * 2);
        float viewportStartY = DisplayManager.getYBuffer() + DisplayManager.scaledHeight(69) + viewportHeight;
        
        boolean mouseIsOver =
                x - (DisplayManager.scaledWidth(currentPosition.x) + DisplayManager.getXBuffer()) <= DisplayManager.scaledWidth(440)
                        && x - (DisplayManager.scaledWidth(currentPosition.x) + DisplayManager.getXBuffer()) >= 0
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - currentPosition.y) - DisplayManager.getYBuffer() <= DisplayManager.scaledHeight(72)
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - currentPosition.y) - DisplayManager.getYBuffer() >= 0;

        if(y > viewportStartY || y < viewportStartY - viewportHeight) {
            mouseIsOver = false;
        }

        if (mouseIsOver && !mouseWasOver) {
            mouseWasOver = true;
        } else if(!mouseIsOver && mouseWasOver) {
            mouseWasOver = false;
        }

        if(MouseHandler.didClick() && mouseIsOver) {
            parent.selectServer(server);

            if(System.currentTimeMillis() - lastClickTime < 350 && doubleClickListener != null) {
                doubleClickListener.onClick();
            }

            lastClickTime = System.currentTimeMillis();
        }
    }

    public void scroll(float yOffset) {
        currentPosition = new Vector2f(originalPosition.x, originalPosition.y + (yOffset / (float)DisplayManager.getScale()));

        this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(currentPosition.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - currentPosition.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(440), DisplayManager.scaledHeight(72)), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 130), new Vector2f(220, 36))));

        nameText.setPosition(new Vector2f(currentPosition.x + 8, currentPosition.y - 70));

        info1Text.setPosition(new Vector2f(currentPosition.x + 8, currentPosition.y - 48));

        if(this.info2 != null) {
            info2Text.setPosition(new Vector2f(currentPosition.x + 8, currentPosition.y - 26));
        }

        if(this.statusText != null) {
            statusText.setPosition(new Vector2f(statusText.getPosition().x, currentPosition.y - 48));
        }
    }

    public void cleanUp() {
        nameText.remove();
        if(statusText != null)
            statusText.remove();
        info1Text.remove();
        if (info2Text != null)
            info2Text.remove();
    }
}
