package cga.exercise.components.light

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f

class LightHandler() {
    private val pointLights = mutableListOf<PointLight>()
    private val spotLights = mutableListOf<SpotLight>()

    fun addPointLight (newLight : PointLight){
        pointLights.add(newLight)
    }

    fun bindLights (shaderProgram : ShaderProgram, camera : TronCamera, ambientLightCol : Vector3f) {

        //bind ambient "light" color
        shaderProgram.setUniform("ambientColor", ambientLightCol)

        bindPointlights(shaderProgram, camera)
        bindSpotlights(shaderProgram, camera)
    }

    fun bindPointlights (shaderProgram : ShaderProgram, camera : TronCamera) {
        if (pointLights.size > 10) {
            println("maximum of 10 lights exceeded. Past 10 will be ignored")
            return
        }

        shaderProgram.setUniform("pointLightArrayLength", pointLights.size)

        pointLights.forEachIndexed { index, light ->
            shaderProgram.setUniform("pointLightArray[" + index +"].lightPos", light.getPremultLightPos(camera.getCalculateViewMatrix()))
            shaderProgram.setUniform("pointLightArray[" + index +"].lightColor", light.lightColor)
            shaderProgram.setUniform("pointLightArray[" + index +"].intensity", light.intensity)
            shaderProgram.setUniform("pointLightArray[" + index +"].attenuationType", light.attenuationType.ordinal)
            if (index == 9) return
        }

    }

    fun bindSpotlights (shaderProgram : ShaderProgram, camera : TronCamera) {
        if (spotLights.size > 10) {
            println("maximum of 10 lights exceeded. Past 10 will be ignored")
            return
        }

        shaderProgram.setUniform("spotLightArrayLength", spotLights.size)

        spotLights.forEachIndexed { index, spotLight ->
            shaderProgram.setUniform("spotLightArray[" + index +"].lightPos", spotLight.getPremultLightPos(camera.getCalculateViewMatrix()))
            shaderProgram.setUniform("spotLightArray[" + index +"].lightColor", spotLight.lightColor)
            shaderProgram.setUniform("spotLightArray[" + index +"].intensity", spotLight.intensity)
            shaderProgram.setUniform("spotLightArray[" + index +"].attenuationType", spotLight.attenuationType.ordinal)
            shaderProgram.setUniform("spotLightArray[" + index +"].direction", spotLight.getSpotLightDirection(camera))
            shaderProgram.setUniform("spotLightArray[" + index +"].cutOff", 1)
            if (index == 9) return
        }
    }

}