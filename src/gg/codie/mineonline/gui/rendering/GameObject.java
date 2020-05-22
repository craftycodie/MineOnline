package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.gui.rendering.models.TexturedModel;
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

    public Matrix4f GetModelMatrix() {
        Matrix4f matrix4f = new Matrix4f();
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix4f, matrix4f);
        Matrix4f.translate(localPosition, matrix4f, matrix4f);
        Matrix4f.rotate((float)Math.toRadians(localZRot), new Vector3f(0, 0, 1),
                matrix4f, matrix4f);
        Matrix4f.rotate((float)Math.toRadians(localYRot), new Vector3f(0, 1, 0),
                matrix4f, matrix4f);
        Matrix4f.rotate((float)Math.toRadians(localXRot), new Vector3f(1, 0, 0),
                matrix4f, matrix4f);

        return matrix4f;
    }

    public Vector3f getPosition() {
        if (parent == null)
            return localPosition;

        Matrix4f matrix;
        if (parent != null) {
            matrix = Matrix4f.mul(parent.GetModelMatrix(), GetModelMatrix(), null);
        } else {
            matrix = GetModelMatrix();
        }

        float x = matrix.m30;
        float y = matrix.m31;
        float z = matrix.m32;

        return new Vector3f(x, y, z);
    }

    public void setLocalPosition(Vector3f localPosition) {
        this.localPosition = localPosition;
    }

    public float getX() {
        return parent != null ? parent.getX() + localXRot : localXRot;
    }

    public float getLocalXRot() {
        return localXRot;
    }

    public void setLocalXRot(float localXRot) {
        this.localXRot = localXRot;
    }

    public float getY() {
        return parent != null ? parent.getY() + localYRot : localYRot;
    }

    public float getLocalYRot() {
        return localYRot;
    }

    public void setLocalYRot(float localYRot) {
        this.localYRot = localYRot;
    }

    public float getZ() {
        return parent != null ? parent.getZ() + localZRot : localZRot;
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
