package cga.exercise.components.light

import cga.exercise.components.camera.Camera
import cga.exercise.components.geometry.Transformable
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Math

class SpotLight(
        attenuationType : AttenuationType,
        lightColor : Vector3f = Vector3f(1F,1F,1F),
        intensity : Float,
        modelMatrix : Matrix4f = Matrix4f(),
        private var cutOff : Float,
        private var outerCutOff : Float,
        parent: Transformable? = null,
        lightVisible : Boolean = false,
        var target : Transformable = Transformable(Matrix4f()))
        // postion vom
        : PointLight(attenuationType, lightColor, intensity, modelMatrix, parent, lightVisible)
{
        //target ist als Position angegeben. "Richtung" des Spotlights ergibt sich aus Neigung aus Positionsvektor zu Target-Vektor
        init {
                var tempInner = cutOff
                var tempOuter = outerCutOff

                if (tempInner < 1F) tempInner = 1f
                if (tempOuter < tempInner) tempOuter = tempInner + 1

                cutOff = Math.cos(Math.toRadians(tempInner.toDouble())).toFloat()
                outerCutOff = Math.cos(Math.toRadians(tempOuter.toDouble())).toFloat()

                target.parent = this
                target.translate(Vector3f(0f,-1f,0f))
        }

        fun setCutoff(inner : Float, outer : Float){
                var tempInner = inner
                var tempOuter = outer

                if (tempInner < 1F) tempInner = 1f
                if (tempOuter < tempInner) tempOuter = tempInner + 1

                cutOff = Math.cos(Math.toRadians(tempInner.toDouble())).toFloat()
                outerCutOff = Math.cos(Math.toRadians(tempOuter.toDouble())).toFloat()
        }

        fun getCutOff() = cutOff
        fun getOuterCutOff() = outerCutOff

        fun getSpotLightDirection(camera : Camera) : Vector3f{
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