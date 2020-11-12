package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.gui.input.MouseHandler;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.mineonline.gui.sound.ClickSound;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class MediumButton extends GUIObject {

    Vector2f position;
    IOnClickListener clickListener;
    GUIText guiText;

    boolean disabled;

    public MediumButton(String name, Vector2f position, IOnClickListener clickListener) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(300), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 89), new Vector2f(150, 20))),new ModelTexture(Loader.singleton.getGuiTexture(EGUITexture.OLD_GUI))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        this.position = new Vector2f(position.x,  position.y);
        this.clickListener = clickListener;

        guiText = new GUIText(name, 1.5f, TextMaster.minecraftFont, new Vector2f(position.x, position.y - 32), 300f, true, true);
    }

    public void render(Renderer renderer, GUIShader shader) {
        shader.start();
        renderer.renderGUI(this, shader);
        shader.stop();

        if(mouseWasOver) {
            guiText.setColour(1, 1, 0.627f);
        } else {
            guiText.setColour(1,1,1);
        }

    }

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean value) {
        this.disabled = value;
        if(this.disabled) {
            this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(300), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(300, 89), new Vector2f(150, 20))));
        } else {
            this.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(300), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 89), new Vector2f(150, 20))));
        }
    }

    boolean mouseWasOver = false;
    public void update() {
        if (disabled)
            return;

        int x = Mouse.getX();
        int y = Mouse.getY();

        boolean mouseIsOver =
                x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer()) <= DisplayManager.scaledWidth(300)
                        && x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer()) >= 0
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) - DisplayManager.getYBuffer() <= DisplayManager.scaledHeight(40)
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) - DisplayManager.getYBuffer() >= 0;

        if (mouseIsOver && !mouseWasOver) {
            mouseWasOver = true;

            RawModel model = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(300), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(150, 89), new Vector2f(150, 20)));
            this.model.setRawModel(model);

        } else if(!mouseIsOver && mouseWasOver) {
            mouseWasOver = false;

            RawModel model = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(300), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 89), new Vector2f(150, 20)));
            this.model.setRawModel(model);
        }

        if(MouseHandler.didLeftClick() && mouseIsOver && clickListener != null) {
            ClickSound.play();
            clickListener.onClick();
        }
    }

    public void resize() {
        model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(300), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 89), new Vector2f(150, 20))));
        guiText.resize();
    }

    public void cleanUp() {
        guiText.remove();
    }

    @Override
    public void setName(String name) {
        guiText.remove();
        super.setName(name);
        guiText = new GUIText(name, 1.5f, TextMaster.minecraftFont, new Vector2f(position.x, position.y - 32), 300f, true, true);
    }

}
