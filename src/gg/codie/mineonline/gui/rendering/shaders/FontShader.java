package gg.codie.mineonline.gui.rendering.shaders;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.net.URL;

public class FontShader extends ShaderProgram{

    private static final URL VERTEX_FILE = GUIShader.class.getResource("/shaders/fontVertex.txt");
    private static final URL FRAGMENT_FILE = GUIShader.class.getResource("/shaders/fontFragment.txt");

    private int location_colour;
    private int location_translation;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_colour = super.getUniformLocation("colour");
        location_translation = super.getUniformLocation("translation");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    public void loadColour(Vector3f colour){
        super.loadVector(location_colour, colour);
    }

    public void loadTranslation(Vector2f translation){
        super.loadVector(location_translation, translation);
    }


}
