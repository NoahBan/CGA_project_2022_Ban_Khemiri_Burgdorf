package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f

class BezierCurve (val points: List<Vector3f>,
                   modelMatrix : Matrix4f, parent: Transformable? = null)
    : Transformable(modelMatrix, parent) {

    var t = 0F
    fun moveAlong(value : Float) {
        t = t + value
        if (t > 1F) t = 1F
        if (t < 0F) t = 0F
    }

    override fun getModelMatrix(): Matrix4f {
        var matrix = Matrix4f()

        val p1 = Vector3f(points[0])
        val p2 = Vector3f(points[1])
        val p3 = Vector3f(points[2])

        val iP11 = p1.lerp(p2, t)

        var iP1 = Vector3f(lerp(p1[0],p2[0],t), lerp(p1[1],p2[1],t), lerp(p1[2],p2[2],t))
        var iP2 = Vector3f(lerp(p2[0],p3[0],t), lerp(p2[1],p3[1],t), lerp(p2[2],p3[2],t))

        var pos = Vector3f(lerp(iP1[0],iP2[0],t), lerp(iP1[1],iP2[1],t), lerp(iP1[2],iP2[2],t))

        val segmentSize = 1 / (points.size - 1)

        matrix.m30(pos[0])
        matrix.m31(pos[1])
        matrix.m32(pos[2])

//        this.modelMatrix = matrix
        return matrix
    }

}

fun Vector3f.lerp(to: Vector3f, t: Float) = lerpV3f(this, to, t)

fun lerpV3f(from: Vector3f, to: Vector3f, t: Float) = Vector3f(
    lerp(from[0], to[0], t),
    lerp(from[1], to[1], t),
    lerp(from[2], to[2], t)
)

fun lerp(from: Float, to: Float, t: Float) = from + (to - from) * t