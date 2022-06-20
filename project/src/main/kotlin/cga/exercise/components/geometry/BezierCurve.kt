package cga.exercise.components.geometry

import org.joml.Matrix4f
import org.joml.Vector3f

class BezierCurve (val points: List<Vector3f> = listOf<Vector3f>()) {

    fun getMatrixAt(t : Float) : Matrix4f{
        var matrix = Matrix4f()

        val segmentSize = 1 / (points.size - 1)



        return matrix
    }

}