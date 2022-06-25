package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

class SpotLight(
        lightColor : Vector3f = Vector3f(1F,1F,1F),
        modelMatrix : Matrix4f = Matrix4f(),
        projectionMatrix : Matrix4f = Matrix4f(),
        private var winkelInnen : Float,
        private var winkelAußen : Float,
        parent: Transformable? = null)


        : PointLight(lightColor, modelMatrix, parent), ISpotLight

{
    override fun bind(shaderProgram: ShaderProgram, viewMatrix: Matrix4f,projectionMatrix: Matrix4f ) {
        TODO("Not yet implemented")
    }
}