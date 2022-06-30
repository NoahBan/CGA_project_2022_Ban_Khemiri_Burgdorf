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
        var target : Matrix4f,
        var cutOff : Float,
        var outerCutOff : Float,
        parent: Transformable? = null)
        
        
        // postion vom 


        : PointLight(attenuationType, lightColor, intensity, modelMatrix, parent)
{
        init {
                cutOff = Math.toRadians(cutOff.toDouble()).toFloat()
                outerCutOff = Math.toRadians(outerCutOff.toDouble()).toFloat()
        }

        fun getSpotLightDirection(camera : TronCamera) : Vector3f{
                var pos = Vector3f(getWorldModelMatrix().m30(), getWorldModelMatrix().m31(), getWorldModelMatrix().m32())
                var tar = Vector3f(target.m30(), target.m31(), target.m32())

                var lightDir = pos.sub(tar)
                var lightDir4 = Vector4f(lightDir[0],lightDir[0],lightDir[0],1F)

                var camAxis = camera.getCalculateViewMatrix()

                var mult = lightDir4.mul(camAxis)

//                var mult = Vector4f(
//                        camAxis.m00() * lightDir4[0] + camAxis.m10() * lightDir4[1] + camAxis.m20() * lightDir4[2] + camAxis.m30() * lightDir4[3],
//                        camAxis.m01() * lightDir4[0] + camAxis.m11() * lightDir4[1] + camAxis.m21() * lightDir4[2] + camAxis.m31() * lightDir4[3],
//                        camAxis.m02() * lightDir4[0] + camAxis.m12() * lightDir4[1] + camAxis.m22() * lightDir4[2] + camAxis.m32() * lightDir4[3],
//                        camAxis.m03() * lightDir4[0] + camAxis.m13() * lightDir4[1] + camAxis.m23() * lightDir4[2] + camAxis.m33() * lightDir4[3]
//                )

                println(mult)

                var returner = Vector3f(mult[0], mult[1], mult[2])

                return lightDir
        }
}

// SpotLightTargetDirectionTest = (View_matrix * vec4(SpotLightArrayTest.direction, 0)).xyz;