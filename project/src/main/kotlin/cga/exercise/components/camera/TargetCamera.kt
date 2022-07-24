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
        modelMatrix : Matrix4f, parent: Transformable? = null
    ): Camera(fov, aspectRatio, nearPlane, farPlane, modelMatrix) {


    fun alignCamera(){
        val thisMatrix = this.getModelMatrix()

        var alignEye = getWorldPosition()
        var alignCenter = target.getWorldPosition().sub(getWorldZAxis()).normalize()
        var aligUnp  = Vector3f(0f,1f,0f)

        thisMatrix.lookAt(alignEye,alignCenter,aligUnp)
        thisMatrix.m30(getWorldPosition()[0])
        thisMatrix.m31(getWorldPosition()[1])
        thisMatrix.m32(getWorldPosition()[2])

        this.setModelMatrix(thisMatrix)
    }


    override fun getCalculateViewMatrix(): Matrix4f {
        alignCamera()
        return super.getCalculateViewMatrix()
    }

}