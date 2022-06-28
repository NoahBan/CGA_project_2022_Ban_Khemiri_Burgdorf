package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

class SpotLight(
        attenuationType : AttenuationType,
        lightColor : Vector3f = Vector3f(1F,1F,1F),
        intensity : Float,
        modelMatrix : Matrix4f = Matrix4f(),
        projectionMatrix : Matrix4f = Matrix4f(),
        private var winkelInnen : Float,
        private var winkelAußen : Float,
        parent: Transformable? = null)


        : PointLight(attenuationType, lightColor, intensity, modelMatrix, parent)

{
}