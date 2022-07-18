package cga.exercise.components.light

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.*

// 4.1.1
// A - Emissive Material
// B - Ambient Light Color
// C - Diffuse Reflection
// D - Specular Reflection //Sharpens highlight, makes it more intense

//4.1.2
// Skalarprodukt könnte negativ sein, wenn wir das Objekt von hinten betrachten.
open class PointLight (
        val attenuationType : AttenuationType,
        var lightColor : Vector3f = Vector3f(1F,1F,1F),
        var intensity : Float,
        modelMatrix : Matrix4f = Matrix4f(),
        parent: Transformable? = null)
        : Transformable(modelMatrix, parent) {

fun getPremultLightPos(viewMatrix : Matrix4f) : Vector3f {
        val worldModelMatrix = Matrix4f(this.getWorldModelMatrix())

        val calcLightPos = Vector4f(
                viewMatrix.m00() * worldModelMatrix.m30() + viewMatrix.m10() * worldModelMatrix.m31() + viewMatrix.m20() * worldModelMatrix.m32() + viewMatrix.m30() * worldModelMatrix.m33(),
                viewMatrix.m01() * worldModelMatrix.m30() + viewMatrix.m11() * worldModelMatrix.m31() + viewMatrix.m21() * worldModelMatrix.m32() + viewMatrix.m31() * worldModelMatrix.m33(),
                viewMatrix.m02() * worldModelMatrix.m30() + viewMatrix.m12() * worldModelMatrix.m31() + viewMatrix.m22() * worldModelMatrix.m32() + viewMatrix.m32() * worldModelMatrix.m33(),
                viewMatrix.m03() * worldModelMatrix.m30() + viewMatrix.m13() * worldModelMatrix.m31() + viewMatrix.m23() * worldModelMatrix.m32() + viewMatrix.m33() * worldModelMatrix.m33()
        )

        return Vector3f(calcLightPos[0], calcLightPos[1], calcLightPos[2])
    }

}