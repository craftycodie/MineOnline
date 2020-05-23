package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.LinkedList;

public class GameObject {

    private TexturedModel model;

    protected Vector3f localPosition;

    protected float localXRot, localYRot, localZRot;
    protected float scale;

    private GameObject parent;

    public LinkedList<GameObject> getChildren() {
        return children;
    }

    private LinkedList<GameObject> children = new LinkedList<>();

    protected GameObject() {}

    public GameObject(TexturedModel texturedModel, Vector3f localPosition, float rotX, float rotY, float rotZ, float scale) {
        this.model = texturedModel;
        this.localPosition = localPosition;
        this.localXRot = rotX;
        this.localYRot = rotY;
        this.localZRot = rotZ;
        this.scale = scale;
    }

    public GameObject(TexturedModel texturedModel) {
        this(texturedModel, new Vector3f(0, 0, -5), 0, 0, 0, 1);
    }

    public void addChild(GameObject child) {
        children.add(child);
        child.parent = this;
    }


//    public void increasePosition(float dx, float dy, float dz) {
//        this.localPosition.x += dx;
//        this.localPosition.y += dy;
//        this.localPosition.z += dz;
//    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.localXRot += dx;
        this.localYRot += dy;
        this.localZRot += dz;
    }


    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        if (parent == null)
            return localPosition;


        Matrix4f matrix = new Matrix4f();
        matrix.translate(parent.getPosition());
        matrix.rotate((float) Math.toRadians(parent.getXRotation()), new Vector3f(1, 0, 0));
        matrix.rotate((float) Math.toRadians(parent.getYRotation()), new Vector3f(0, 1, 0));
        matrix.rotate((float) Math.toRadians(parent.getZRotation()), new Vector3f(0, 0, 1));
        matrix.translate(localPosition);
        return MathUtils.getPosition(matrix);
    }

    public void setLocalPosition(Vector3f localPosition) {
        this.localPosition = localPosition;
    }

    public float getXRotation() {
        return parent != null ? parent.getXRotation() + localXRot : localXRot;
    }

    public float getLocalXRot() {
        return localXRot;
    }

    public void setLocalXRot(float localXRot) {
        this.localXRot = localXRot;
    }

    public float getYRotation() {
        return parent != null ? parent.getYRotation() + localYRot : localYRot;
    }

    public float getLocalYRot() {
        return localYRot;
    }

    public void setLocalYRot(float localYRot) {
        this.localYRot = localYRot;
    }

    public float getZRotation() {
        return parent != null ? parent.getZRotation() + localZRot : localZRot;
    }

    public float getLocalZRot() {
        return localZRot;
    }

    public void setLocalZRot(float localZRot) {
        this.localZRot = localZRot;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }


}
