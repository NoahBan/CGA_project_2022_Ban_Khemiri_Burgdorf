package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.LightHandler
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import cga.framework.ModelLoader
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30
import org.joml.Math


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    private val staticShader : ShaderProgram

    private val importedGround : Renderable

    private val importedBike : Renderable

    private val importedSphere : Renderable
    private val importedLightSphere : Renderable
    private val importedLightSphere2 : Renderable

    private val sceneCam : TronCamera

    private val light1 : PointLight
    private val light2 : PointLight
    private val lightHandler : LightHandler

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
        val groundEmissionTex = Texture2D("assets/textures/ground_emit.png", true)
        groundEmissionTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val matGround = Material(
            groundEmissionTex,
            groundEmissionTex,
            groundEmissionTex,
            60.0f,
            Vector2f(64.0f,64.0f))

        //Sphere Texture
        val sphereDiffuseTex = Texture2D("assets/textures/sphere.png", true)
        val sphereEmissionTex = Texture2D("assets/textures/sphere_emissive.png", true)
        val lightSphereEmissionTex = Texture2D("assets/textures/lightSphereEmissive.png", true)
        groundEmissionTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val matSphere = Material(
                lightSphereEmissionTex,
                sphereEmissionTex,
                sphereDiffuseTex,
                60.0f,
                Vector2f(1f,1f))
        val matLightSphere = Material(
                sphereEmissionTex,
                lightSphereEmissionTex,
                sphereDiffuseTex,
                60.0f,
                Vector2f(1f,1f))



        //Geometry

        //Sphere Geo
        val importObjSphere = OBJLoader.loadOBJ("assets/models/sphere.obj", true)
        val importedSphereData  = importObjSphere.objects[0].meshes[0]
        val importedSphereMesh = Mesh (importedSphereData.vertexData, importedSphereData.indexData, posAndTexcAndNormAttrArray,false, matSphere)
        val importedLightSphereMesh = Mesh (importedSphereData.vertexData, importedSphereData.indexData, posAndTexcAndNormAttrArray,false, matLightSphere)

        //Ground Geo
        val importObjGround = OBJLoader.loadOBJ("assets/models/ground.obj", true)
        val importedGroundData  = importObjGround.objects[0].meshes[0]
        val importedGroundMesh = Mesh (importedGroundData.vertexData, importedGroundData.indexData, posAndTexcAndNormAttrArray,false, matGround)

        importedGround = Renderable(mutableListOf(importedGroundMesh), Matrix4f(), null)
        importedSphere = Renderable(mutableListOf(importedSphereMesh), Matrix4f(), null)
        importedLightSphere = Renderable(mutableListOf(importedLightSphereMesh), Matrix4f(), null)
        importedLightSphere2 = Renderable(mutableListOf(importedLightSphereMesh), Matrix4f(), null)

        importedBike = ModelLoader.loadModel("assets/Light Cycle/HQ_Movie cycle.obj",Math.toRadians(-90f),Math.toRadians(0.0f),Math.toRadians(0f))!!
        importedLightSphere.parent = importedBike
        importedLightSphere2.parent = importedBike

        sceneCam = TronCamera(89F, 16f/9f, 0.1F, 100.0F, Matrix4f(), importedBike)
        sceneCam.rotate(-20F,0F,0F)
        sceneCam.translate(Vector3f(0F,1F,5.0F))

        light1 = PointLight(Vector3f(1F,0F,0F), Matrix4f(), importedLightSphere2)
        light2 = PointLight(Vector3f(0F,0F,1F), Matrix4f(), importedLightSphere)

        importedSphere.translate(Vector3f(0f,2f,-5f))
        importedLightSphere.translate(Vector3f(-2f,2f,0f))
        importedLightSphere.scale(Vector3f(0.05F))
        importedLightSphere2.translate(Vector3f(2f,2f,0f))
        importedLightSphere2.scale(Vector3f(0.05F))

//        light1.translate(Vector3f(-2f,2f,0f))
//        light1.translate(Vector3f(2f,2f,0f))

        lightHandler = LightHandler()

        lightHandler.addLight(light2)
        lightHandler.addLight(light1)


        println(light1.getPremultLightPos(sceneCam.getCalculateViewMatrix()))
        println(light2.getPremultLightPos(sceneCam.getCalculateViewMatrix()))

    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.setUniform("ambientColor", Vector3f(0f))

        staticShader.use()
        sceneCam.bind(staticShader)

        light1.bind(staticShader,sceneCam.getCalculateViewMatrix())
        lightHandler.bindLights(staticShader, sceneCam)

        importedSphere.render(staticShader)
        importedLightSphere.render(staticShader)
        importedLightSphere2.render(staticShader)
        importedGround.render(staticShader)
        importedBike.render(staticShader)

    }

    fun update(dt: Float, t: Float) {
        if(window.getKeyState(GLFW_KEY_W)){
            importedBike.translate(Vector3f(0f,0f,-10f*dt))
        }
        if(window.getKeyState(GLFW_KEY_S)){
            importedBike.translate(Vector3f(0f,0f,10f*dt))
        }
        if(window.getKeyState(GLFW_KEY_A)){
            importedBike.rotate(0f,50f*dt,0f)
        }
        if(window.getKeyState(GLFW_KEY_D)){
            importedBike.rotate(0f,-50f*dt,0f)
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
