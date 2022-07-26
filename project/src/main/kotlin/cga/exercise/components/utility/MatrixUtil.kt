package cga.exercise.components.utility

import org.joml.Matrix4f
import org.joml.Vector3f

fun setEuler(inMatrix : Matrix4f, x : Float, y : Float, z : Float) : Matrix4f{
    var outMatrix = Matrix4f()

    var eulers = Vector3f(0f,0f,0f)
    inMatrix.getEulerAnglesZYX(eulers)

    outMatrix = inMatrix.rotateXYZ(Vector3f(-eulers[0],-eulers[1],-eulers[2]))

    outMatrix.rotateXYZ(
        Vector3f(x,y,z))
    return outMatrix
}