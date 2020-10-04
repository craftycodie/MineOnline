package gg.codie.mineonline.gui.rendering.utils;

import gg.codie.mineonline.gui.rendering.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class MathUtils {

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();

        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1), matrix, matrix);
        Matrix4f.scale(scale, matrix, matrix);

        return matrix;
    }


    public static Matrix4f createTransformationMatrix(Vector3f position, Quaternion rotation, Vector3f scale) {
        Matrix4f dest = new Matrix4f();
        dest.setIdentity();

        float q00 = 2.0f * rotation.x * rotation.x;
        float q11 = 2.0f * rotation.y * rotation.y;
        float q22 = 2.0f * rotation.z * rotation.z;
        float q01 = 2.0f * rotation.x * rotation.y;
        float q02 = 2.0f * rotation.x * rotation.z;
        float q03 = 2.0f * rotation.x * rotation.w;
        float q12 = 2.0f * rotation.y * rotation.z;
        float q13 = 2.0f * rotation.y * rotation.w;
        float q23 = 2.0f * rotation.z * rotation.w;

        dest.m00 = (1.0f - q11 - q22) * scale.x;
        dest.m01 = (q01 + q23) * scale.x;
        dest.m02 = (q02 - q13) * scale.x;
        dest.m03 = 0.0f;
        dest.m10 = (q01 - q23) * scale.y;
        dest.m11 = (1.0f - q22 - q00) * scale.y;
        dest.m12 = (q12 + q03) * scale.y;
        dest.m13 = 0.0f;
        dest.m20 = (q02 + q13) * scale.z;
        dest.m21 = (q12 - q03) * scale.z;
        dest.m22 = (1.0f - q11 - q00) * scale.z;
        dest.m23 = 0.0f;
        dest.m30 = position.x;
        dest.m31 = position.y;
        dest.m32 = position.z;
        dest.m33 = 1.0f;

        return dest;
    }

    public static Quaternion rotate(Quaternion q, Vector3f rotation) {
        // Assuming the angles are in radians.
        float c1 = (float)Math.cos(rotation.y/2);
        float s1 = (float)Math.sin(rotation.y/2);
        float c2 = (float)Math.cos(rotation.x/2);
        float s2 = (float)Math.sin(rotation.x/2);
        float c3 = (float)Math.cos(rotation.z/2);
        float s3 = (float)Math.sin(rotation.z/2);
        float c1c2 = c1*c2;
        float s1s2 = s1*s2;
        q.w =(c1c2*c3 - s1s2*s3);
        q.x =(c1c2*s3 + s1s2*c3);
        q.y =(s1*c2*c3 + c1*s2*s3);
        q.z =(c1*s2*c3 - s1*c2*s3);

        return q;
    }

    public static Quaternion quaternion(Vector3f rotation) {
        Quaternion quaternion = new Quaternion();
        quaternion = rotate(quaternion, rotation);
        return quaternion;
    }

