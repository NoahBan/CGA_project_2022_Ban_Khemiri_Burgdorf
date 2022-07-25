package cga.exercise.components.camera

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.*
import org.joml.Math.toRadians

class Skybox(camera: TronCamera):ICamera {
    val camera = camera
    override fun getCalculateViewMatrix(): Matrix4f {
        var view = Matrix4f(Matrix3f(Matrix4f().lookAt(camera.getWorldPosition(),camera.getWorldPosition().add(camera.getWorldYAxis()),camera.getWorldYAxis() )))
        return view
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        return Matrix4f().perspective(toRadians(45f),camera.aspectRatio,camera.nearPlane,camera.farPlane)
    }

    override fun bind(shader: ShaderProgram) {
        val calculateViewMatrix = getCalculateViewMatrix()
        val calculateProjectionMatrix = getCalculateProjectionMatrix()
        shader.setUniform("View_matrix",calculateViewMatrix,false)
        shader.setUniform("Projection_matrix",calculateProjectionMatrix,false)

    }


}