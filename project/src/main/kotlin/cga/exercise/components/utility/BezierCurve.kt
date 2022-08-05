package cga.exercise.components.utility

import org.joml.Matrix4f
import org.joml.Vector3f

class BezierCurve (val pointList: MutableList<Vector3f>) {


    fun getPos(t : Float): Vector3f{
        return calcPos(pointList,t)
    }

    fun getPosAndRota(t : Float): Matrix4f{
        return calcPosAndRota(pointList,t)
    }

    fun calcPos (list : MutableList<Vector3f>, t : Float) : Vector3f{

        if (list.size == 1){
            return list.elementAt(0)
        }

        val res = mutableListOf<Vector3f>()

        list.forEachIndexed{index, element ->
            if(index < list.size-1){
                res.add(lerpV3f(element,list[index+1],t))
            }
        }
        return calcPos(res,t)
    }

    fun calcPosAndRota(list : MutableList<Vector3f>, t : Float): Matrix4f {
        var newT = t
        val offset = 0.0001f
        if (t - offset <= 0f) newT += offset
        var pos = calcPos(list,newT - offset)
        var target = calcPos(list,newT)
        var newMatrix = Matrix4f()

        var eye = pos
        var center = target.negate()
        var up = Vector3f(0f, 1f, 0f)
        newMatrix.lookAt(eye, center, up).invert()

        newMatrix.normalize3x3()
        return newMatrix

    }
}
