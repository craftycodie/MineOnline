package gg.codie.mineonline.gui.rendering;

import gg.codie.mineonline.gui.rendering.models.TexturedModel;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import java.util.LinkedList;

public class GameObject {

    public String name;
    public TexturedModel model;

    protected Matrix4f localMatrix;

    protected GameObject parent;

    public LinkedList<GameObject> getChildren() {
        return children;
    }

    private LinkedList<GameObject> children = new LinkedList<>();

    public GameObject(String name) {
        this(name, new Vector3f(0, 0, -5), new Vector3f(), new Vector3f(1, 1, 1));
    }

    protected GameObject() {
        name = "untitled";
    }

    public GameObject(String name, Vector3f localPosition, Vector3f rotation, Vector3f scale) {
        this.name = name;

        this.localMatrix = MathUtils.createTransformationMatrix(localPosition, rotation, scale);
    }

    public GameObject(String name, TexturedModel texturedModel, Vector3f localPosition, Vector3f rotation, Vector3f scale) {
        this(name, localPosition, rotation, scale);
        this.model = texturedModel;
    }

    public GameObject(String name, TexturedModel texturedModel) {
        this(name, texturedModel, new Vector3f(0, 0, -5), new Vector3f(), new Vector3f(1, 1, 1));
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

    public void increaseRotation(Vector3f rotation) {
//        Quaternion quaternion = new Quaternion();
//        quaternion = MathUtils.rotateY(rotation.y, quaternion);
//        quaternion = MathUtils.rotateZ(rotation.z, quaternion);
//        quaternion = MathUtils.rotateX(rotation.x, quaternion);
//        localMatrix = MathUtils.createTransformationMatrix(MathUtils.getPosition(localMatrix), quaternion, MathUtils.getScale(localMatrix));

        this.localMatrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        this.localMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
        this.localMatrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1));

    }

    public void setLocalRotation(Vector3f rotation) {
        // Kinda dirty solution but I couldn't get other methods to work properly.
        //increaseRotation(new Vector3f(rotation.x - getLocalRotation().x, rotation.y - getLocalRotation().y, rotation.z - getLocalRotation().z));
        localMatrix = MathUtils.createTransformationMatrix(MathUtils.getPosition(localMatrix), rotation, MathUtils.getScale(localMatrix));
    }




    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Matrix4f getTransformationMatrix() {
        if(parent != null) {
            return Matrix4f.mul(parent.getTransformationMatrix(), localMatrix, null);
        }

        return localMatrix;
    }

//    public Vector3f getPosition() {
//        if (parent == null)
//            return localPosition;
//
////        Matrix4f matrix = new Matrix4f();
////        matrix.translate(parent.getPosition());
////        matrix.rotate((float) Math.toRadians(parent.getRotation().x), new Vector3f(1, 0, 0));
////        matrix.rotate((float) Math.toRadians(parent.getRotation().y), new Vector3f(0, 1, 0));
////        matrix.rotate((float) Math.toRadians(parent.getRotation().z), new Vector3f(0, 0, 1));
////        matrix.translate(localPosition);
////        return MathUtils.getPosition(matrix);
//    }

    public void translate(Vector3f localPosition) {
        localMatrix = localMatrix.translate(localPosition);
    }

    // Buggy.
    public void setLocalPosition(Vector3f localPosition) {
        localMatrix = MathUtils.createTransformationMatrix(localPosition, MathUtils.getRotation(localMatrix), MathUtils.getScale(localMatrix));
    }

    public Vector3f getLocalPosition() {
        return MathUtils.getPosition(localMatrix);
    }
//
//    public Matrix4f GetModelMatrix() {
//        Matrix4f matrix4f = new Matrix4f();
//        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix4f, matrix4f);
//        Matrix4f.translate(localPosition, matrix4f, matrix4f);
//        Matrix4f.rotate((float)Math.toRadians(localZRot), new Vector3f(0, 0, 1),
//                matrix4f, matrix4f);
//        Matrix4f.rotate((float)Math.toRadians(localYRot), new Vector3f(0, 1, 0),
//                matrix4f, matrix4f);
//        Matrix4f.rotate((float)Math.toRadians(localXRot), new Vector3f(1, 0, 0),
//                matrix4f, matrix4f);
//
//        return matrix4f;
//    }

