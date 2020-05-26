package gg.codie.mineonline.gui.rendering.shaders;

import gg.codie.mineonline.gui.rendering.Camera;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import org.lwjgl.util.vector.Matrix4f;

import java.net.URL;

public class GUIShader extends StaticShader {

    private static final URL VERTEX_FILE = GUIShader.class.getResource("/shaders/GUIVertexShader.txt");
    private static final URL FRAGMENT_FILE = GUIShader.class.getResource("/shaders/GUIFragmentShader.txt");

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    public GUIShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "texutureCoordinates");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = MathUtils.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

}
