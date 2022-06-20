package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import cga.framework.ModelLoader
import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.joml.Math


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    private val staticShader : ShaderProgram

    private val importedGround : Renderable

    private val importedBike : Renderable

    private val sceneCam : TronCamera

    private val testCurve : BezierCurve

    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        //staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glDisable(GL_CULL_FACE); GLError.checkThrow()
        //glFrontFace(GL_CCW); GLError.checkThrow()
        //glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()


        glEnable ( GL_CULL_FACE )
        glFrontFace ( GL_CCW )
        glCullFace ( GL_BACK )

        //define ibo
        val posAndColVaoPos = VertexAttribute (3, GL30.GL_FLOAT,6 * 4, 0)
        val posAndColVaoCol = VertexAttribute (3, GL30.GL_FLOAT,6 * 4, 3 * 4)
        val posAndColAttrArray = arrayOf(posAndColVaoPos, posAndColVaoCol)

        val posAndTexcAndNormPos = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 0)
        val posAndTexcAndNormTexc = VertexAttribute (2, GL30.GL_FLOAT,8 * 4, 3 * 4)
        val posAndTexcAndNormNorm = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 5 * 4)
        val posAndTexcAndNormAttrArray = arrayOf(posAndTexcAndNormPos, posAndTexcAndNormTexc, posAndTexcAndNormNorm)

        //Textures




        //Ground Texture
        val groundEmissionTex = Texture2D.invoke("assets/textures/ground_emit.png", true)
        groundEmissionTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

        val matGround = Material(
            groundEmissionTex,
            groundEmissionTex,
            groundEmissionTex,
            60.0f,
            Vector2f(64.0f,64.0f))


        //Geometry

        //Ground Geo
        val importObjGround = OBJLoader.loadOBJ("assets/models/ground.obj", true)
        val importedGroundData  = importObjGround.objects[0].meshes[0]
        val importedGroundMesh = Mesh (importedGroundData.vertexData, importedGroundData.indexData, posAndTexcAndNormAttrArray,false, matGround)

        importedGround = Renderable(mutableListOf(importedGroundMesh), Matrix4f(), null)

        importedBike = ModelLoader.loadModel("assets/Light Cycle/HQ_Movie cycle.obj",Math.toRadians(-90f),Math.toRadians(90.0f),Math.toRadians(0f))!!


        sceneCam = TronCamera(90F, 16f/9f, 0.1F, 100.0F, Matrix4f(), importedBike)
        sceneCam.rotate(-20F,0F,0F)
        sceneCam.translate(Vector3f(0F,0F,6.0F))

        testCurve = BezierCurve(
            listOf(
                Vector3f(0F,0F,0F),
                Vector3f(10F,20F,30F)
            )
        )
    }

    fun render(dt: Float, t: Float) {
        staticShader.use()
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        sceneCam.bind(staticShader)

        importedGround.render(staticShader)
        importedBike.render(staticShader)

        println(testCurve.getMatrixAt(1f))

    }

    fun update(dt: Float, t: Float) {
        if(window.getKeyState(GLFW_KEY_W)){
            importedBike.translate(Vector3f(0f,0f,-10f*dt))
            if(window.getKeyState(GLFW_KEY_A)){
                importedBike.rotate(0f,50f*dt,0f)
            }
            if(window.getKeyState(GLFW_KEY_D)){
                importedBike.rotate(0f,-50f*dt,0f)
            }
        }
        if(window.getKeyState(GLFW_KEY_S)){
            importedBike.translate(Vector3f(0f,0f,10f*dt))
            if(window.getKeyState(GLFW_KEY_A)){
                importedBike.rotate(0f,50f*dt,0f)
            }
            if(window.getKeyState(GLFW_KEY_D)){
                importedBike.rotate(0f,-50f*dt,0f)
            }
        }

        if(window.getKeyState(GLFW_KEY_R)){
            importedBike.translate(Vector3f(0f,0.1f,0f))
        }
        if(window.getKeyState(GLFW_KEY_F)){
            importedBike.translate(Vector3f(0f,-0.1f,0f))
        }


    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}

    fun cleanup() {}
}
