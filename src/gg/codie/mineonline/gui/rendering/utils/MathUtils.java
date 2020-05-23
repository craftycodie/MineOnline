package gg.codie.mineonline.gui.rendering.utils;

import gg.codie.mineonline.gui.rendering.Camera;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MathUtils {

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();

        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera){
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), viewMatrix,
                viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), viewMatrix,
                viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }

    public static float[] makeBoxVertices(Vector3f begin, Vector3f end) {
        return new float[]{
                begin.x, end.y, begin.z,   // v1
                begin.x, begin.y, begin.z,  // v2
                end.x, begin.y, begin.z,   // v3
                end.x, end.y, begin.z,    // v4

                begin.x, end.y, end.z,    // v5
                begin.x, begin.y, end.z,   // v6
                end.x, begin.y, end.z,    // v7
                end.x, end.y, end.z,     // v8

                end.x, end.y, begin.z,    // v4
                end.x, begin.y, begin.z,   // v3
                end.x, begin.y, end.z,    // v7
                end.x, end.y, end.z,     // v8

                begin.x, end.y, begin.z,   // v1
                begin.x, begin.y, begin.z,  // v2
                begin.x, begin.y, end.z,   // v6
                begin.x, end.y, end.z,    // v5

                begin.x, end.y, end.z,    // v5
                begin.x, end.y, begin.z,   // v1
                end.x, end.y, begin.z,    // v4
                end.x, end.y, end.z,     // v8

                begin.x, begin.y, end.z,   // v6
                begin.x, begin.y, begin.z,  // v2
                end.x, begin.y, begin.z,   // v3
                end.x, begin.y, end.z    // v7

        };

    }

        public static Vector3f getRotation(Matrix4f matrix) {
            float x,y,z;
//            Matrix3f m = new Matrix3f();
//
//            matrix.get(m);
            Matrix4f m = matrix;
            if(Math.abs(m.m02 - 1) < 0.0000001) {
                x = (float) Math.atan2(-m.m10,m.m11);
                y = (float) (-3.1415926535897931/2);
                z = 0.0f;
            } else if(Math.abs(m.m02 + 1)< 0.0000001) {
                x = (float) Math.atan2(m.m10,m.m11);
                y = (float) (3.1415926535897931/2);
                z = 0.0f;
            } else {
                x = (float) Math.atan2(m.m12,m.m22);
                y = (float) Math.atan2(-m.m02, Math.sqrt(m.m12 * m.m12 + m.m22 * m.m22));
                z = (float) Math.atan2(m.m01,m.m00);
            }

            return new Vector3f(x, y, z);
        }

        public static Vector3f getPosition(Matrix4f matrix) {
            float x = matrix.m30;
            float y = matrix.m31;
            float z = matrix.m32;

            return new Vector3f(x, y, z);
        }

        public static Vector3f getForward(float x, float y) {
            return new Vector3f((float)Math.cos(x) * (float)Math.cos(y), (float)Math.sin(x) * (float)Math.cos(y), (float)Math.sin(y));
        }

        public static float getDistance(Vector3f a, Vector3f b) {
                return (float)(Math.sqrt((b.x - a.x)*(b.x - a.x) + (b.y - a.y) * (b.y - a.y) + (b.z - a.z) * (b.z - a.z)));
        }

//        return new float[] {
//                -0.5f,0.5f,-0.5f,   // v1
//                -0.5f,-0.5f,-0.5f,  // v2
//                0.5f,-0.5f,-0.5f,   // v3
//                0.5f,0.5f,-0.5f,    // v4
//
//                -0.5f,0.5f,0.5f,    // v5
//                -0.5f,-0.5f,0.5f,   // v6
//                0.5f,-0.5f,0.5f,    // v7
//                0.5f,0.5f,0.5f,     // v8
//
//                0.5f,0.5f,-0.5f,    // v4
//                0.5f,-0.5f,-0.5f,   // v3
//                0.5f,-0.5f,0.5f,    // v7
//                0.5f,0.5f,0.5f,     // v8
//
//                -0.5f,0.5f,-0.5f,   // v1
//                -0.5f,-0.5f,-0.5f,  // v2
//                -0.5f,-0.5f,0.5f,   // v6
//                -0.5f,0.5f,0.5f,    // v5
//
//                -0.5f,0.5f,0.5f,    // v5
//                -0.5f,0.5f,-0.5f,   // v1
//                0.5f,0.5f,-0.5f,    // v4
//                0.5f,0.5f,0.5f,     // v8
//
//                -0.5f,-0.5f,0.5f,   // v6
//                -0.5f,-0.5f,-0.5f,  // v2
//                0.5f,-0.5f,-0.5f,   // v3
//                0.5f,-0.5f,0.5f     // v7
//
//        };

}
