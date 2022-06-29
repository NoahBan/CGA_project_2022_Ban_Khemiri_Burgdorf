package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.*
import org.joml.Math.toRadians


class TronCamera (
    val fov : Float = 90F,
    val aspectRatio : Float = 9f/9f,
    val nearPlane: Float = 0.1F,
    val farPlane: Float = 100.0F,
    modelMatrix : Matrix4f, parent: Transformable? = null) : Transformable(modelMatrix, parent) , ICamera {

    override fun getCalculateViewMatrix(): Matrix4f {
        var eye = getWorldPosition()
        var center = getWorldPosition().sub(getWorldZAxis())
        var up  = getWorldYAxis()

        return Matrix4f().lookAt(eye,center,up)
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(fov,aspectRatio,nearPlane,farPlane)
    }

    override fun bind(shader: ShaderProgram) {
        val calculateViewMatrix = getCalculateViewMatrix();
        val calculateProjectionMatrix = getCalculateProjectionMatrix();
        shader.setUniform("View_matrix",calculateViewMatrix,false);
        shader.setUniform("Projection_matrix",calculateProjectionMatrix,false);
    }
}