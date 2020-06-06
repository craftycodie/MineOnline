package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.shaders.GUIShader;
import org.lwjgl.util.vector.Vector3f;

import java.util.LinkedList;

public class GUIObject extends GameObject {

    protected GUIObject() {}

    public LinkedList<GUIObject> getGUIChildren() {
        LinkedList<GUIObject> guiObjects = new LinkedList<>();
        for(Object obj: super.getChildren()) {
            guiObjects.add((GUIObject)obj);
        }
        return guiObjects;
    }

    public GUIObject(String name, TexturedModel texturedModel, Vector3f localPosition, Vector3f rotation, Vector3f scale) {
        super(name, texturedModel, localPosition, rotation, scale);
    }

    protected GUIObject(String name, Vector3f localPosition, Vector3f rotation, Vector3f scale) {
        super(name, localPosition, rotation, scale);
    }
}
