package gg.codie.mineonline.gui.rendering.shaders;

import gg.codie.mineonline.gui.rendering.Camera;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import org.lwjgl.util.vector.Matrix4f;

import java.net.URL;

public class StaticShader extends ShaderProgram {

    private static final URL VERTEX_FILE = StaticShader.class.getResource("/shaders/vertexShader.txt");
    private static final URL FRAGMENT_FILE = StaticShader.class.getResource("/shaders/fragmentShader.txt");

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    public StaticShader() {
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
