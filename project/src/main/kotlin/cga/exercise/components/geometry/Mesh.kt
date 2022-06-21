package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

/**
 * Creates a Mesh object from vertexdata, intexdata and a given set of vertex attributes
 *
 * @param vertexdata plain float array of vertex data
 * @param indexdata  index data
 * @param attributes vertex attributes contained in vertex data
 * @throws Exception If the creation of the required OpenGL objects fails, an exception is thrown
 *
 * Created by Fabian on 16.09.2017.
 */
class Mesh(
    vertexdata: FloatArray,
    indexdata: IntArray,
    attributes: Array<VertexAttribute>,
    private val normalized: Boolean = false,
    private val material : Material? = null
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

    /**
     * renders the mesh
     */
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

    /**
     * Deletes the previously allocated OpenGL objects for this mesh
     */
    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) GL30.glDeleteVertexArrays(vao)
    }
}