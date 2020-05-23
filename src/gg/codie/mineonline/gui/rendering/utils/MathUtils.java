package gg.codie.mineonline.gui.rendering.utils;

import gg.codie.mineonline.gui.rendering.Camera;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
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
            float yaw = 0.0f;
            float pitch = 0.0f;
            float roll = 0.0f;
            if(matrix.m00 == 1.0f || matrix.m00 == -1.0f){
                yaw = (float)Math.atan2(matrix.m02, matrix.m23);
                //pitch and roll remain = 0;
            }else{
                yaw = (float) Math.atan2(-matrix.m20, matrix.m00);
                pitch = (float) Math.asin(matrix.m10);
                roll = (float) Math.atan2(-matrix.m12, matrix.m11);
            }
            return new Vector3f(yaw, pitch, roll);
        }

        public static Vector3f getPosition(Matrix4f matrix) {
            float x = matrix.m30;
            float y = matrix.m31;
            float z = matrix.m32;

            return new Vector3f(x, y, z);
        }

        public static float cosFromSin(float sin, float angle) {
            // sin(x)^2 + cos(x)^2 = 1
            float cos = (float)Math.sqrt(1.0f - sin * sin);
            float a = angle + (float)Math.PI / 2;
            float b = a - (int)(a / ((float)Math.PI * 2) * ((float)Math.PI * 2));
            if (b < 0.0)
                b = ((float)Math.PI * 2) + b;
            if (b >= (float)Math.PI)
                return -cos;
            return cos;
        }

        public static float fma(float a, float b, float c) {
            return a * b + c;
        }

        public static Quaternion rotateXYZ(float angleX, float angleY, float angleZ, Quaternion quaternion) {
            float sx = (float)Math.sin(angleX * 0.5f);
            float cx = cosFromSin(sx, angleX * 0.5f);
            float sy = (float)Math.sin(angleY * 0.5f);
            float cy = cosFromSin(sy, angleY * 0.5f);
            float sz = (float)Math.sin(angleZ * 0.5f);
            float cz = cosFromSin(sz, angleZ * 0.5f);

            float cycz = cy * cz;
            float sysz = sy * sz;
            float sycz = sy * cz;
            float cysz = cy * sz;
            float w = cx*cycz - sx*sysz;
            float x = sx*cycz + cx*sysz;
            float y = cx*sycz - sx*cysz;
            float z = cx*cysz + sx*sycz;
            // right-multiply
            quaternion.set(fma(quaternion.w, x, fma(quaternion.x, w, fma(quaternion.y, z, -quaternion.z * y))),
                    fma(quaternion.w, y, fma(-quaternion.x, z, fma(quaternion.y, w, quaternion.z * x))),
                    fma(quaternion.w, z, fma(quaternion.x, y, fma(-quaternion.y, x, quaternion.z * w))),
                    fma(quaternion.w, w, fma(-quaternion.x, x, fma(-quaternion.y, y, -quaternion.z * z))));

            return quaternion;
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
