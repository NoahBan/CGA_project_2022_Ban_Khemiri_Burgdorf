package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var shininess: Float = 60.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f),
               var emitMultiplier : Vector3f = Vector3f(1F),
               var opacityMultiplier : Float = 1.0f,
               var opacity : Float = 1.0f,
               var movingU : Float = 0.0f,
               var movingV : Float = 0.0f
               ){

    var scalingColor = Vector3f(1f)
    var colorScaling = 1.0f
    var flatOpacity = 0

        init {
            if (opacity != 1.0f){
                flatOpacity = 1
            }
        }

    fun bind(shaderProgram: ShaderProgram) {
        diff.bind(0)
        shaderProgram.setUniform("Material.texdDiff", 0)
        emit.bind(1)
        shaderProgram.setUniform("Material.texEmit", 1)
        specular.bind(2)
        shaderProgram.setUniform("Material.texSpec", 2)
        shaderProgram.setUniform("Material.tcMultiplier", tcMultiplier)
        shaderProgram.setUniform("Material.shininess", shininess)
        shaderProgram.setUniform("Material.opacityMultiplier", opacityMultiplier)
        shaderProgram.setUniform("Material.opacity", opacity)
        shaderProgram.setUniform("Material.flatOpacity", flatOpacity)
        shaderProgram.setUniform("Material.emitMultiplier", emitMultiplier)
        shaderProgram.setUniform("Material.scalingColor", scalingColor)
        shaderProgram.setUniform("Material.colorScaling", colorScaling)
        if (movingU == 0f && movingV == 0f)  shaderProgram.setUniform("Material.movingMat", 0)
        else shaderProgram.setUniform("Material.movingMat", 1)
        shaderProgram.setUniform("Material.movingU", movingU)
        shaderProgram.setUniform("Material.movingV", movingV)
    }
}