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

    fun addSpotLight (newLight : SpotLight){
        spotLights.add(newLight)
    }

    fun bindLights (shaderProgram : ShaderProgram, camera : TronCamera, ambientLightCol : Vector3f) {

        //bind ambient "light" color
        shaderProgram.setUniform("AmbientColor", ambientLightCol)

        bindPointlights(shaderProgram, camera)
//        bindSpotlights(shaderProgram, camera)
//        bindSpotlightsTest(shaderProgram, camera)
    }

    fun bindPointlights (shaderProgram : ShaderProgram, camera : TronCamera) {
        if (pointLights.size > 5) {
            println("maximum of 10 lights exceeded. Past 5 will be ignored")
            return
        }

        shaderProgram.setUniform("PointLightsLength", pointLights.size)

        pointLights.forEachIndexed { index, pointLight ->
            shaderProgram.setUniform("PointLights[" + index +"].lightPos", pointLight.getPremultLightPos(camera.getCalculateViewMatrix()))
            shaderProgram.setUniform("PointLights[" + index +"].lightColor", pointLight.lightColor)
            shaderProgram.setUniform("PointLights[" + index +"].intensity", pointLight.intensity)
            shaderProgram.setUniform("PointLights[" + index +"].attenuationType", pointLight.attenuationType.ordinal)
            if (index == 4) return
        }
    }

    fun bindSpotlights (shaderProgram : ShaderProgram, camera : TronCamera) {
        if (spotLights.size > 5) {
            println("maximum of 10 lights exceeded. Past 5 will be ignored")
            return
        }

        shaderProgram.setUniform("SpotLightArrayLength", spotLights.size)

        spotLights.forEachIndexed { index, spotLight ->
            shaderProgram.setUniform("SpotLightArray[" + index + "].lightPos", spotLight.getWorldPosition())
            shaderProgram.setUniform("SpotLightArray[" + index + "].lightColor", spotLight.lightColor)
            shaderProgram.setUniform("SpotLightArray[" + index + "].intensity", spotLight.intensity)
            shaderProgram.setUniform("SpotLightArray[" + index + "].attenuationType", spotLight.attenuationType.ordinal)
            shaderProgram.setUniform("SpotLightArray[" + index + "].direction", spotLight.getSpotLightDirection(camera))
            shaderProgram.setUniform("SpotLightArray[" + index + "].cutOff", spotLight.cutOff)
            shaderProgram.setUniform("SpotLightArray[" + index + "].outerCutOff", spotLight.outerCutOff)
            if (index == 4) return
        }
    }
}