//    public Vector3f getRotation() {
//        if(parent == null) {
//            return new Vector3f(localXRot, localYRot, localZRot);
//        }
//
////        Vector3f rotation = parent.getRotation();
////
//        Vector3f up = parent.getUp();
//        Vector3f right = parent.getRight();
//        Vector3f forward = parent.getForward();
////
//        if(this.name.equals("leftarm")) {
//            System.out.println("left arm");
//            System.out.println("forward " + forward.x + ", " + forward.y + ", " + forward.z);
//            System.out.println("up " + up.x + ", " + up.y + ", " + up.z);
//            System.out.println("right " + right.x + ", " + right.y + ", " + right.z);
//        }
//
//        if(this.name.equals("rightarm")) {
//            System.out.println("right arm");
//            System.out.println("forward " + forward.x + ", " + forward.y + ", " + forward.z);
//            System.out.println("up " + up.x + ", " + up.y + ", " + up.z);
//            System.out.println("right " + right.x + ", " + right.y + ", " + right.z);
//        }
////
////        rotation = new Vector3f(rotation.x + (localXRot * right.x), rotation.y + (localXRot * right.y), rotation.z + (localXRot * right.z));
////        rotation = new Vector3f(rotation.x + (localYRot * up.x), rotation.y + (localYRot * up.y), rotation.z + (localYRot * up.z));
////        rotation = new Vector3f(rotation.x + (localZRot * forward.x), rotation.y + (localZRot * forward.y), rotation.z + (localZRot * forward.z));
////
////        return rotation;
//
////        Matrix4f parentMatrix = MathUtils.createTransformationMatrix(parent.getPosition(), parent.getRotation().x, parent.getRotation().y, parent.getRotation().z, parent.scale);
////
////        Matrix4f thisMatrix = MathUtils.createTransformationMatrix(localPosition, localXRot, localYRot, localZRot, scale);
////
////        return MathUtils.getRotation(Matrix4f.mul(thisMatrix, parentMatrix, null));
//
//        //Matrix4f localMatrix = MathUtils.createTransformationMatrix(parent.getPosition(), parent.getRotation().x, parent.getRotation().y, parent.getRotation().z, parent.scale);
//
//
////        Quaternion rotation = new Quaternion();
////        rotation = MathUtils.rotateXYZ(parent.getRotation().x, parent.getRotation().y, parent.getRotation().z, rotation);
////        //rotation.setIdentity();
////        rotation = MathUtils.rotateXYZ(getLocalXRot(), getLocalYRot(), getLocalZRot(), rotation);
////        return MathUtils.getEulerAnglesXYZ(rotation);
//
//
////        Matrix4f parentMatrix = new Matrix4f();
////        parentMatrix.scale(new Vector3f(parent.scale, parent.scale, parent.scale));
////        parentMatrix.translate(parent.getPosition());
////        parentMatrix.rotate((float)Math.toRadians(parent.getRotation().x), new Vector3f(1, 0, 0));
////        parentMatrix.rotate((float)Math.toRadians(parent.getRotation().y), new Vector3f(0, 1, 0));
////        parentMatrix.rotate((float)Math.toRadians(parent.getRotation().z), new Vector3f(0, 0, 1));
////        parentMatrix.setIdentity();
////        parentMatrix.translate(localPosition);
////        parentMatrix.rotate((float)Math.toRadians(localXRot), new Vector3f(1, 0, 0));
////        parentMatrix.rotate((float)Math.toRadians(localYRot), new Vector3f(0, 1, 0));
////        parentMatrix.rotate((float)Math.toRadians(localZRot), new Vector3f(0, 0, 1));
////
////
////        return MathUtils.getRotation(parentMatrix);
//
//        //return MathUtils.getRotation(Matrix4f.mul(Matrix4f.invert(parent.GetModelMatrix(), null), GetModelMatrix(), null));
//
////        return Vector3f.dot(parent.getRotation(), getLocalRotation());
//
////        Matrix4f matrix = new Matrix4f();
////        matrix.translate(parent.getPosition());
////        matrix.rotate((float) Math.toRadians(parent.getRotation().x), new Vector3f(1, 0, 0));
////        matrix.rotate((float) Math.toRadians(parent.getRotation().y), new Vector3f(0, 1, 0));
////        matrix.rotate((float) Math.toRadians(parent.getRotation().z), new Vector3f(0, 0, 1));
////        matrix.translate(localPosition);
////        matrix.rotate((float) Math.toRadians(parent.getRotation().x), new Vector3f(1, 0, 0));
////        matrix.rotate((float) Math.toRadians(parent.getRotation().y), MathUtils.getUp(parent.getRotation()));
////        matrix.rotate((float) Math.toRadians(parent.getRotation().z), MathUtils.getForward(parent.getRotation()));
////        return MathUtils.getRotation(matrix);
//
//        return new Vector3f(parent.getRotation().x + localXRot,parent.getRotation().y +  localYRot, parent.getRotation().z + localZRot);
//        //return new Vector3f(parent.getRotation().x ,parent.getRotation().y, parent.getRotation().z);
//    }

