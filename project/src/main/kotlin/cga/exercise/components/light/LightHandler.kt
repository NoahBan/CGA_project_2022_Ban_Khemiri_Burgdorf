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
        bindSpotlights(shaderProgram, camera)
        bindSpotlightsTest(shaderProgram, camera)
    }

    fun bindPointlights (shaderProgram : ShaderProgram, camera : TronCamera) {
        if (pointLights.size > 10) {
            println("maximum of 10 lights exceeded. Past 10 will be ignored")
            return
        }

        shaderProgram.setUniform("PointLightArrayLength", pointLights.size)

        pointLights.forEachIndexed { index, light ->
            shaderProgram.setUniform("PointLightArray[" + index +"].lightPos", light.getWorldPosition())
            shaderProgram.setUniform("PointLightArray[" + index +"].lightColor", light.lightColor)
            shaderProgram.setUniform("PointLightArray[" + index +"].intensity", light.intensity)
            shaderProgram.setUniform("PointLightArray[" + index +"].attenuationType", light.attenuationType.ordinal)
            if (index == 9) return
        }
    }

    fun bindSpotlights (shaderProgram : ShaderProgram, camera : TronCamera) {
        if (spotLights.size > 10) {
            println("maximum of 10 lights exceeded. Past 10 will be ignored")
            return
        }

        shaderProgram.setUniform("SpotLightArrayLength", spotLights.size)

        spotLights.forEachIndexed { index, spotLight ->
            shaderProgram.setUniform("SpotLightArray[" + index +"].lightPos", spotLight.getWorldPosition())
            shaderProgram.setUniform("SpotLightArray[" + index +"].lightColor", spotLight.lightColor)
            shaderProgram.setUniform("SpotLightArray[" + index +"].intensity", spotLight.intensity)
            shaderProgram.setUniform("SpotLightArray[" + index +"].attenuationType", spotLight.attenuationType.ordinal)
            shaderProgram.setUniform("SpotLightArray[" + index +"].direction", spotLight.getSpotLightDirection(camera))
            shaderProgram.setUniform("SpotLightArray[" + index +"].cutOff", spotLight.cutOff)
            if (index == 9) return
        }
    }

    fun bindSpotlightsTest (shaderProgram : ShaderProgram, camera : TronCamera) {
        if (spotLights.size > 10) {
            println("maximum of 10 lights exceeded. Past 10 will be ignored")
            return
        }

        shaderProgram.setUniform("SpotLightArrayLength", spotLights.size)

        spotLights.forEachIndexed { index, spotLight ->
            shaderProgram.setUniform("SpotLightArrayTest.lightPos", spotLight.getWorldPosition())
            shaderProgram.setUniform("SpotLightArrayTest.lightColor", spotLight.lightColor)
            shaderProgram.setUniform("SpotLightArrayTest.intensity", spotLight.intensity)
            shaderProgram.setUniform("SpotLightArrayTest.attenuationType", spotLight.attenuationType.ordinal)
            shaderProgram.setUniform("SpotLightArrayTest.direction", spotLight.getSpotLightDirection(camera))
            shaderProgram.setUniform("SpotLightArrayTest.cutOff", spotLight.cutOff)
            shaderProgram.setUniform("SpotLightArrayTest.outerCutOff", spotLight.outerCutOff)
            if (index == 9) return
        }
    }

}