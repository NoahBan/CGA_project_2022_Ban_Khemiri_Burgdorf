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
               var emitMultiplier : Vector3f = Vector3f(1F)){

    fun bind(shaderProgram: ShaderProgram) {
        // todo 3.2


        diff.bind(0)
        shaderProgram.setUniform("material.texdDiff", 0)


        emit.bind(1)
        shaderProgram.setUniform("material.texEmit", 1)


        specular.bind(2)
        shaderProgram.setUniform("material.texSpec", 2)

        shaderProgram.setUniform("material.tcMultiplier", tcMultiplier)
        shaderProgram.setUniform("material.shininess", shininess)
    }
}