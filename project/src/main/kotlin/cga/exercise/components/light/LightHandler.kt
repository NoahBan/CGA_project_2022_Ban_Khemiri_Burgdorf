package cga.exercise.components.light

import cga.exercise.components.camera.Camera
import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f

class LightHandler(val maxPointLights : Int, val maxSpotLight : Int, val maxDirectionalLight : Int)  {
    private val pointLights = mutableListOf<PointLight>()
    private val spotLights = mutableListOf<SpotLight>()
    private val directionalLights = mutableListOf<DirectionalLight>()

    fun addPointLight (newLight : PointLight){
        pointLights.add(newLight)
    }

    fun removePointLight (pLight : PointLight){
        pointLights.remove(pLight)
    }

    fun addSpotLight (newLight : SpotLight){
        spotLights.add(newLight)
    }

    fun addDirectionalLight(newLight:DirectionalLight){
        directionalLights.add(newLight);
    }

    fun bindLights (shaderProgram : ShaderProgram, camera : Camera, ambientLightCol : Vector3f) {

        //bind ambient "light" color
        shaderProgram.setUniform("AmbientColor", ambientLightCol)

        bindPointlights(shaderProgram, camera)
        bindSpotlights(shaderProgram, camera)
        bindDirectionalLights(shaderProgram,camera)
    }

    fun bindPointlights (shaderProgram : ShaderProgram, camera : Camera) {
        if (pointLights.size > maxPointLights) {
            println(pointLights.size)
            println("maximum of ${maxPointLights} Pointlights exceeded. Past ${maxPointLights} will be ignored")
            return
        }

        shaderProgram.setUniform("PointLightsLength", pointLights.size)

        pointLights.forEachIndexed { index, pointLight ->
            shaderProgram.setUniform("PointLights[" + index +"].lightPos", pointLight.getPremultLightPos(camera.getCalculateViewMatrix()))
            shaderProgram.setUniform("PointLights[" + index +"].lightColor", pointLight.lightColor)
            shaderProgram.setUniform("PointLights[" + index +"].intensity", pointLight.intensity)
            shaderProgram.setUniform("PointLights[" + index +"].attenuationType", pointLight.attenuationType.ordinal)
            if (index >= maxPointLights) return
        }
    }

    fun bindSpotlights (shaderProgram : ShaderProgram, camera : Camera) {
        if (spotLights.size > maxSpotLight) {
            println("maximum of ${maxSpotLight} Spotlights exceeded. Past ${maxSpotLight} will be ignored")
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
            if (index >= maxSpotLight-1) return
        }
    }

    fun bindDirectionalLights(shaderProgram : ShaderProgram, camera : Camera){
        directionalLights.forEachIndexed{ index,directionalLight ->
            shaderProgram.setUniform("DirectionalLights[" + index +"].direction", directionalLight.direction) //
            shaderProgram.setUniform("DirectionalLights[" + index +"].lightColor", directionalLight.lightColor)
            shaderProgram.setUniform("DirectionalLights[" + index +"].intensity", directionalLight.intensity)
            if (index >= maxDirectionalLight) return
        }
    }
}