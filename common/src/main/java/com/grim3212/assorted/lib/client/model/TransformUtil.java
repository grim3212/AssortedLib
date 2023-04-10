package com.grim3212.assorted.lib.client.model;

import com.mojang.math.Transformation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TransformUtil {

    /**
     * Transforms the position according to this transformation.
     *
     * @param position the position to transform
     */
    public static void transformPosition(Transformation transform, Vector4f position) {
        position.mul(transform.getMatrix());
    }

    /**
     * Transforms the normal according to this transformation and normalizes it.
     *
     * @param normal the normal to transform
     */
    public static void transformNormal(Transformation transform, Vector3f normal) {
        normal.mul(getNormalMatrix(transform));
        normal.normalize();
    }

    public static Matrix3f getNormalMatrix(Transformation transform) {
        Matrix3f normalTransform = new Matrix3f(transform.getMatrix());
        normalTransform.invert();
        normalTransform.transpose();
        return normalTransform;
    }

    /**
     * Converts and returns a new transformation based on this transformation from assuming a center-block system to an
     * opposing-corner-block system.
     *
     * @return a new transformation using the opposing-corner-block system
     */
    public static Transformation blockCenterToCorner(Transformation transform) {
        return applyOrigin(transform, new Vector3f(.5f, .5f, .5f));
    }

    /**
     * Returns a new transformation with a changed origin by applying the given parameter (which is relative to the
     * current origin). This can be used for switching between coordinate systems.
     *
     * @param origin the new origin as relative to the current origin
     * @return a new transformation with a changed origin
     */
    public static Transformation applyOrigin(Transformation transform, Vector3f origin) {
        if (transform.equals(Transformation.identity())) return Transformation.identity();

        Matrix4f ret = transform.getMatrix();
        Matrix4f tmp = new Matrix4f().translation(origin.x(), origin.y(), origin.z());
        tmp.mul(ret, ret);
        tmp.translation(-origin.x(), -origin.y(), -origin.z());
        ret.mul(tmp);
        return new Transformation(ret);
    }

}
