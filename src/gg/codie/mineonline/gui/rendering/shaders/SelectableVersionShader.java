package gg.codie.mineonline.gui.rendering.shaders;

import gg.codie.mineonline.gui.rendering.Camera;
import gg.codie.mineonline.gui.rendering.DisplayManager;
import gg.codie.mineonline.gui.rendering.utils.MathUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;

import java.net.URL;

public class SelectableVersionShader extends StaticShader {

    private static final URL VERTEX_FILE = SelectableVersionShader.class.getResource("/shaders/SelectableVersionVertexShader.txt");
    private static final URL FRAGMENT_FILE = SelectableVersionShader.class.getResource("/shaders/SelectableVersionFragmentShader.txt");

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_yMin;
    private int location_yMax;

    public SelectableVersionShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_yMin = super.getUniformLocation("yMin");
        location_yMax = super.getUniformLocation("yMax");
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

    public void loadYBounds(int min, int max) {
        super.loadInt(location_yMin, min);
        super.loadInt(location_yMax, max);
    }

    @Override
    public void start() {
        super.start();
        loadYBounds(DisplayManager.getYBuffer() + (int)DisplayManager.scaledHeight(69), Display.getHeight() - (DisplayManager.getYBuffer() + (int)DisplayManager.scaledHeight(69)));
    }
}
