package cga.exercise.components.ground

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.shader.ShaderProgram
import cga.framework.ModelLoader
import org.joml.*
import org.lwjgl.opengl.GL30

enum class GroundAniMode (val groundAniMode: Int){
    NONE(0),
    ROTATION(1),
    TEXTURE(0)
}

class Ground(var mode : GroundAniMode) {

    val groundGeo : Renderable
    val groundWireframe : Renderable

    var groundPos = Vector3f(0F, -14010F, 0F)

    init {


        groundGeo = ModelLoader.loadModel(
            "assets/models/Ground/Ground.obj",0f,0f,0f
        )!!
        groundGeo.renderList[0].material!!.diff.setTexParams(
            GL30.GL_REPEAT,
            GL30.GL_REPEAT,
            GL30.GL_LINEAR_MIPMAP_LINEAR,
            GL30.GL_LINEAR_MIPMAP_LINEAR
        )
        groundGeo.setPosition(groundPos)

        groundWireframe = ModelLoader.loadModel(
            "assets/models/Ground/Ground_Wireframe.obj",0f,0f,0f
        )!!
        groundWireframe.setPosition(groundPos)
        groundWireframe.renderList[0].drawPrimitivesAs = GL30.GL_LINES

    }

    fun update(deltaTime: Float, time: Float){
        if (mode == GroundAniMode.ROTATION) groundGeo.rotate(deltaTime+0f,0f,0f)
        if (mode == GroundAniMode.TEXTURE) groundGeo.renderList[0].material!!.tcMultiplier.x += deltaTime
    }

    fun render(shaderProgram : ShaderProgram){
        groundGeo.render(shaderProgram)
//        groundWireframe.render(shaderProgram)
    }

}