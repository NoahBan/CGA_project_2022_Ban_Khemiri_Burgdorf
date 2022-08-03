package cga.exercise.game

import cga.exercise.components.player.PlayerObject
import cga.exercise.components.camera.Camera
import cga.exercise.components.camera.CameraHandler
import cga.exercise.components.camera.TargetCamera
import cga.exercise.components.collision.CollisionHandler
import cga.exercise.components.enemy.EnemyHandler
import cga.exercise.components.effects.Emitter
import cga.exercise.components.effects.EmitterHandler
import cga.exercise.components.geometry.*
import cga.exercise.components.ground.Ground
import cga.exercise.components.ground.GroundAniMode
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
import org.lwjgl.opengl.GL46
import java.nio.ByteBuffer

val globalLightHandler = LightHandler(26,1,1)
val globalCollisionHandler = CollisionHandler()
val emitterHandler = EmitterHandler()


        /**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    private val baseShader : ShaderProgram

    private val deferredBufferShader : ShaderProgram
    private val deferredLightingShader : ShaderProgram
//    private val last : ShaderProgram
    var gBuffer : Int
    var gPosition : Int
    var gNormal : Int
    var gColorSpec : Int
    var gEmission : Int
    var attachements : IntArray
    var rboDepth : Int

    private val ground : Ground

    private val player : PlayerObject

    private val importedSkySphere : Renderable


    private val cameraHandler = CameraHandler()
    private val followCam : TargetCamera
    private val thirdPersonCam : Camera
    private val topCam : Camera
    private val botCam : Camera


    private val light1 : PointLight
    private val light2 : PointLight
    private val spotLight1 : SpotLight

    private val dirLight1 : DirectionalLight

    private val enemyHandler : EnemyHandler


    val buttonPressDelay = 0.5f

    var waitForButtonPress_N_M = 0f
    var waitForButtonPress_G = 0f
    var waitForButtonPress_P = 0f
    var waitForButtonPress_I_O = 0.2f
    var waitForButtonPress_K = 0f


    val buttonPressDelay_Space = 0.125f
    var waitForButtonPress_Space = 0f


    val renderQuad : Mesh
    var deferred = false

    var renderCollision = false

    val emitter1 : Emitter

    var xposBefore : Double = 0.0

    //scene setup
    init {
        glEnable(GL_DEPTH_TEST)
        baseShader = ShaderProgram("assets/shaders/baseVertexShdr.glsl", "assets/shaders/baseFragmentShdr.glsl")
        deferredBufferShader = ShaderProgram("assets/shaders/deferredVertShdrBuff.glsl", "assets/shaders/defferedFragShdrBuff.glsl")
        deferredLightingShader = ShaderProgram("assets/shaders/deferredVertShdrLight.glsl", "assets/shaders/defferedFragShdrLight.glsl")
//        last = ShaderProgram("assets/shaders/hoffnungV.glsl", "assets/shaders/hoffnungF.glsl")

        //initial opengl state
//        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        //glFrontFace(GL_CCW); GLError.checkThrow()
        //glCullFace(GL_BACK); GLError.checkThrow()

//        glDepthFunc(GL_LESS)

//        glEnable ( GL_CULL_FACE )
//        glFrontFace ( GL_CCW )
//        glCullFace ( GL_BACK )


        ///Deff
        gBuffer = GL30.glGenFramebuffers()

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBuffer)

        gPosition = GL30.glGenTextures()
        gNormal = GL30.glGenTextures()
        gColorSpec = GL30.glGenTextures()
        gEmission = GL30.glGenTextures()

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, gPosition)
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, window.windowWidth, window.windowHeight, 0, GL30.GL_RGBA, GL30.GL_FLOAT, null as ByteBuffer?)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST)
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, gPosition, 0)

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, gNormal)
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, window.windowWidth, window.windowHeight, 0, GL30.GL_RGBA, GL30.GL_FLOAT, null as ByteBuffer?)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST)
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL30.GL_TEXTURE_2D, gNormal, 0)

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, gColorSpec)
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D,0,GL30.GL_RGBA, window.windowWidth, window.windowHeight,0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, null as ByteBuffer?)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST)
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL30.GL_TEXTURE_2D, gColorSpec, 0)

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, gEmission)
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D,0,GL30.GL_RGBA, window.windowWidth, window.windowHeight,0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, null as ByteBuffer?)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST)
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST)
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT3, GL30.GL_TEXTURE_2D, gEmission, 0)

        attachements = intArrayOf(GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1, GL30.GL_COLOR_ATTACHMENT2, GL30.GL_COLOR_ATTACHMENT3)
        GL30.glDrawBuffers(attachements)

        rboDepth = GL30.glGenRenderbuffers()
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rboDepth)
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT32F, window.windowWidth, window.windowHeight)
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, rboDepth)
        if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) print("Framebuffer not complete")

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)



        ///DEFF





        //define ibo
        val posAndTexcAndNormPos = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 0)
        val posAndTexcAndNormTexc = VertexAttribute (2, GL30.GL_FLOAT,8 * 4, 3 * 4)
        val posAndTexcAndNormNorm = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 5 * 4)
        val posAndTexcAndNormAttrArray = arrayOf(posAndTexcAndNormPos, posAndTexcAndNormTexc, posAndTexcAndNormNorm)

        val tempFloatArr = floatArrayOf(-1f,-1f, 1f, -1f, -1f, 1f, 1f,1f )
        val tempIndexData = intArrayOf(0,1,3,0,3,2)

        val tempAttribute = arrayOf(VertexAttribute (2, GL30.GL_FLOAT,4 * 2, 0))

        renderQuad = Mesh(tempFloatArr, tempIndexData,tempAttribute)

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
        val groundEmissionTex = Texture2D("assets/models/Ground/ground_emit.png", true)
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
            pureBlackTex,
            0f,
            Vector2f(1f,1f),Vector3f(1f,1f,1f),
            0f,
            0f
        )

        //Geometry


        ground = Ground(GroundAniMode.ROTATION)

        //Skybox Geo
        val importObjKarimSkybox = OBJLoader.loadOBJ("assets/models/sky/SkySphere.obj", true)
        val importObjKarimSkyboxData  = importObjKarimSkybox.objects[0].meshes[0]
        val importedKarimSkyboxMesh = Mesh (importObjKarimSkyboxData.vertexData, importObjKarimSkyboxData.indexData, posAndTexcAndNormAttrArray,false, matSkySphere)

        importedSkySphere = Renderable(mutableListOf(importedKarimSkyboxMesh), Matrix4f(),null)
        importedSkySphere.scale(Vector3f(1.0f))

        light1 = PointLight(AttenuationType.QUADRATIC,Vector3f(1F,1F,0F), 20F, Matrix4f(), player.rollParent, true)
        light2 = PointLight(AttenuationType.QUADRATIC,Vector3f(0F,1F,1F), 20F, Matrix4f(), player.rollParent, true)
        dirLight1 = DirectionalLight(Vector3f(1f,1f,1f),0.5F, Vector3f(0f,-1f,0f))
        spotLight1 = SpotLight(AttenuationType.QUADRATIC,Vector3f(1F,1F, 1F), 120F, Matrix4f(), 20f,70f, null)
        spotLight1.setPosition(Vector3f(0f,10f,0f))

        light1.translate(Vector3f(-5f,1f,0f))
        light2.translate(Vector3f(5f,1f,0f))

        globalLightHandler.addDirectionalLight(dirLight1)

        //Cameras
        followCam = TargetCamera(player,20f, 16f/9f, 0.1F, 1000.0F+35F, Matrix4f(), null, Vector3f(0f,0f,0f), 0.8f)
        followCam.translate(Vector3f(0F,2F,40.0F))
        cameraHandler.addCamera(followCam)

        thirdPersonCam = Camera(60f, 16f/9f, 0.1F, 1000.0F+2.2F, Matrix4f(), player.rollParent)
        thirdPersonCam.translate(Vector3f(0F,1.2F,3F))
        thirdPersonCam.rotate(-6F,0F,0F)
        cameraHandler.addCamera(thirdPersonCam)

        topCam = Camera(90f, 16f/9f, 0.1F, 1000.0F+2.2F, Matrix4f(), player.rollParent)
        topCam.translate(Vector3f(0F,5F,0F))
        topCam.rotate(-90F,0F,0F)
//        cameraHandler.addCamera(topCam)

        botCam = Camera(90f, 16f/9f, 0.1F, 1000.0F+2.2F, Matrix4f())
        botCam.translate(Vector3f(0F,-5F,0F))
        botCam.rotate(90F,0F,0F)
//        cameraHandler.addCamera(botCam)

        val explosiveTex = Texture2D("assets/textures/explosive2.png",true)
        skySphereTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

        val explosiveMaterial = Material(
            pureRedTex,
            pureRedTex,
            pureWhiteTex,
            60.0f,
            Vector2f(1f,1f),
            Vector3f(1f),
            0.7f
        )

        emitter1 = Emitter(
            0f,0f,-10f,
            explosiveMaterial,
            1000,
            1f,
            0.3f,
            Vector3f(0f,0.3f,0f),
            Vector3f(0f,0.5f,0f),
            180f,
            0.96f,
            Vector3f(0f,0f,0f),
            0.7f,
            1.3f,
            0.993f,
            0.98f,
            Vector3f(0f,0f,1f),
            0.98f,
            1.5f,
            1
        )
        emitterHandler.addEmitter(emitter1)

        var emitter2 = Emitter(
            -5f, 0f, -10f,
            explosiveMaterial,
            10,
            0.1f,
            0.3f,
            Vector3f(0f, 0.3f, 0f),
            Vector3f(0f, 0.35f, 0f),
            10f,
            0.98f,
            Vector3f(0f, 0f, 0f),
            0.7f,
            1.3f,
            0.993f,
            0.97f,
            Vector3f(0.1f, 0.1f, 0.1f),
            0.9f,
            0.05f
        )
        emitterHandler.addEmitter(emitter2)

        enemyHandler = EnemyHandler()
    }

    fun renderAllGeometry(shaderProgram: ShaderProgram){

        GL30.glDepthMask(false)
        importedSkySphere.render(shaderProgram)
        GL30.glDepthMask(true)
        ground.render(shaderProgram)

        player.render(shaderProgram)
        if (renderCollision) globalCollisionHandler.render(shaderProgram)
        enemyHandler.render(shaderProgram)
//        emitterHandler.renderAllEmitter(shaderProgram)
    }

    fun render(dt: Float, t: Float, gameWindow : Long) {

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        if(!deferred) {
            baseShader.use()
            cameraHandler.getActiveCamera().bind(baseShader)
            globalLightHandler.bindLights(baseShader, cameraHandler.getActiveCamera(), Vector3f(0.5f))
            renderAllGeometry(baseShader)
        }

        if(deferred){
            //BUFFER PASS
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBuffer)
                glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
                deferredBufferShader.use()
                cameraHandler.getActiveCamera().bind(deferredBufferShader)
                renderAllGeometry(deferredBufferShader)
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0) //RGBA 32F
                deferredLightingShader.use()
                GL30.glActiveTexture(GL30.GL_TEXTURE0)
                GL30.glBindTexture(GL30.GL_TEXTURE_2D, gPosition)
                GL30.glActiveTexture(GL30.GL_TEXTURE1)
                GL30.glBindTexture(GL30.GL_TEXTURE_2D, gNormal)
                GL30.glActiveTexture(GL30.GL_TEXTURE2)
                GL30.glBindTexture(GL30.GL_TEXTURE_2D, gColorSpec)
                GL30.glActiveTexture(GL30.GL_TEXTURE3)
                GL30.glBindTexture(GL30.GL_TEXTURE_2D, gEmission)
                cameraHandler.getActiveCamera().bind(deferredLightingShader)
                globalLightHandler.bindLights(deferredLightingShader, cameraHandler.getActiveCamera(), Vector3f(0.5f))
                renderQuad.render()
        }
        glfwSwapBuffers(gameWindow)
    }


    fun update(dt: Float, t: Float) {
        ground.update(dt,t)

        //Move
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

        //Shoot
        if(window.getKeyState(GLFW_KEY_SPACE) && t >= waitForButtonPress_Space){
            waitForButtonPress_Space = t + buttonPressDelay_Space
            player.setShoot()
        }

        //WingsOut
        if(window.getKeyState(GLFW_KEY_G) && t >= waitForButtonPress_G){
            waitForButtonPress_G = t + buttonPressDelay
            player.toggleWingMode()
        }

        //Switch Camera/Perspective
        if(window.getKeyState(GLFW_KEY_N) && t >= waitForButtonPress_N_M){
            waitForButtonPress_N_M = t + buttonPressDelay
            cameraHandler.prevCam()
        }
        if(window.getKeyState(GLFW_KEY_M) && t >= waitForButtonPress_N_M){
            waitForButtonPress_N_M = t + buttonPressDelay
            cameraHandler.nextCam()
        }

        //CameraZoom
        if (window.getKeyState(GLFW_KEY_I) && t >= waitForButtonPress_I_O && cameraHandler.getActiveCamera() == cameraHandler.cameraList[1]){
            var newfov = 0.1f
            if (cameraHandler.getActiveCamera().fov > 20f) {
                cameraHandler.getActiveCamera().fov -= t * newfov
            }
        }
        //CameraZoom
        if (window.getKeyState(GLFW_KEY_O) && t >= waitForButtonPress_I_O && cameraHandler.getActiveCamera() == cameraHandler.cameraList[1]){
            var newfov = 0.1f
            if (cameraHandler.getActiveCamera().fov < 60f) {
                cameraHandler.getActiveCamera().fov += t * newfov
            }
        }

        //Render Collision Switch
        if(window.getKeyState(GLFW_KEY_K) && t >= waitForButtonPress_K){
            waitForButtonPress_K = t + buttonPressDelay
            renderCollision = !renderCollision
        }

        //Switch Shaderprogram
        if(window.getKeyState(GLFW_KEY_P) && t >= waitForButtonPress_P){
            waitForButtonPress_P = t + buttonPressDelay
            deferred = !deferred
        }

        player.update(dt,t)
        importedSkySphere.setPosition(cameraHandler.getActiveCamera().getWorldPosition())
        enemyHandler.update(dt,t)
        globalCollisionHandler.update()
        emitterHandler.updateAllEmitter(t,dt,cameraHandler.getActiveCamera())
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