//    public Vector3f getRotationRadians() {
//        Vector3f rotation = getRotation();
//        return new Vector3f((float)Math.toRadians(rotation.x), (float)Math.toRadians(rotation.y), (float)Math.toRadians(rotation.z));
//    }

//    public Vector3f getUp() {
//        Vector3f rotation = this.getRotation();
//        Vector3f up = new Vector3f();
//        up.x = (float)(-Math.cos(rotation.x) * Math.sin(rotation.y) * Math.sin(rotation.z) - Math.sin(rotation.x) * Math.cos(rotation.z));
//        up.y = (float)(-Math.sin(rotation.x) * Math.sin(rotation.y) * Math.sin(rotation.z) + Math.cos(rotation.x) * Math.cos(rotation.z));
//        up.z =  (float)(Math.cos(rotation.y) * Math.sin(rotation.z));
//        return up;
//    }
//
////    | cos(yaw)cos(pitch)        -cos(yaw)sin(pitch)sin(roll)-sin(yaw)cos(roll)         -cos(yaw)sin(pitch)cos(roll)+sin(yaw)sin(roll)|
////    | sin(yaw)cos(pitch)        -sin(yaw)sin(pitch)sin(roll)+cos(yaw)cos(roll)         -sin(yaw)sin(pitch)cos(roll)-cos(yaw)sin(roll)|
////    | sin(pitch)                cos(pitch)sin(roll)                                   cos(pitch)sin(roll)|
//
//    public Vector3f getForward() {
//        Vector3f rotation = this.getRotation();
//        Vector3f forward = new Vector3f();
//        forward.x = (float)(-Math.cos(rotation.x) * Math.sin(rotation.y) * Math.cos(rotation.z) + Math.sin(rotation.x) * Math.sin(rotation.z));
//        forward.y = (float)(-Math.sin(rotation.x) * Math.sin(rotation.y) * Math.cos(rotation.z) - Math.cos(rotation.x) * Math.sin(rotation.z));
//        forward.z =  (float)(Math.cos(rotation.y) * Math.sin(rotation.z));
//        return forward;
//    }
//
//    public Vector3f getRight() {
//        Vector3f rotation = this.getRotation();
//        Vector3f right = new Vector3f();
//        right.x = (float)Math.cos(rotation.x) * (float)Math.cos(rotation.y);
//        right.y = (float)Math.sin(rotation.x) * (float)Math.cos(rotation.y);
//        right.z = (float)Math.sin(rotation.y);
//        return right;
//    }

    public Vector3f getLocalRotation() {
        return MathUtils.getRotation(localMatrix);
    }

    public void scale(Vector3f scale) {
        this.localMatrix.scale(scale);
    }

//    public void setLocalRotation(Vector3f rotation) {
//        this.setLocalXRot(rotation.x);
//        this.setLocalYRot(rotation.y);
//        this.setLocalZRot(rotation.z);
//    }
//
//    public float getLocalXRot() {
//        return localXRot;
//    }
//
//    public void setLocalXRot(float localXRot) {
//        this.localXRot = localXRot;
//    }
//
//    public float getLocalYRot() {
//        return localYRot;
//    }
//
//    public void setLocalYRot(float localYRot) {
//        this.localYRot = localYRot;
//    }
//
//    public float getLocalZRot() {
//        return localZRot;
//    }
//
//    public void setLocalZRot(float localZRot) {
//        this.localZRot = localZRot;
//    }
//
//    public float getScale() {
//        return scale;
//    }
//
    public void setScale(Vector3f scale) {
        //this.localMatrix = MathUtils.createTransformationMatrix(MathUtils.getPosition(localMatrix), getLocalRotation(), scale);
    }


}
