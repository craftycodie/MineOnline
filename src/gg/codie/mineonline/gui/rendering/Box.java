package gg.codie.mineonline.gui.rendering;

import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Box {

    public final Vector3f begin;
    public final Vector3f end;

    public Box(Vector3f begin, Vector3f end) {
        this.begin = begin;
        this.end = end;
    }

    protected List<Vector3f> getVertices() {
        return Arrays.asList(new Vector3f[]{
            begin,
            new Vector3f(begin.x, end.y, end.z),
            new Vector3f(end.x, begin.y, begin.z),
            new Vector3f(end.x, end.y, begin.z),
            new Vector3f(begin.x, begin.y, end.z),
            new Vector3f(end.x, begin.y, end.z),
            new Vector3f(begin.x, end.y, end.z),
            end,
        });
    }

    public float[] getVerticesArray() {
        return new float[] {
            begin.x, begin.y, begin.z,
            begin.x, end.y, end.z,
            end.x, begin.y, begin.z,
            end.x, end.y, begin.z,
            begin.x, begin.y, end.z,
            end.x, begin.y, end.z,
            begin.x, end.y, end.z,
            end.x, end.y, end.z
        };
    }

    protected void render() {
        for (Vector3f vertex : getVertices()) {
            glVertex3f(vertex.x, vertex.y, vertex.z);
        }
    }

}
