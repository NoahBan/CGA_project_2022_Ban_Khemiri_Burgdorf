package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.*
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
    private val importedLightSphere3 : Renderable

    private val sceneCam : TronCamera

    private val light1 : PointLight
    private val light2 : PointLight
    private val spotLight1 : SpotLight

    private val lightHandler : LightHandler

    private val testMatrix = Transformable();

    var xposBefore : Double = 0.0

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
//        val groundDiffuseTex = Texture2D("assets/textures/ground_diff.png", true)
        val groundDiffuseTex = Texture2D("assets/textures/lightSphereEmissive.png", true)
        val groundSpecTex = Texture2D("assets/textures/ground_spec.png", true)

        groundEmissionTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val matGround = Material(
            groundDiffuseTex,
            groundEmissionTex,
            groundSpecTex,
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
        val importObjGround = OBJLoader.loadOBJ("assets/models/ground_blender.obj", true)
        val importedGroundData  = importObjGround.objects[0].meshes[0]
        val importedGroundMesh = Mesh (importedGroundData.vertexData, importedGroundData.indexData, posAndTexcAndNormAttrArray,false, matGround)

        importedGround = Renderable(mutableListOf(importedGroundMesh), Matrix4f(), null)
//        importedGround.scale(Vector3f(100F,1F,100F))
        importedSphere = Renderable(mutableListOf(importedSphereMesh), Matrix4f(), null)
        importedLightSphere = Renderable(mutableListOf(importedLightSphereMesh), Matrix4f(), null)
        importedLightSphere2 = Renderable(mutableListOf(importedLightSphereMesh), Matrix4f(), null)
        importedLightSphere3 = Renderable(mutableListOf(importedLightSphereMesh), Matrix4f(), null)

        importedBike = ModelLoader.loadModel("assets/Light Cycle/HQ_Movie cycle.obj",Math.toRadians(-90f),Math.toRadians(90.0f),Math.toRadians(0f))!!



        sceneCam = TronCamera(70f, 16f/9f, 0.1F, 100.0F, Matrix4f(), importedBike)
        sceneCam.rotate(-20F,0F,0F)
        sceneCam.translate(Vector3f(0F,1F,3.0F))
//        sceneCam.rotate(-90F,0F,0F)
//        sceneCam.setPosition(Vector3f(0f,10f,0f))

        light1 = PointLight(AttenuationType.QUADRATIC,Vector3f(1F,0F,0F), 10F, Matrix4f(), importedBike)
        light2 = PointLight(AttenuationType.QUADRATIC,Vector3f(0F,0F,1F), 10F, Matrix4f(), importedBike)

        spotLight1 = SpotLight(AttenuationType.QUADRATIC,Vector3f(0F,1F, 0F), 100F, Matrix4f(), null,20f,40f, importedBike)
        spotLight1.setPosition(Vector3f(0f,1f,-1.8f))
        spotLight1.rotate(45f,0f,0f)

        importedLightSphere.parent = light1
        importedLightSphere2.parent = light2
        importedLightSphere3.parent = spotLight1

        importedSphere.translate(Vector3f(0f,2f,-4f))

        importedLightSphere.scale(Vector3f(0.05F))
        importedLightSphere2.scale(Vector3f(0.05F))
        importedLightSphere3.scale(Vector3f(0.05F))

        light1.translate(Vector3f(-1f,1f,0f))
        light2.translate(Vector3f(1f,1f,0f))

        lightHandler = LightHandler()

        lightHandler.addPointLight(light2)
        lightHandler.addPointLight(light1)
        lightHandler.addSpotLight(spotLight1)


    }

    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        staticShader.use()
        sceneCam.bind(staticShader)

        lightHandler.bindLights(staticShader, sceneCam, Vector3f(0f))

        importedSphere.render(staticShader)
        importedLightSphere.render(staticShader)
        importedLightSphere2.render(staticShader)
        importedLightSphere3.render(staticShader)
        importedGround.setMaterialEmitMult(Vector3f(0f,1f,0f))
        importedGround.render(staticShader)
        importedBike.setMaterialEmitMult(Vector3f(Math.abs(Math.sin(t)) + 0.2F,Math.abs(Math.sin(t+0.333f)) + 0.2F,Math.abs(Math.sin(t+0.666f)) + 0.2F))
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

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
    }

    fun onMouseMove(xpos: Double, ypos: Double) {
        var xposNew = xpos.minus(xposBefore) * 0.002f
        sceneCam.rotateAroundPoint(0f,- xposNew.toFloat(),0f,importedBike.getWorldPosition())
        xposBefore = xpos
    }

    fun cleanup() {}
}
