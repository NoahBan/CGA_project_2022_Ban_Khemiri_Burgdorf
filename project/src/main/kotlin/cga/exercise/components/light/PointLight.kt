package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
// 4.1.1
// A - Emissive Material
// B - Ambient Light Color
// C - Diffuse Reflection
// D - Specular Reflection

//4.1.2
// Skalarprodukt könnte negativ sein, wenn wir das Objekt von hinten betrachten
class PointLight (
        var lightColor : Vector3f = Vector3f(1F,1F,1F),
        modelMatrix : Matrix4f = Matrix4f(),
        parent: Transformable? = null)

    : Transformable(modelMatrix, parent), IPointLight
{

        init {

        }

    override fun bind(shaderProgram: ShaderProgram,viewMatrix: Matrix4f,projectionMatrix: Matrix4f) {
        shaderProgram.setUniform("lightColor",lightColor)
        var light_matrix = projectionMatrix.mul(viewMatrix.mul(getModelMatrix().mul(getWorldModelMatrix())))
        shaderProgram.setUniform("lightPos",Vector3f(light_matrix.m30(),light_matrix.m31(),light_matrix.m32()))
    }
}