package cga.exercise.components.light

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Math
import org.joml.Vector4f

class SpotLight(
        attenuationType : AttenuationType,
        lightColor : Vector3f = Vector3f(1F,1F,1F),
        intensity : Float,
        modelMatrix : Matrix4f = Matrix4f(),
        var cutOff : Float,
        var outerCutOff : Float,
        parent: Transformable? = null,
        var target : Transformable = Transformable(Matrix4f()))
        // postion vom
        : PointLight(attenuationType, lightColor, intensity, modelMatrix, parent)
{
        //target ist als Position angegeben. "Richtung" des Spotlights ergibt sich aus Neigung aus Positionsvektor zu Target-Vektor
        init {
                cutOff = Math.toRadians(cutOff.toDouble()).toFloat()
                outerCutOff = Math.toRadians(outerCutOff.toDouble()).toFloat()
                target.parent = this
                target.translate(Vector3f(0f,-1f,0f))
        }
        fun getSpotLightDirection(camera : TronCamera) : Vector3f{
                var viewMatrix1 = camera.getCalculateViewMatrix()
                var viewMatrix2 = camera.getCalculateViewMatrix()
                var worldModelMatrix = viewMatrix1.mul(getWorldModelMatrix())
                var targetMatrix = viewMatrix2.mul(target.getWorldModelMatrix())

                var direction = Vector3f(
                        worldModelMatrix.m30()-targetMatrix.m30(),
                        worldModelMatrix.m31()-targetMatrix.m31(),
                        worldModelMatrix.m32()-targetMatrix.m32()
                )

                return direction
        }
}

// SpotLightTargetDirectionTest = (View_matrix * vec4(SpotLightArrayTest.direction, 0)).xyz;