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
               var movingU : Float = 0.0f,
               var movingV : Float = 0.0f
               ){

    fun bind(shaderProgram: ShaderProgram) {
        diff.bind(0)
        shaderProgram.setUniform("Material.texdDiff", 0)
        emit.bind(1)
        shaderProgram.setUniform("Material.texEmit", 1)
        specular.bind(2)
        shaderProgram.setUniform("Material.texSpec", 2)
        shaderProgram.setUniform("Material.tcMultiplier", tcMultiplier)
        shaderProgram.setUniform("Material.shininess", shininess)
        shaderProgram.setUniform("Material.emitMultiplier", emitMultiplier)
        if (movingU == 0f && movingV == 0f)  shaderProgram.setUniform("Material.movingMat", 0)
        else shaderProgram.setUniform("Material.movingMat", 1)
        shaderProgram.setUniform("Material.movingU", movingU)
        shaderProgram.setUniform("Material.movingV", movingV)
    }
}