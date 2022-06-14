package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f
import org.lwjgl.opengl.GL30

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var shininess: Float = 60.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram) {
        // todo 3.2
        shaderProgram.setUniform("tcMultiplier1", tcMultiplier[0])
        shaderProgram.setUniform("tcMultiplier2", tcMultiplier[1])

        shaderProgram.setUniform("shininess", shininess)
//        shaderProgram.setUniform("texDiff", 0)

//        diff.bind(0)
//        shaderProgram.setUniform("texdDiff", 1)

        emit.bind(GL30.GL_TEXTURE1)
        shaderProgram.setUniform("texEmit", GL30.GL_TEXTURE1)



//        specular.bind(0)
//        shaderProgram.setUniform("texSpec", 1)

    }
}