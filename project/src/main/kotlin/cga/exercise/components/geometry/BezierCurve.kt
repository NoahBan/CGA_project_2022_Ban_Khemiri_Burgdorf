package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f


class BezierCurve (val points: List<Vector3f> = listOf<Vector3f>()) {

    fun getTransformAt(t : Float) : Matrix4f{
        var matrix = Matrix4f()

        val p1 = Vector3f(points[0])
        val p2 = Vector3f(points[1])
        val p3 = Vector3f(points[2])

//        var iP1 = Vector3f(lerp(p1[0],p2[0],t), lerp(p1[1],p2[1],t), lerp(p1[2],p2[2],t))
//        var iP2 = Vector3f(lerp(p2[0],p3[0],t), lerp(p2[1],p3[1],t), lerp(p2[2],p3[2],t))
//
//        var pos = Vector3f(lerp(iP1[0],iP2[0],t), lerp(iP1[1],iP2[1],t), lerp(iP1[2],iP2[2],t))

        val segmentSize = 1 / (points.size - 1)



        return matrix
    }

}