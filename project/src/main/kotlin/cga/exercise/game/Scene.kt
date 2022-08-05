package cga.exercise.game

import cga.exercise.components.player.PlayerObject
import cga.exercise.components.camera.Camera
import cga.exercise.components.camera.CameraHandler
import cga.exercise.components.camera.TargetCamera
import cga.exercise.components.collision.CollisionHandler
import cga.exercise.components.enemy.EnemyHandler
import cga.exercise.components.effects.EmitterHandler
import cga.exercise.components.geometry.*
import cga.exercise.components.ground.Ground
import cga.exercise.components.ground.GroundAniMode
import cga.exercise.components.light.*
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GameWindow
import cga.framework.OBJLoader
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL46
import java.nio.ByteBuffer

val globalLightHandler = LightHandler(30,1,1)
val globalCollisionHandler = CollisionHandler()
val globalEmitterHandler = EmitterHandler()
val globalDepthSortRenderer = DepthSortRenderer()


        /**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    private val baseShader : ShaderProgram

    private val deferredBufferShader : ShaderProgram
    private val deferredLightingShader : ShaderProgram

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
    private val planet : Renderable


    private val cameraHandler = CameraHandler()
    private val followCam : TargetCamera
    private val thirdPersonCam : Camera
    private val topCam : Camera
    private val botCam : Camera

    private val dirLight1 : DirectionalLight

    private val enemyHandler : EnemyHandler


    val buttonPressDelay = 0.5f

    var waitForButtonPress_N_M = 0f
    var waitForButtonPress_G = 0f
    var waitForButtonPress_P = 0f
    var waitForButtonPress_I_O = 0.2f
    var waitForButtonPress_K = 0f
    var waitForButtonPress_C = 0f


    val buttonPressDelay_Space = 0.125f
    var waitForButtonPress_Space = 0f


    val renderQuad : Mesh
    var deferred = false

    var renderCollision = false

    var xposBefore : Double = 0.0

    //scene setup
    init {
        baseShader = ShaderProgram("assets/shaders/baseVertexShdr.glsl", "assets/shaders/baseFragmentShdr.glsl")
        deferredBufferShader = ShaderProgram("assets/shaders/deferredVertShdrBuff.glsl", "assets/shaders/defferedFragShdrBuff.glsl")
        deferredLightingShader = ShaderProgram("assets/shaders/deferredVertShdrLight.glsl", "assets/shaders/defferedFragShdrLight.glsl")

        ///Deff
        gBuffer = GL30.glGenFramebuffers()

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBuffer)

        gPosition = GL30.glGenTextures()
        gNormal = GL30.glGenTextures()
        gColorSpec = GL30.glGenTextures()
        gEmission = GL30.glGenTextures()

        GL30.glBindTexture(GL30.GL_TEXTURE_2D, gPosition)
        GL46.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, window.windowWidth, window.windowHeight, 0, GL30.GL_RGBA, GL30.GL_FLOAT, null as ByteBuffer?)
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


        val skySphereTex = Texture2D("assets/textures/starmap_2020_8k_gal.png",true)
        skySphereTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val matSkySphere = Material(
            pureBlackTex,
            skySphereTex,
            pureBlackTex,
            0f,
            Vector2f(1f,1f),Vector3f(1f,1f,1f),
            0f,
            0f
        )
        //Skybox Geo
        val importObjSkybox = OBJLoader.loadOBJ("assets/models/sky/SkySphere.obj", true)
        val importObjSkyboxData  = importObjSkybox.objects[0].meshes[0]
        val importedKSkyboxMesh = Mesh (importObjSkyboxData.vertexData, importObjSkyboxData.indexData, posAndTexcAndNormAttrArray,false, matSkySphere)
        importedSkySphere = Renderable(mutableListOf(importedKSkyboxMesh), Matrix4f(),null)
        importedSkySphere.scale(Vector3f(1.0f))
        importedSkySphere.rotate(25f,90f,-20f)

        val planetTex = Texture2D("assets/models/sky/planetSurface.png",true)
        skySphereTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val matPlanet = Material(
            pureBlackTex,
            planetTex,
            pureBlackTex,
            0f,
            Vector2f(1f,1f),Vector3f(1f,1f,1f),
            0f,
            0f
        )
        //PLANET
        val importObjPlanet = OBJLoader.loadOBJ("assets/models/sky/planet.obj", true)
        val importObjPlanetData  = importObjPlanet.objects[0].meshes[0]
        val importedPlanetMesh = Mesh (importObjPlanetData.vertexData, importObjPlanetData.indexData, posAndTexcAndNormAttrArray,false, matPlanet)
        planet = Renderable(mutableListOf(importedPlanetMesh), Matrix4f(),null)
        planet.translate(Vector3f(0f,-2000f,-100000f))
        val planetScale = 13000f
        planet.scale(Vector3f(planetScale,planetScale,planetScale))


        ground = Ground(GroundAniMode.ROTATION)

        player = PlayerObject(Matrix4f())

        dirLight1 = DirectionalLight(Vector3f(1f,1f,1f),0.5F, Vector3f(0f,-1f,0f))

        globalLightHandler.addDirectionalLight(dirLight1)

        //Cameras
        followCam = TargetCamera(player,20f, 16f/9f, 0.1F, 200000F, Matrix4f(), null, Vector3f(0f,0f,0f), 0.8f)
        followCam.translate(Vector3f(0F,2F,60.0F))
        cameraHandler.addCamera(followCam)

        thirdPersonCam = Camera(60f, 16f/9f, 0.1F, 200000F, Matrix4f(), player.rollParent)
        thirdPersonCam.translate(Vector3f(0F,1.2F,3F))
        thirdPersonCam.rotate(-6F,0F,0F)
        cameraHandler.addCamera(thirdPersonCam)

        topCam = Camera(90f, 16f/9f, 0.1F, 1000.0F+2.2F, Matrix4f(), player.rollParent)
        topCam.translate(Vector3f(0F,5F,0F))
        topCam.rotate(-90F,0F,0F)

        botCam = Camera(90f, 16f/9f, 0.1F, 1000.0F+2.2F, Matrix4f())
        botCam.translate(Vector3f(0F,-5F,0F))
        botCam.rotate(90F,0F,0F)

        enemyHandler = EnemyHandler()
    }

    fun renderAllGeometry(shaderProgram: ShaderProgram){
            if(!deferred) {
                glDisable(GL_BLEND)
            }
            if(deferred){
                glDisable(GL_BLEND)
                glBlendFunc(GL_SRC_ALPHA, GL_ZERO)
                glDepthFunc(GL_LESS)
            }
            GL30.glDepthMask(false)
            importedSkySphere.render(shaderProgram)
            planet.render(shaderProgram)
            GL30.glDepthMask(true)

            ground.render(shaderProgram)
            if (renderCollision) globalCollisionHandler.render(shaderProgram)
            enemyHandler.render(shaderProgram)
            player.render(shaderProgram, deferred)
            globalEmitterHandler.renderAllEmitter(shaderProgram)

            if(!deferred){
                glEnable(GL_BLEND)
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                glDepthFunc(GL_LESS)
            }
            globalDepthSortRenderer.render(shaderProgram)
    }

    fun render(dt: Float, t: Float, gameWindow : Long) {
        glEnable(GL_DEPTH_TEST)
        glEnable ( GL_CULL_FACE )
        glFrontFace ( GL_CCW )
        glCullFace ( GL_BACK )

        if(!deferred) {
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            baseShader.use()
            cameraHandler.getActiveCamera().bind(baseShader)
            globalLightHandler.bindLights(baseShader, cameraHandler.getActiveCamera(), Vector3f(0.5f), deferred)
            renderAllGeometry(baseShader)
        }
        if(deferred){
            //BUFFER PASS
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, gBuffer)
                glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
                deferredBufferShader.use()
                cameraHandler.getActiveCamera().bind(deferredBufferShader)
                renderAllGeometry(deferredBufferShader)

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0) //RGBA 32F
                glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
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
                globalLightHandler.bindLights(deferredLightingShader, cameraHandler.getActiveCamera(), Vector3f(0.5f), deferred)
                renderQuad.render()
        }
        glfwSwapBuffers(gameWindow)
    }


    fun update(dt: Float, t: Float) {
        planet.rotate(0f,dt/2,dt/6)
        ground.update(dt,t)

        //Move
        if(window.getKeyState(GLFW_KEY_W)){
            player.setMoveUp()
        }
        if(window.getKeyState(GLFW_KEY_A)){
            player.setMoveLeft()
        }
        if(window.getKeyState(GLFW_KEY_S)){
            player.setMoveDown()
        }
        if(window.getKeyState(GLFW_KEY_D)){
            player.setMoveRight()
        }

        //AIM
        if(window.getKeyState(GLFW_KEY_UP)){
            player.targetUpDown(false, dt)
        }
        if(window.getKeyState(GLFW_KEY_DOWN)){
            player.targetUpDown(true, dt)
        }
        if(window.getKeyState(GLFW_KEY_LEFT)){
            player.targetLeftRight(true, dt)
        }
        if(window.getKeyState(GLFW_KEY_RIGHT)){
            player.targetLeftRight(false, dt)
        }
        if(window.getKeyState(GLFW_KEY_R)){
            player.targetReset()
        }

        //Shoot
        if(window.getKeyState(GLFW_KEY_SPACE) && t >= waitForButtonPress_Space){
            waitForButtonPress_Space = t + buttonPressDelay_Space
            player.setShoot()
        }

        //FADENKREUTZ
        if(window.getKeyState(GLFW_KEY_C) && t >= waitForButtonPress_C){
            waitForButtonPress_C = t + buttonPressDelay
            player.toggleFadenkreuz()
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
        if (player.isDead){
            if (cameraHandler.getActiveCamera() != cameraHandler.cameraList[0]) cameraHandler.nextCam()
        }
        importedSkySphere.setPosition(cameraHandler.getActiveCamera().getWorldPosition())
        enemyHandler.update(dt,t, player.getWorldPosition())
        globalCollisionHandler.update()
        globalEmitterHandler.updateAllEmitter(t,dt,cameraHandler.getActiveCamera())
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
