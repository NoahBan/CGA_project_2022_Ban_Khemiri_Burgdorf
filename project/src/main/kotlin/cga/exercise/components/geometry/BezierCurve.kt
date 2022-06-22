package cga.exercise.components.geometry

import cga.exercise.components.utility.lerp
import org.joml.Matrix4f
import org.joml.Vector3f

class BezierCurve (val points: List<Vector3f>,
                   modelMatrix : Matrix4f, parent: Transformable? = null)
    : Transformable(modelMatrix, parent) {

    var t = 0F
    val segmentAmount : Int
    val segmentSize : Float
    init {
        if (points.size < 3) throw Exception("Need at least 3 points for curve to function")
        if (points.size % 2 == 0) throw Exception("Amount of points must be 3 + 2 * n")

        var pointCount = 0
        var minus = 0

        for (each in points) {
            pointCount++
            if(pointCount % 2 == 1) minus++
        }
        segmentAmount = pointCount - minus
        segmentSize = 1F / segmentAmount

        println(segmentSize)
    }


    fun moveAlong(value : Float) {
        if ((t + value) > 1F) t = 1F
        else if ((t + value) < 0F) t = 0F
        else t = t + value
    }

    override fun getModelMatrix(): Matrix4f {
        var matrix = Matrix4f()

        val pos = calcSegmentPos(getActiveSegment(),calcSegmentT())
        matrix.m30(pos[0])
        matrix.m31(pos[1])
        matrix.m32(pos[2])

        return matrix
    }

    fun calcSegmentT() : Float{


        return t
    }

    fun getActiveSegment() : List<Vector3f> {
//        val p1 = Vector3f()
//        val p2 = Vector3f()
//        val p3 = Vector3f()

        val p1 = Vector3f(points[0])
        val p2 = Vector3f(points[1])
        val p3 = Vector3f(points[2])

        return listOf(p1,p2,p3)
    }

    fun calcSegmentPos(segment : List<Vector3f>, segmentT : Float) : Vector3f {
        var iP1 = segment[0].lerp(segment[1],segmentT)
        var iP2 = segment[1].lerp(segment[2],segmentT)

        return iP1.lerp(iP2,segmentT)
    }

}

