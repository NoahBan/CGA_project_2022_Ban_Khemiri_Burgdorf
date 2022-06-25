package cga.exercise.components.light

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

// 4.1.1
// A - Emissive Material
// B - Ambient Light Color
// C - Diffuse Reflection
// D - Specular Reflection

//4.1.2
// Skalarprodukt könnte negativ sein, wenn wir das Objekt von hinten betrachten
open class PointLight (
        var lightColor : Vector3f = Vector3f(1F,1F,1F),
        modelMatrix : Matrix4f = Matrix4f(),
        parent: Transformable? = null)

    : Transformable(modelMatrix, parent), IPointLight
{

    fun getPremultLightPos(viewMatrix : Matrix4f) : Vector3f {
        val worldModelMatrix = Matrix4f(this.getWorldModelMatrix())
        val thisViewMatrix = Matrix4f(viewMatrix)

        var light_matrix = getWorldModelMatrix()

        return Vector3f(light_matrix.m30(),light_matrix.m31(),light_matrix.m32())
    }
    override fun bind(shaderProgram: ShaderProgram,viewMatrix: Matrix4f) {

        shaderProgram.setUniform("lightColor",lightColor)
        shaderProgram.setUniform("lightPos", getPremultLightPos(viewMatrix))

    }

    fun bindTest (shaderProgram: ShaderProgram, camera : TronCamera){
        val worldModelMatrix = Matrix4f(this.getWorldModelMatrix())
        val thisViewMatrix = Matrix4f(camera.getCalculateViewMatrix())

        var light_matrix = getWorldModelMatrix()//thisViewMatrix.mul(worldModelMatrix)

        shaderProgram.setUniform("lightColor",lightColor)
        shaderProgram.setUniform("lightPos", Vector3f(light_matrix.m30(),light_matrix.m31(),light_matrix.m32()))
    }
}