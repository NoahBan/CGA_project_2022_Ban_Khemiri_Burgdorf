package cga.exercise.components.collision

import cga.exercise.components.geometry.IRenderable
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f

open class CollisionObject(
    val renderList : MutableList<Mesh>,
    modelMatrix : Matrix4f = Matrix4f(),
    parent: Transformable? = null)

    : Transformable(modelMatrix, parent), IRenderable {

    fun add (newMesh : Mesh) : Boolean {
        renderList.add(newMesh)
        return true
    }


    fun setMaterialEmitMult(mult : Vector3f){
        for (each in renderList){
            each.setMaterialEmitMul(mult)
        }
    }

    override fun render(shaderProgram : ShaderProgram) {
        shaderProgram.setUniform("Model_matrix", getWorldModelMatrix(), false)
        for (each in renderList){
            each.render(shaderProgram)
        }
    }
}