package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector2f
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
    var material : Material? = null,
    var drawPrimitivesAs : Int = GL30.GL_TRIANGLES,
    init : Boolean = true
    ) {

    //private data
    private var vao = 0
    private var vbo = 0
    private var ibo = 0
    private var indexcount = indexdata.size

    init {
        if (init) {
            // generate IDs
            vao = GL30.glGenVertexArrays()
            vbo = GL30.glGenBuffers()
            ibo = GL30.glGenBuffers()
            // bind objects
            GL30.glBindVertexArray(vao)
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo)
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ibo)

            // upload mesh data
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexdata, GL30.GL_STATIC_DRAW)
            GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indexdata, GL30.GL_STATIC_DRAW)

            attributes.forEachIndexed { index, vertAttr ->
                GL30.glEnableVertexAttribArray(index)
                GL30.glVertexAttribPointer(
                    index, vertAttr.n, vertAttr.type, normalized, vertAttr.stride, vertAttr.offset.toLong()
                )
            }

            GL30.glBindVertexArray(0)
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0)
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0)
        }
    }

    fun render() {

        GL30.glBindVertexArray(vao)
        GL30.glDrawElements(drawPrimitivesAs, indexcount, GL30.GL_UNSIGNED_INT, 0)
        GL30.glBindVertexArray(0)

    }

    fun render(shaderProgram: ShaderProgram) {

        material?.bind(shaderProgram)
        render()
    }

    fun setMaterialEmitMul(mult : Vector3f){
        this.material?.emitMultiplier = mult
    }

    fun getMeshCopyMaterial() : Mesh{

        val newdShininess = this.material!!.shininess + 0f
        val newdTcMultiplier = Vector2f(this.material!!.tcMultiplier)
        val newdEmitMultiplier = Vector3f(this.material!!.emitMultiplier)
        val newdOpacityMultiplier = this.material!!.opacityMultiplier + 0f
        val newdOpacity = this.material!!.opacity + 0f
        val newdMovingU = this.material!!.movingU + 0f
        val newdMovingV = this.material!!.movingV + 0f

        val newMaterial = Material(
            this.material!!.diff,
            this.material!!.emit,
            this.material!!.specular,
            newdShininess,
            newdTcMultiplier,
            newdEmitMultiplier,
            newdOpacityMultiplier,
            newdOpacity,
            newdMovingU,
            newdMovingV
        )

        var returnMesh = Mesh(
            floatArrayOf(),
            intArrayOf(),
            arrayOf(),
            this.normalized,
            this.material,
            this.drawPrimitivesAs,
            false
        )

        returnMesh.ibo = this.ibo
        returnMesh.vao = this.vao
        returnMesh.vbo = this.vbo
        returnMesh.indexcount = this.indexcount
        returnMesh.material = newMaterial

        return returnMesh
    }

    fun cleanup() {
        if (ibo != 0) GL15.glDeleteBuffers(ibo)
        if (vbo != 0) GL15.glDeleteBuffers(vbo)
        if (vao != 0) GL30.glDeleteVertexArrays(vao)
    }
}