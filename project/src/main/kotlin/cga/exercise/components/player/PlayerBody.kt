package cga.exercise.components.player

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class PlayerBody (playerGeo : PlayerGeo, modelMatrix : Matrix4f, parent: Transformable? = null) : PlayerPart(modelMatrix, parent) {

    var bodyGeo = Renderable(playerGeo.body.renderList, Matrix4f() )

    init {
        bodyGeo.parent = this
    }

    override fun render(shaderProgram : ShaderProgram){
        bodyGeo.render(shaderProgram)
    }
}