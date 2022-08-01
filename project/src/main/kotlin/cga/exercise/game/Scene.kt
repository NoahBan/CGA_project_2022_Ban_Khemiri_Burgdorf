package cga.exercise.game

import cga.exercise.components.player.PlayerObject
import cga.exercise.components.camera.Camera
import cga.exercise.components.camera.CameraHandler
import cga.exercise.components.camera.TargetCamera
import cga.exercise.components.effects.ParticleSystem
import cga.exercise.components.geometry.*
import cga.exercise.components.light.*
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30

val globalLightHandler = LightHandler()
val cameraHandler = CameraHandler()

/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    private val staticShader : ShaderProgram

    private val ground : Renderable

    private val player : PlayerObject

    private val importedLightSphere : Renderable
    private val importedLightSphere2 : Renderable
    private val importedLightSphere3 : Renderable
    private val importedSkySphere : Renderable

    private val followCam : TargetCamera
    private val thirdPersonCam : Camera
    private val topCam : Camera
    private val botCam : Camera


    private val light1 : PointLight
    private val light2 : PointLight
    private val spotLight1 : SpotLight

    private val dirLight1 : DirectionalLight

    private val lightHandler : LightHandler

    val buttonPressDelay = 0.5f
    var waitForButtonPress_CameraSwitch = 0f
    var waitForButtonPress_ToggleWeapon = 0f

    val particleSystem1 : ParticleSystem

    var xposBefore : Double = 0.0

    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/vertexShdr.glsl", "assets/shaders/fragmentShdr.glsl")

        //initial opengl state
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glDisable(GL_CULL_FACE); GLError.checkThrow()
        //glFrontFace(GL_CCW); GLError.checkThrow()
        //glCullFace(GL_BACK); GLError.checkThrow()
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthFunc(GL_LESS); GLError.checkThrow()

        glEnable ( GL_CULL_FACE )
        glFrontFace ( GL_CCW )
        glCullFace ( GL_BACK )

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

        //Ground Texture
        val groundEmissionTex = Texture2D("assets/models/Ground/ground_emit2.png", true)
        groundEmissionTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

        //Sphere Texture
        val sphereDiffuseTex = Texture2D("assets/textures/sphere.png", true)
        sphereDiffuseTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val sphereEmissionTex = Texture2D("assets/textures/sphere_emissive.png", true)
        sphereEmissionTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val lightSphereEmissionTex = Texture2D("assets/textures/lightSphereEmissive.png", true)
        lightSphereEmissionTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val skySphereTex = Texture2D("assets/textures/starmap_2020_4k_gal.png",true)
        skySphereTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

        player = PlayerObject(Matrix4f())

        val matSphere = Material(
            pureWhiteTex,
            pureBlackTex,
            pureWhiteTex
        )
        val matLightSphere = Material(
            pureWhiteTex,
            pureWhiteTex,
            pureWhiteTex
        )
        val matSkySphere = Material(
            pureBlackTex,
            skySphereTex,
            pureBlackTex
        )

        //Geometry

        //Sphere Geo
        val importObjSphere = OBJLoader.loadOBJ("assets/models/sphere.obj", true)
        val importedSphereData  = importObjSphere.objects[0].meshes[0]
        val importedSphereMesh = Mesh (importedSphereData.vertexData, importedSphereData.indexData, posAndTexcAndNormAttrArray,false, matSphere)
        val importedLightSphereMesh = Mesh (importedSphereData.vertexData, importedSphereData.indexData, posAndTexcAndNormAttrArray,false, matLightSphere)

        //Ground Geo
        val matGround = Material(
            groundEmissionTex,
            pureBlackTex,
            pureWhiteTex,
            60.0f,
            Vector2f(64.0f,64.0f),
            Vector3f(1f),
            1f,
            0.95f
        )
        val importObjGround = OBJLoader.loadOBJ("assets/models/Ground/Ground.obj", true)
        val importedGroundData  = importObjGround.objects[0].meshes[0]
        val importedGroundMesh = Mesh (importedGroundData.vertexData, importedGroundData.indexData, posAndTexcAndNormAttrArray,false, matGround)
        ground = Renderable(mutableListOf(importedGroundMesh), Matrix4f(), null)
        ground.scale(Vector3f(200f,10f,800f))
        ground.setPosition(Vector3f(0F,-13F,0F))
        ground.rotate(0f,90f,0f)

        //Skybox Geo
        val importObjKarimSkybox = OBJLoader.loadOBJ("assets/models/SkySphere.obj", true)
        val importObjKarimSkyboxData  = importObjKarimSkybox.objects[0].meshes[0]
        val importedKarimSkyboxMesh = Mesh (importObjKarimSkyboxData.vertexData, importObjKarimSkyboxData.indexData, posAndTexcAndNormAttrArray,false, matSkySphere)

        importedSkySphere = Renderable(mutableListOf(importedKarimSkyboxMesh), Matrix4f(),null)
        importedSkySphere.scale(Vector3f(1.0f))

        light1 = PointLight(AttenuationType.QUADRATIC,Vector3f(1F,1F,0F), 20F, Matrix4f(), player.rollParent, true)
        light2 = PointLight(AttenuationType.QUADRATIC,Vector3f(0F,1F,1F), 20F, Matrix4f(), player.rollParent, true)
        dirLight1 = DirectionalLight(Vector3f(0.8f,0.5f,0.1f),1F, Vector3f(0f,-1f,0f))
        spotLight1 = SpotLight(AttenuationType.QUADRATIC,Vector3f(1F,1F, 1F), 120F, Matrix4f(), 20f,70f, null)
        spotLight1.setPosition(Vector3f(0f,10f,0f))

        importedLightSphere = Renderable(mutableListOf(importedLightSphereMesh), Matrix4f(), light1)
        importedLightSphere2 = Renderable(mutableListOf(importedLightSphereMesh), Matrix4f(), light2)
        importedLightSphere3 = Renderable(mutableListOf(importedLightSphereMesh), Matrix4f(), spotLight1)


        importedLightSphere.scale(Vector3f(0.05F))
        importedLightSphere2.scale(Vector3f(0.05F))
        importedLightSphere3.scale(Vector3f(0.05F))

        light1.translate(Vector3f(-5f,1f,0f))
        light2.translate(Vector3f(5f,1f,0f))

        lightHandler = LightHandler()
        lightHandler.addDirectionalLight(dirLight1)
        lightHandler.addPointLight(light2)
        lightHandler.addPointLight(light1)
        lightHandler.addSpotLight(spotLight1)

        globalLightHandler.addDirectionalLight(dirLight1)
