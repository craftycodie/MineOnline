package gg.codie.mineonline.gui.rendering;

import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class PlayerModelBox extends Box {

    public final Vector3f rotationOrigin;
    public final boolean mirrored;

    public PlayerModelBox(Vector3f begin, Vector3f end) {
        this(begin, end, new Vector3f());
    }

    public PlayerModelBox(Vector3f begin, int width, int height, int depth) {
        this(begin, new Vector3f(begin.x + width, begin.y + height, begin.z + depth), new Vector3f());
    }

    public PlayerModelBox(Vector3f begin, Vector3f end, Vector3f rotationOrigin) {
        this(begin, end, rotationOrigin, false);
    }

    public PlayerModelBox(Vector3f begin, int width, int height, int depth, Vector3f rotationOrigin) {
        this(begin, new Vector3f(begin.x + width, begin.y + height, begin.z + depth), rotationOrigin);
    }


    public PlayerModelBox(Vector3f begin, Vector3f end, Vector3f rotationOrigin, boolean mirrored) {
        super(begin, end);

        this.rotationOrigin = rotationOrigin;
        this.mirrored = mirrored;
    }

    public PlayerModelBox(Vector3f begin, int width, int height, int depth, Vector3f rotationOrigin, boolean mirrored) {
        super(begin, new Vector3f(begin.x + width, begin.y + height, begin.z + depth));

        this.rotationOrigin = rotationOrigin;
        this.mirrored = mirrored;
    }

    public void render() {
        super.render();
    }

}