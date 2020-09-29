package gg.codie.mineonline.gui.rendering;

import org.lwjgl.util.vector.Vector3f;

public class Camera {

    protected Vector3f position = new Vector3f(0, 0, 0);
    protected float pitch;
    protected float yaw;
    protected float roll;

    public static Camera singleton = null;

    public Camera() {
        singleton = this;
    }

    public void move() { }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

}
