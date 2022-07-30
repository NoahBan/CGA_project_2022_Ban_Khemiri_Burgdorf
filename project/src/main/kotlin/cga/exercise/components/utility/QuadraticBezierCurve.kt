package cga.exercise.components.utility

import org.joml.Matrix4f
import org.joml.Vector3f

class QuadraticBezierCurve (val start : Vector3f, val mid : Vector3f, val end : Vector3f) {

    fun calcPos(t: Float): Vector3f {
        var lerpP1 = lerpV3f(start, mid, t)
        var lerpP2 = lerpV3f(mid, end, t)
        var pos = lerpV3f(lerpP1, lerpP2, t)
        return pos
    }

    fun calcPosAndRota(t: Float): Matrix4f {
        var newT = t
        val offset = 0.0001f
        if (t - offset <= 0f) newT += offset
        var pos = calcPos(newT - offset)
        var target = calcPos(newT)
        var newMatrix = Matrix4f()
        newMatrix.m30(pos[0])
        newMatrix.m31(pos[1])
        newMatrix.m32(pos[2])
        var eye = pos
        var center = target
        var up = Vector3f(0f, 1f, 0f)
        newMatrix.lookAt(eye, center, up)
        return newMatrix

    }
}
