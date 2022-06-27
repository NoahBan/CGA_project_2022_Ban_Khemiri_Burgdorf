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

        //bind point lights
        shaderProgram.setUniform("pointLightArrayLength", pointLights.size)
        if (pointLights.size > 10) {
            println("maximum of 10 lights exceeded. Past 10 will be ignored")
            return
        }
        pointLights.forEachIndexed { index, light ->
            shaderProgram.setUniform("pointLightArray[" + index +"].lightPos", light.getPremultLightPos(camera.getCalculateViewMatrix()))
            shaderProgram.setUniform("pointLightArray[" + index +"].lightColor", light.lightColor)
            if (index == 9) return
        }

    }

}