//        globalLightHandler.addPointLight(light2)
//        globalLightHandler.addPointLight(light1)
//        globalLightHandler.addSpotLight(spotLight1)


        //Cameras
        followCam = TargetCamera(player,20f, 16f/9f, 0.1F, 1000.0F+35F, Matrix4f(), null, Vector3f(0f,0f,0f), 0.8f)
        followCam.translate(Vector3f(0F,2F,40.0F))
        cameraHandler.addCamera(followCam)

        thirdPersonCam = Camera(60f, 16f/9f, 0.1F, 1000.0F+2.2F, Matrix4f(), player.rollParent)
        thirdPersonCam.translate(Vector3f(0F,1.2F,3F))
        thirdPersonCam.rotate(-6F,0F,0F)
        cameraHandler.addCamera(thirdPersonCam)

        topCam = Camera(90f, 16f/9f, 0.1F, 1000.0F+2.2F, Matrix4f())
        topCam.translate(Vector3f(0F,5F,0F))
        topCam.rotate(-90F,0F,0F)
//        cameraHandler.addCamera(topCam)

        botCam = Camera(90f, 16f/9f, 0.1F, 1000.0F+2.2F, Matrix4f())
        botCam.translate(Vector3f(0F,-5F,0F))
        botCam.rotate(90F,0F,0F)
//        cameraHandler.addCamera(botCam)


        val matMaterial = Material(
            pureWhiteTex,
            pureWhiteTex,
            pureWhiteTex
        )

        particleSystem1 = ParticleSystem(0f,0f,-10f,matMaterial,1000,0.10f,0.30f)

    }

    fun render(dt: Float, t: Float) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        staticShader.use()
        cameraHandler.getActiveCamera().bind(staticShader)
        globalLightHandler.bindLights(staticShader, cameraHandler.getActiveCamera(), Vector3f(0.0f))

        GL30.glDepthMask(false)
        importedSkySphere.render(staticShader)
        GL30.glDepthMask(true)

//        importedSphere.render(staticShader)
//        importedLightSphere.render(staticShader)
//        importedLightSphere2.render(staticShader)
//        importedLightSphere3.render(staticShader)
        ground.render(staticShader)

        player.render(staticShader)

        particleSystem1.render(staticShader)
    }

    fun update(dt: Float, t: Float) {
        ground.renderList[0].material!!.movingV += dt.toFloat() * 10f

        if(window.getKeyState(GLFW_KEY_W)){
            player.setMoveUp()
        }
        if(window.getKeyState(GLFW_KEY_S)){
            player.setMoveDown()
        }
        if(window.getKeyState(GLFW_KEY_A)){
            player.setMoveLeft()
        }
        if(window.getKeyState(GLFW_KEY_D)){
            player.setMoveRight()
        }

        if(window.getKeyState(GLFW_KEY_G) && t >= waitForButtonPress_ToggleWeapon){
            waitForButtonPress_ToggleWeapon = t + buttonPressDelay
            player.toggleWingMode()
        }

        if(window.getKeyState(GLFW_KEY_N) && t >= waitForButtonPress_CameraSwitch){
            waitForButtonPress_CameraSwitch = t + buttonPressDelay
            cameraHandler.prevCam()
        }
        if(window.getKeyState(GLFW_KEY_M) && t >= waitForButtonPress_CameraSwitch){
            waitForButtonPress_CameraSwitch = t + buttonPressDelay
            cameraHandler.nextCam()
        }
        player.update(dt,t)
        importedSkySphere.setPosition(cameraHandler.getActiveCamera().getWorldPosition())

        particleSystem1.update()
    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {
    }

    fun onMouseMove(xpos: Double, ypos: Double) {
//        var xposNew = xpos.minus(xposBefore) * 0.002f
//        sceneCam.rotateAroundPoint(0f,- xposNew.toFloat(),0f,importedBike.getWorldPosition())
//        xposBefore = xpos
    }

    fun cleanup() {}
}
