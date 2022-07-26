package cga.exercise.components.light

import cga.exercise.components.camera.Camera
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

    fun bindLights (shaderProgram : ShaderProgram, camera : Camera, ambientLightCol : Vector3f) {

        //bind ambient "light" color
        shaderProgram.setUniform("AmbientColor", ambientLightCol)

        bindPointlights(shaderProgram, camera)
        bindSpotlights(shaderProgram, camera)
    }

    fun bindPointlights (shaderProgram : ShaderProgram, camera : Camera) {
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
            if(pointLight.lightVisible) pointLight.lightSphere.render(shaderProgram)
            if (index == 4) return
        }
    }

    fun bindSpotlights (shaderProgram : ShaderProgram, camera : Camera) {
        if (spotLights.size > 5) {
            println("maximum of 10 lights exceeded. Past 5 will be ignored")
            return
        }

        shaderProgram.setUniform("SpotLightsLength", spotLights.size)

        spotLights.forEachIndexed { index, spotLight ->
            shaderProgram.setUniform("SpotLights[" + index + "].lightPos", spotLight.getPremultLightPos(camera.getCalculateViewMatrix()))
            shaderProgram.setUniform("SpotLights[" + index + "].lightColor", spotLight.lightColor)
            shaderProgram.setUniform("SpotLights[" + index + "].intensity", spotLight.intensity)
            shaderProgram.setUniform("SpotLights[" + index + "].attenuationType", spotLight.attenuationType.ordinal)
            shaderProgram.setUniform("SpotLights[" + index + "].direction", spotLight.getSpotLightDirection(camera))
            shaderProgram.setUniform("SpotLights[" + index + "].cutOff", spotLight.getCutOff())
            shaderProgram.setUniform("SpotLights[" + index + "].outerCutOff", spotLight.getOuterCutOff())
            if (index == 4) return
        }
    }
}