//    public static Quaternion rotateXYZ(Vector3f rotation, Quaternion dest) {
//        float rx = (float) Math.toRadians(rotation.x);
//        float ry = (float) Math.toRadians(rotation.y);
//        float rz = (float) Math.toRadians(rotation.z);
//
//        float sx = (float)Math.sin(rx * 0.5f);
//        float cx = cosFromSin(sx, rx * 0.5f);
//        float sy = (float)Math.sin(ry * 0.5f);
//        float cy = cosFromSin(sy, ry * 0.5f);
//        float sz = (float)Math.sin(rz * 0.5f);
//        float cz = cosFromSin(sz, rz * 0.5f);
//
//        float cycz = cy * cz;
//        float sysz = sy * sz;
//        float sycz = sy * cz;
//        float cysz = cy * sz;
//        float w = cx*cycz - sx*sysz;
//        float x = sx*cycz + cx*sysz;
//        float y = cx*sycz - sx*cysz;
//        float z = cx*cysz + sx*sycz;
//        // right-multiply
//        dest.set(fma(dest.w, x, fma(dest.x, w, fma(dest.y, z, -dest.z * y))),
//                fma(dest.w, y, fma(-dest.x, z, fma(dest.y, w, dest.z * x))),
//                fma(dest.w, z, fma(dest.x, y, fma(-dest.y, x, dest.z * w))),
//                fma(dest.w, w, fma(-dest.x, x, fma(-dest.y, y, -dest.z * z))));
//        return dest;
//    }

    public static Quaternion rotateX(float angle, Quaternion dest) {
        float sin = (float)Math.sin(angle * 0.5f);
        float cos = cosFromSin(sin, angle * 0.5f);
        dest.set(dest.w * sin + dest.x * cos,
                dest.y * cos + dest.z * sin,
                dest.z * cos - dest.y * sin,
                dest.w * cos - dest.x * sin);
        return dest;
    }

    public static Quaternion rotateY(float angle, Quaternion dest) {
        float sin = (float)Math.sin(angle * 0.5f);
        float cos = cosFromSin(sin, angle * 0.5f);
        dest.set(dest.x * cos - dest.z * sin,
                dest.w * sin + dest.y * cos,
                dest.x * sin + dest.z * cos,
                dest.w * cos - dest.y * sin);
        return dest;
    }

    public static Quaternion rotateZ(float angle, Quaternion dest) {
        float sin = (float)Math.sin(angle * 0.5f);
        float cos = cosFromSin(sin, angle * 0.5f);
        dest.set(dest.x * cos + dest.y * sin,
                dest.y * cos - dest.x * sin,
                dest.w * sin + dest.z * cos,
                dest.w * cos - dest.z * sin);
        return dest;
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

    public static float[] makePlaneVertices(Vector2f begin, Vector2f end) {
        return new float[] {
                begin.x, begin.y, 0,   // v1
                begin.x, end.y, 0,  // v2
                end.x, end.y, 0,   // v3
                end.x, begin.y, 0,    // v4
        };
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
            Vector3f dest = new Vector3f();
            dest.x = (float)Math.toDegrees(Math.atan2(matrix.m12, matrix.m22));
            dest.y = (float)Math.toDegrees(Math.atan2(-matrix.m02, Math.sqrt(matrix.m12 * matrix.m12 + matrix.m22 * matrix.m22)));
            dest.z = (float)Math.toDegrees(Math.atan2(matrix.m01, matrix.m00));
            return dest;
        }

        public static Vector3f getPosition(Matrix4f matrix) {
            float x = matrix.m30;
            float y = matrix.m31;
            float z = matrix.m32;

            return new Vector3f(x, y, z);
        }

        // This should work until things get complicated (eg negative scale) which shouldn't happen for thisd
        public static Vector3f getScale(Matrix4f matrix4f) {
            Vector3f dest = new Vector3f();
            dest.x = (float)Math.sqrt(Math.pow(matrix4f.m00, 2) + Math.pow(matrix4f.m01, 2) + Math.pow(matrix4f.m02, 2));
            dest.y = (float)Math.sqrt(Math.pow(matrix4f.m10, 2) + Math.pow(matrix4f.m11, 2) + Math.pow(matrix4f.m12, 2));
            dest.z = (float)Math.sqrt(Math.pow(matrix4f.m20, 2) + Math.pow(matrix4f.m21, 2) + Math.pow(matrix4f.m22, 2));
//            float sx = length(new Vector3f(matrix4f.m11, matrix4f.m12, matrix4f.m13));
//            float sy = length(new Vector3f(matrix4f.m21, matrix4f.m22, matrix4f.m23));
//            float sz = length(new Vector3f(matrix4f.m31, matrix4f.m32, matrix4f.m33));
            return dest;
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


    public static void setRotationXYZ(Vector3f rotation, Matrix4f matrix) {
        float sinX = (float)Math.sin(rotation.x);
        float cosX = cosFromSin(sinX, rotation.x);
        float sinY = (float)Math.sin(rotation.y);
        float cosY = cosFromSin(sinY, rotation.y);
        float sinZ = (float)Math.sin(rotation.z);
        float cosZ = cosFromSin(sinZ, rotation.z);
        float nm01 = -sinX * -sinY;
        float nm02 = cosX * -sinY;

        matrix.m20 = sinY;
        matrix.m21 = -sinX * cosY;
        matrix.m22 = cosX * cosY;
        matrix.m00 = cosY * cosZ;
        matrix.m01 = nm01 * cosZ + cosX * sinZ;
        matrix.m02 = nm02 * cosZ + sinX * sinZ;
        matrix.m10 = cosY * -sinZ;
        matrix.m11 = nm01 * -sinZ + cosX * cosZ;
        matrix.m12 = nm02 * -sinZ + sinX * cosZ;
        matrix.setIdentity();
    }


    public static float safeAsin(float r) {
        return r <= -1.0f ? -((float)Math.PI / 2) : r >= 1.0f ? ((float)Math.PI / 2) : (float)Math.asin(r);
    }

    public static Quaternion rotateZYX(float angleZ, float angleY, float angleX, Quaternion quaternion) {
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
        float w = cx*cycz + sx*sysz;
        float x = sx*cycz - cx*sysz;
        float y = cx*sycz + sx*cysz;
        float z = cx*cysz - sx*sycz;
        // right-multiply
        quaternion.set(fma(quaternion.w, x, fma(quaternion.x, w, fma(quaternion.y, z, -quaternion.z * y))),
                fma(quaternion.w, y, fma(-quaternion.x, z, fma(quaternion.y, w, quaternion.z * x))),
                fma(quaternion.w, z, fma(quaternion.x, y, fma(-quaternion.y, x, quaternion.z * w))),
                fma(quaternion.w, w, fma(-quaternion.x, x, fma(-quaternion.y, y, -quaternion.z * z))));

        return quaternion;
    }

//    public static Vector3f getEulerAnglesXYZ(Quaternion quaternion) {
//        Vector3f eulerAngles = new Vector3f();
//        eulerAngles.x = (float)Math.atan2(2.0f * (quaternion.x*quaternion.w - quaternion.y*quaternion.z), 1.0f - 2.0f * (quaternion.x*quaternion.x + quaternion.y*quaternion.y));
//        eulerAngles.y = safeAsin(2.0f * (quaternion.x*quaternion.z + quaternion.y*quaternion.w));
//        eulerAngles.z = (float)Math.atan2(2.0f * (quaternion.z*quaternion.w - quaternion.x*quaternion.y), 1.0f - 2.0f * (quaternion.y*quaternion.y + quaternion.z*quaternion.z));
//        return eulerAngles;
//    }

    public static Vector3f getForward(Vector3f rotation) {
        Vector3f up = new Vector3f();

        up.x = -(float)Math.cos(rotation.z) * (float)Math.sin(rotation.z) - (float)Math.sin(rotation.z) * (float)Math.sin(rotation.y) * (float)Math.cos(rotation.z);
        up.y = (float)Math.sin(rotation.z) * (float)Math.sin(rotation.z) - (float)Math.cos(rotation.z) * (float)Math.sin(rotation.y) * (float)Math.cos(rotation.z);
        up.z = (float)Math.cos(rotation.y) * (float)Math.cos(rotation.z);

        return (Vector3f)up.normalise();
    }

    public static Vector3f getRight(Vector3f rotation) {
        return (Vector3f)(new Vector3f((float)Math.cos(rotation.x) * (float)Math.cos(rotation.y), (float)Math.sin(rotation.y), (float)Math.cos(rotation.x) * (float)Math.sin(rotation.y))).normalise();
    }

    public static Vector3f getUp(Vector3f rotation) {
        return Vector3f.cross((Vector3f)getForward(rotation).normalise(), (Vector3f)getRight(rotation).normalise(), null);
    }

    public static float getDistance(Vector3f a, Vector3f b) {
            return (float)(Math.sqrt((b.x - a.x)*(b.x - a.x) + (b.y - a.y) * (b.y - a.y) + (b.z - a.z) * (b.z - a.z)));
    }
}
