package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import java.awt.Image
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import kotlin.math.PI


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram

    private val importedSphere : Renderable
    private val importedGround : Renderable

    private val sceneCam : TronCamera

    private val groundEmissionTex : Texture2D

    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        //staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")

        //initial opengl state
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f); GLError.checkThrow()
        glDisable(GL_CULL_FACE); GLError.checkThrow()
        //glFrontFace(GL_CCW); GLError.checkThrow()
        //glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()


        glEnable ( GL_CULL_FACE )
        glFrontFace ( GL_CCW )
        glCullFace ( GL_BACK )


        val posAndColVaoPos = VertexAttribute (3, GL30.GL_FLOAT,6 * 4, 0)
        val posAndColVaoCol = VertexAttribute (3, GL30.GL_FLOAT,6 * 4, 3 * 4)
        val posAndColAttrArray = arrayOf(posAndColVaoPos, posAndColVaoCol)

        val posAndTexcAndNormPos = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 0)
        val posAndTexcAndNormTexc = VertexAttribute (2, GL30.GL_FLOAT,8 * 4, 3 * 4)
        val posAndTexcAndNormNorm = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 5 * 4)
        val posAndTexcAndNormAttrArray = arrayOf(posAndTexcAndNormPos, posAndTexcAndNormTexc, posAndTexcAndNormNorm)


//MAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAATS

        val buffy = BufferUtils.createIntBuffer(1)
        val bufferding = STBImage.stbi_load("assets/textures/ground_emit.png" , buffy, buffy, buffy, 4)

        groundEmissionTex = Texture2D(bufferding!!,512, 512, false)
        groundEmissionTex.setTexParams(1,1,1,1)

        val mat = Material(groundEmissionTex,
            groundEmissionTex,
            groundEmissionTex,
            60.0f,
            Vector2f(64.0f,64.0f))




//GEEEEEEEEOOOOOOOOOO
//        val importObjSphere = OBJLoader.loadOBJ("assets/models/sphere.obj", true)
        val importObjSphere = OBJLoader.loadOBJ("assets/models/LAAT_gunship.obj", false, true)
        val importedSphereData  = importObjSphere.objects[0].meshes[0]
        val importedSphereMesh = Mesh (importedSphereData.vertexData, importedSphereData.indexData, posAndTexcAndNormAttrArray)


        val importObjGround = OBJLoader.loadOBJ("assets/models/ground.obj", true)
        val importedGroundData  = importObjGround.objects[0].meshes[0]
        val importedGroundMesh = Mesh (importedGroundData.vertexData, importedGroundData.indexData, posAndTexcAndNormAttrArray,false, mat)


        importedGround = Renderable(mutableListOf(importedGroundMesh), Matrix4f(), null)
        importedSphere = Renderable(mutableListOf(importedSphereMesh), Matrix4f(), null)

//        importedGround.parent = importedSphere

//        importedSphere.translate(Vector3f(0F,0F,400.0F))


        sceneCam = TronCamera(90F, 16f/9f, 0.1F, 100.0F, Matrix4f(), importedSphere)
        sceneCam.rotate(-20F,0F,0F)
        sceneCam.translate(Vector3f(0F,0F,4.0F))
        }

    fun render(dt: Float, t: Float) {
        staticShader.use()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        sceneCam.bind(staticShader)

        importedSphere.render(staticShader)
        importedGround.render(staticShader)

    }

    fun update(dt: Float, t: Float) {
        if(window.getKeyState(GLFW_KEY_W)){
            importedSphere.translate(Vector3f(0f,0f,-10f*dt))
        }
        if(window.getKeyState(GLFW_KEY_A)){
            importedSphere.rotate(0f,50f*dt,0f)
        }
        if(window.getKeyState(GLFW_KEY_S)){
            importedSphere.translate(Vector3f(0f,0f,10f*dt))
        }
        if(window.getKeyState(GLFW_KEY_D)){
            importedSphere.rotate(0f,-50f*dt,0f)
        }
        if(window.getKeyState(GLFW_KEY_R)){
            importedSphere.translate(Vector3f(0f,0.1f,0f))
        }
        if(window.getKeyState(GLFW_KEY_F)){
            importedSphere.translate(Vector3f(0f,-0.1f,0f))
        }


    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}

    fun cleanup() {}
}
