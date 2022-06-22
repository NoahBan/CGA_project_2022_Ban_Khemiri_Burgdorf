package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

class PointLight (
        var lightColor : Vector3f = Vector3f(1F,1F,1F),
        modelMatrix : Matrix4f = Matrix4f(),
        parent: Transformable? = null)

    : Transformable(modelMatrix, parent), IPointLight
{

        init {

        }

    override fun bind(shaderProgram: ShaderProgram) {
        shaderProgram.setUniform("lightColor",lightColor)
        shaderProgram.setUniform("lightMatrix",getWorldModelMatrix(), false)
    }
}