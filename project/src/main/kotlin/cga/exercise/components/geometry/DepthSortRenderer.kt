package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram

class DepthSortRenderer {

    var renderList = mutableListOf<Renderable>()


    fun addRenderable(newRenderable: Renderable) = renderList.add(newRenderable)

    fun render (shaderProgram: ShaderProgram){
        renderList.sortBy { it.getWorldPosition().z }
        for (each in renderList) each.render(shaderProgram)
        renderList = mutableListOf<Renderable>()
    }

}