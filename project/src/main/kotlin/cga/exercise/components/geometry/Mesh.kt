package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class Mesh(
    vertexdata: FloatArray,
    indexdata: IntArray,
    attributes: Array<VertexAttribute>,
    private val normalized: Boolean = false,
    val material : Material? = null
    ) {

    //private data
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    private var indexcount = indexdata.size

    init {
        // todo: place your code here

        // todo: generate IDs
        vao = GL30.glGenVertexArrays()
        vbo = GL30.glGenBuffers()
        ibo = GL30.glGenBuffers()
        // todo: bind your objects
        GL30.glBindVertexArray(vao)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ibo)

        // todo: upload your mesh data
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexdata, GL30.GL_STATIC_DRAW)
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indexdata, GL30.GL_STATIC_DRAW)

        attributes.forEachIndexed { index, vertAttr ->
            GL30.glEnableVertexAttribArray(index)
            GL30.glVertexAttribPointer(index, vertAttr.n, vertAttr.type, normalized, vertAttr.stride, vertAttr.offset.toLong()
            )
        }

        GL30.glBindVertexArray(0)
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun render() {
        // todo: place your code here
        GL30.glBindVertexArray(vao)
        GL30.glDrawElements(GL30.GL_TRIANGLES, indexcount, GL30.GL_UNSIGNED_INT, 0)
        GL30.glBindVertexArray(0)
        // call the rendering method every frame
    }

    fun render(shaderProgram: ShaderProgram) {
        // todo: place your code here
        material?.bind(shaderProgram)
        render()
    }

    fun setMaterialEmitMul(mult : Vector3f){
        this.material?.emitMultiplier = mult
    }

    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) GL30.glDeleteVertexArrays(vao)
    }
}