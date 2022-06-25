package cga.exercise.components.light

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class LightHandler() {
    private val lights = mutableListOf<PointLight>()

    fun addLight (newLight : PointLight){
        lights.add(newLight)
    }

    fun bindLights (shaderProgram : ShaderProgram, camera : TronCamera) {
        shaderProgram.setUniform("lightsArrayLength", lights.size)
        if (lights.size > 10) {
            println("maximum of 10 lights exceeded. Past 10 will be ignored")
            return
        }

        lights.forEachIndexed { index, light ->

            shaderProgram.setUniform("lightsArray[" + index +"].lightPos", light.getPremultLightPos(camera.getCalculateViewMatrix()))
            shaderProgram.setUniform("lightsArray[" + index +"].lightColor", light.lightColor)
            if (index == 9) return
        }

    }

}