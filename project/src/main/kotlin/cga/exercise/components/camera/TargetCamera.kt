package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import org.joml.Matrix4f
import org.joml.Vector3f

class TargetCamera(
        var target : Transformable,
        fov : Float,
        aspectRatio : Float,
        nearPlane: Float,
        farPlane: Float,
        modelMatrix : Matrix4f, parent: Transformable? = null,
        var lerpVec : Vector3f? = null,
        var lerpT : Float = 0f
    ): Camera(fov, aspectRatio, nearPlane, farPlane, modelMatrix) {

    override fun getCalculateViewMatrix(): Matrix4f {
        var eye = getWorldPosition()
        var center = target.getWorldPosition()
        if (lerpVec != null) center = center.lerp(lerpVec,lerpT)
        var up  = getWorldYAxis()

        return Matrix4f().lookAt(eye,center,up)
    }

}