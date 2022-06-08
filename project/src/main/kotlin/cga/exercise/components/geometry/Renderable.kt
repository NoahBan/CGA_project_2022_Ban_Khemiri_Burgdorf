package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class Renderable(
    private val renderList : MutableList<Mesh>, modelMatrix : Matrix4f, parent: Transformable?)
    : Transformable(modelMatrix, parent), IRenderable  {

    fun add (newMesh : Mesh) : Boolean {
        renderList.add(newMesh)
        return true
    }

    override fun render(shaderProgram : ShaderProgram) {
        shaderProgram.setUniform("model_matrix", this.getWorldModelMatrix(), false)
        for (each in renderList){
            each.render()
        }
    }
}