package gg.codie.mineonline.gui.rendering.components;

import gg.codie.mineonline.gui.rendering.*;
import gg.codie.mineonline.gui.rendering.models.RawModel;
import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import gg.codie.mineonline.gui.rendering.textures.ModelTexture;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.LinkedList;

public class SelectableVersionList extends GUIObject {

    GUIObject scrollBarBackground;
    GUIObject scrollBar;
    GUIObject background;

    public SelectableVersionList(String name, Vector3f localPosition, Vector3f rotation, Vector3f scale) {
        super(name, localPosition, rotation, scale);

        float viewportHeight = DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - 138);

        RawModel scrollBackgroundModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), viewportHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1)));
        ModelTexture scrollBackgroundTexture = new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")));
        TexturedModel texuredScrollBackgroundModel =  new TexturedModel(scrollBackgroundModel, scrollBackgroundTexture);
        scrollBarBackground = new GUIObject("scroll", texuredScrollBackgroundModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        RawModel scrollModel = Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240), DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(DisplayManager.scaledWidth(10), viewportHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(1, 129), new Vector2f(1, 1)));
        ModelTexture scrollTexture = new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")));
        TexturedModel texuredScrollModel =  new TexturedModel(scrollModel, scrollTexture);
        scrollBar = new GUIObject("scroll", texuredScrollModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));

        RawModel backgroundModel = Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1)));
        ModelTexture backgroundTexture = new ModelTexture(Loader.singleton.loadTexture(PlayerRendererTest.class.getResource("/img/gui.png")));
        TexturedModel texuredBackgroundModel =  new TexturedModel(backgroundModel, backgroundTexture);
        background = new GUIObject("background", texuredBackgroundModel, new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));
    }

    public void addVersion(String name, String path, String info) {
        int buffer = (72) * getVersions().size();
        super.addChild(
            new SelectableVersion(path, new Vector2f((DisplayManager.getDefaultWidth() / 2) - 220, DisplayManager.scaledHeight(140 + buffer)), name, path, info,this)
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

    float position = 0;
    public void update() {
        float scroll = (float)(-Mouse.getDWheel())/1000;


        if (scroll < 0 && position - scroll > 0) {
            scroll = position;
        }

        if (scroll > 0 && (-(position - scroll) * DisplayManager.getDefaultHeight()) - 140 >= (72 * getVersions().size())) {
            return;
        }

        position -= scroll;

        for(SelectableVersion child : getVersions()) {
            child.update();
            child.translate(new Vector3f(0f, scroll, 0f));;
        }

//        if(scroll != 0) {
//            System.out.println(scroll);
//            System.out.println(-(position - scroll) * DisplayManager.getDefaultHeight());
//            System.out.println(72 * getVersions().size());
//        }
    }

    public void resize() {
        for(SelectableVersion child : getVersions()) {
            child.resize();
        }

        background.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(0, DisplayManager.scaledHeight(69) + DisplayManager.getYBuffer()), new Vector2f(Display.getWidth(), DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - (69 * 2))), TextureHelper.getYFlippedPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(0, 129), new Vector2f(1, 1))));

        float viewportHeight = DisplayManager.scaledHeight(DisplayManager.getDefaultHeight() - 138);
        float contentHeight = 72 * this.getVersions().size();
        float scrollBarY = DisplayManager.scaledHeight((DisplayManager.getDefaultHeight() - 69) + contentHeight * position) + DisplayManager.getYBuffer();
        scrollBar.model.setRawModel(Loader.singleton.loadGUIToVAO(new Vector2f(DisplayManager.scaledWidth((DisplayManager.getDefaultWidth() / 2) + 240), scrollBarY), new Vector2f(DisplayManager.scaledWidth(10), -(viewportHeight / contentHeight) * viewportHeight), TextureHelper.getPlaneTextureCoords(new Vector2f(512, 512), new Vector2f(1, 129), new Vector2f(1, 1))));
//        scrollBar.model.setRawModel();
    }

    public void render(Renderer renderer, GUIShader shader) {
        renderer.renderGUI(background, shader);

        for(SelectableVersion child : getVersions()) {
            child.render(renderer, shader);
        }

        renderer.renderGUI(scrollBarBackground, shader);
        renderer.renderGUI(scrollBar, shader);
    }

    public void selectVersion(String path) {
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
    }
}
