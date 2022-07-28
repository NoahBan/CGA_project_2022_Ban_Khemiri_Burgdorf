package cga.exercise.components.collision

import cga.exercise.components.geometry.*
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

class Collider (var radius: Float = 1f, val colorType : String = "Green" , val collisionType : String = "Sphere", modelMatrix : Matrix4f = Matrix4f(), parent: Transformable ?= null) : Transformable(modelMatrix, parent){

    var startMaterial : Material
    val matWhite : Material
    val matBlue : Material
    val matGreen : Material
    val matRed : Material

    val importedMesh : Mesh
    val importedMesh2 : Mesh
    lateinit var collisionObject : Renderable

    var collided : Boolean = false

    init {
        //define ibo
        val posAndTexcAndNormPos = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 0)
        val posAndTexcAndNormTexc = VertexAttribute (2, GL30.GL_FLOAT,8 * 4, 3 * 4)
        val posAndTexcAndNormNorm = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 5 * 4)
        val posAndTexcAndNormAttrArray = arrayOf(posAndTexcAndNormPos, posAndTexcAndNormTexc, posAndTexcAndNormNorm)

        //Textures
        //pureTextures
        val pureBlackTex = Texture2D("assets/textures/pureColor/pureBlack.png", true)
        pureBlackTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val pureBlueTex = Texture2D("assets/textures/pureColor/pureBlue.png", true)
        pureBlueTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val pureGreenTex = Texture2D("assets/textures/pureColor/pureGreen.png", true)
        pureGreenTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val pureGreyTex = Texture2D("assets/textures/pureColor/pureGrey.png", true)
        pureGreyTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val pureRedTex = Texture2D("assets/textures/pureColor/pureRed.png", true)
        pureRedTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val pureWhiteTex = Texture2D("assets/textures/pureColor/pureWhite.png", true)
        pureWhiteTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

        matWhite = Material(
                pureWhiteTex,
                pureWhiteTex,
                pureWhiteTex
        )
        matBlue = Material(
                pureWhiteTex,
                pureBlueTex,
                pureWhiteTex
        )
        matRed = Material(
                pureWhiteTex,
                pureRedTex,
                pureWhiteTex
        )
        matGreen = Material(
                pureWhiteTex,
                pureGreenTex,
                pureWhiteTex
        )

        startMaterial = Material(
                pureWhiteTex,
                pureGreenTex,
                pureWhiteTex
        )

        if(colorType == "White")
            startMaterial = matWhite
        if(colorType == "Blue")
            startMaterial = matBlue
        if(colorType == "Red")
            startMaterial = matRed
        if(colorType == "Green")
            startMaterial = matGreen

        val importObj = OBJLoader.loadOBJ("assets/models/CollisionSphere.obj", true)
        val importedData  = importObj.objects[0].meshes[0]
        importedMesh = Mesh (importedData.vertexData, importedData.indexData, posAndTexcAndNormAttrArray,false, startMaterial)

        val importObj2 = OBJLoader.loadOBJ("assets/models/CollisionObject.obj", true)
        val importedData2  = importObj2.objects[0].meshes[0]
        importedMesh2 = Mesh (importedData2.vertexData, importedData2.indexData, posAndTexcAndNormAttrArray,false, startMaterial)

        if (collisionType == "Sphere") {
            collisionObject = Renderable(mutableListOf(importedMesh), Matrix4f(), this)
            collisionObject.scaling(Vector3f(radius))
        }else if (collisionType == "Box") {
            collisionObject = Renderable(mutableListOf(importedMesh2), Matrix4f(), this)
            collisionObject.scaling(Vector3f(radius))
        }

    }

    fun showCollision(staticShader : ShaderProgram){
        collisionObject.render(staticShader)
    }

    fun setCollidedMat(){
        importedMesh.material = matRed
    }

    fun setStandardMat(){
        if(colorType == "White")
            importedMesh.material = matWhite
        if(colorType == "Blue")
            importedMesh.material = matBlue
        if(colorType == "Red")
            importedMesh.material = matRed
        if(colorType == "Green")
            importedMesh.material = matGreen
    }
}