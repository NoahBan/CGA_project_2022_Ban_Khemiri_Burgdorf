package cga.exercise.components.utility

import org.joml.Matrix4f
import org.joml.Vector3f

fun Vector3f.lerp(to: Vector3f, t: Float) = lerpV3f(this, to, t)

fun lerpV3f(from: Vector3f, to: Vector3f, t: Float) = Vector3f(
        lerpf(from[0], to[0], t),
        lerpf(from[1], to[1], t),
        lerpf(from[2], to[2], t)
)

fun lerpf(from: Float, to: Float, t: Float) = from + (to - from) * t

fun lerpMatrices(from : Matrix4f, to : Matrix4f, t : Float) : Matrix4f {
    var lerpMatrix = Matrix4f()
    lerpMatrix.m00(lerpf(from.m00(),to.m00(), t))
    lerpMatrix.m01(lerpf(from.m01(),to.m01(), t))
    lerpMatrix.m02(lerpf(from.m02(),to.m02(), t))
    lerpMatrix.m03(lerpf(from.m03(),to.m03(), t))
    lerpMatrix.m10(lerpf(from.m10(),to.m10(), t))
    lerpMatrix.m11(lerpf(from.m11(),to.m11(), t))
    lerpMatrix.m12(lerpf(from.m12(),to.m12(), t))
    lerpMatrix.m13(lerpf(from.m13(),to.m13(), t))
    lerpMatrix.m20(lerpf(from.m20(),to.m20(), t))
    lerpMatrix.m21(lerpf(from.m21(),to.m21(), t))
    lerpMatrix.m22(lerpf(from.m22(),to.m22(), t))
    lerpMatrix.m23(lerpf(from.m23(),to.m23(), t))
    lerpMatrix.m30(lerpf(from.m30(),to.m30(), t))
    lerpMatrix.m31(lerpf(from.m31(),to.m31(), t))
    lerpMatrix.m32(lerpf(from.m32(),to.m32(), t))
    lerpMatrix.m33(lerpf(from.m33(),to.m33(), t))
    return lerpMatrix
}

fun lerpMatricesRotation(from : Matrix4f, to : Matrix4f, t : Float) : Matrix4f {
    var lerpMatrix = from
    lerpMatrix.m00(lerpf(from.m00(),to.m00(), t))
    lerpMatrix.m01(lerpf(from.m01(),to.m01(), t))
    lerpMatrix.m02(lerpf(from.m02(),to.m02(), t))
    lerpMatrix.m10(lerpf(from.m10(),to.m10(), t))
    lerpMatrix.m11(lerpf(from.m11(),to.m11(), t))
    lerpMatrix.m12(lerpf(from.m12(),to.m12(), t))
    lerpMatrix.m20(lerpf(from.m20(),to.m20(), t))
    lerpMatrix.m21(lerpf(from.m21(),to.m21(), t))
    lerpMatrix.m22(lerpf(from.m22(),to.m22(), t))
    return lerpMatrix
}