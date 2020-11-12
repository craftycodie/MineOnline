package gg.codie.mineonline.gui.components;

import gg.codie.mineonline.gui.input.MouseHandler;
import gg.codie.mineonline.gui.events.IOnClickListener;
import gg.codie.mineonline.gui.font.GUIText;
import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.font.TextMaster;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.EGUITexture;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import gg.codie.mineonline.gui.sound.ClickSound;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class ValueSlider extends GUIObject {

    Vector2f position;
    IOnClickListener clickListener;
    GUIText guiText;
    ValueSliderBar sliderBar;

    int value;
    int min;
    int max;

    public ValueSlider(String name, Vector2f position, IOnClickListener clickListener, int value, int min, int max) {
        super(name,
                new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(300), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(300, 89), new Vector2f(150, 20))), new ModelTexture(Loader.singleton.getGuiTexture(EGUITexture.OLD_GUI))),
                new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
        );

        this.value = value;
        this.min = min;
        this.max = max;
        this.position = new Vector2f(position.x,  position.y);
        this.clickListener = clickListener;

        guiText = new GUIText(name, 1.5f, TextMaster.minecraftFont, new Vector2f(position.x, position.y - 32), 300f, true, true);

        float sliderX = position.x + ((float)(value - min) / (max - min)) * (DisplayManager.scaledWidth(300) - DisplayManager.scaledWidth(16));

        sliderBar = new ValueSliderBar("slider bar", new Vector2f(sliderX, position.y));
        this.addChild(sliderBar);
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

    public int getValue() {
        return value;
    }

    boolean mouseWasOver = false;
    public void update() {
        int x = Mouse.getX();
        int y = Mouse.getY();

        boolean mouseIsOver =
                x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer()) <= DisplayManager.scaledWidth(300)
                        && x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer()) >= 0
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) - DisplayManager.getYBuffer() <= DisplayManager.scaledHeight(40)
                        && y - DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) - DisplayManager.getYBuffer() >= 0;

        if (mouseIsOver && Mouse.isButtonDown(0)) {
            float sliderCenter = (x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer())) - DisplayManager.scaledWidth(8);

            sliderCenter = MathUtils.clamp(sliderCenter, DisplayManager.scaledWidth(0), DisplayManager.scaledHeight(300) - DisplayManager.scaledWidth(16));

            this.value = (int)(((x - (DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer())) / DisplayManager.scaledWidth(300)) * (max - min)) + min;

            sliderBar.setPosition(new Vector2f(position.x + sliderCenter, position.y));
            sliderBar.update();

            clickListener.onClick();
        }

        if (mouseIsOver && !mouseWasOver) {
            mouseWasOver = true;
        } else if(!mouseIsOver && mouseWasOver) {
            mouseWasOver = false;
        }

        if(MouseHandler.didLeftClick() && mouseIsOver && clickListener != null) {
            ClickSound.play();
        }
    }

    public void resize() {
        model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(300), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(300, 89), new Vector2f(150, 20))));
        sliderBar.resize();
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

    private static class ValueSliderBar extends GUIObject {
        Vector2f position;

        public ValueSliderBar(String name, Vector2f position) {
            super(name,
                    new TexturedModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(16), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(500, 0), new Vector2f(8, 20))), new ModelTexture(Loader.singleton.getGuiTexture(EGUITexture.OLD_GUI))),
                    new Vector3f(0, 0, 0), new Vector3f(), new Vector3f(1, 1, 1)
            );

            this.position = new Vector2f(position.x,  position.y);
        }

        public void setPosition(Vector2f position) {
            this.position = position;
        }

        public void update() {
            model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(16), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(500, 0), new Vector2f(8, 20))));
        }

        public void resize() {
            model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth(position.x) + DisplayManager.getXBuffer(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - position.y) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(16), DisplayManager.scaledHeight(40)), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(500, 0), new Vector2f(8, 20))));
        }
    }

}
