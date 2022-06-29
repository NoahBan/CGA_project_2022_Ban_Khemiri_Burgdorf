package cga.exercise.components.light

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Math

class SpotLight(
        attenuationType : AttenuationType,
        lightColor : Vector3f = Vector3f(1F,1F,1F),
        intensity : Float,
        modelMatrix : Matrix4f = Matrix4f(),
        projectionMatrix : Matrix4f = Matrix4f(),
        var target : Matrix4f,
        private var winkelInnen : Float,
        private var winkelAußen : Float,
        parent: Transformable? = null)
        
        
        // postion vom 


        : PointLight(attenuationType, lightColor, intensity, modelMatrix, parent)
{
        init {
                winkelInnen = Math.toRadians(winkelInnen.toDouble()).toFloat()
                winkelAußen = Math.toRadians(winkelAußen.toDouble()).toFloat()
        }

        fun getSpotLightDirection(camera : TronCamera) : Vector3f{
                var pos = Vector3f(getWorldModelMatrix().m30(), getWorldModelMatrix().m31(), getWorldModelMatrix().m32())
                var tar = Vector3f(target.m30(), target.m31(), target.m32())
                return pos.sub(tar)
        }
}