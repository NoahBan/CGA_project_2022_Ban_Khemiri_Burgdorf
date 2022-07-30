package cga.exercise.game

import cga.exercise.components.player.PlayerObject
import cga.exercise.components.camera.Camera
import cga.exercise.components.camera.CameraHandler
import cga.exercise.components.camera.TargetCamera
import cga.exercise.components.collision.CollisionHandler
import cga.exercise.components.enemy.EnemyHandler
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

val globalLightHandler = LightHandler(30,1,1)
val globalCollisionHandler = CollisionHandler()

        /**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    private val baseShader : ShaderProgram

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

    val buttonPressDelay_Space = 0.125f
    var waitForButtonPress_Space = 0f

    var deferred = false

    var xposBefore : Double = 0.0

    //scene setup
    init {
        baseShader = ShaderProgram("assets/shaders/vertexShdr.glsl", "assets/shaders/fragmentShdr.glsl")
//        baseShader = ShaderProgram("assets/shaders/vertexTestShdr.glsl", "assets/shaders/fragmentTestShdr.glsl", "assets/shaders/geometryTestShdr.glsl")

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
            0f,
            1
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

//        testCollision = Collider(ColliderType.ENEMYCOLLIDER,5f)
//        testCollision.translate(Vector3f(0f,5f,-14f))

        enemyHandler = EnemyHandler()
    }

    fun render(dt: Float, t: Float) {
        if(!deferred) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            baseShader.use()
            cameraHandler.getActiveCamera().bind(baseShader)
            globalLightHandler.bindLights(baseShader, cameraHandler.getActiveCamera(), Vector3f(0.5f))

            GL30.glDepthMask(false)
            importedSkySphere.render(baseShader)
            GL30.glDepthMask(true)

            ground.render(baseShader)

            player.render(baseShader)
//            testCollision.render(baseShader)
//            globalCollisionHandler.render(baseShader)
            enemyHandler.render(baseShader)



        }
        if(deferred){

        }
    }


    fun update(dt: Float, t: Float) {
        ground.update(dt,t)

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

        if(window.getKeyState(GLFW_KEY_SPACE) && t >= waitForButtonPress_Space){
            waitForButtonPress_Space = t + buttonPressDelay_Space
            player.setShoot()
        }

        if(window.getKeyState(GLFW_KEY_G) && t >= waitForButtonPress_G){
            waitForButtonPress_G = t + buttonPressDelay
            player.toggleWingMode()
        }

        if(window.getKeyState(GLFW_KEY_N) && t >= waitForButtonPress_N_M){
            waitForButtonPress_N_M = t + buttonPressDelay
            cameraHandler.prevCam()
        }
        if(window.getKeyState(GLFW_KEY_M) && t >= waitForButtonPress_N_M){
            waitForButtonPress_N_M = t + buttonPressDelay
            cameraHandler.nextCam()
        }
        player.update(dt,t)
        importedSkySphere.setPosition(cameraHandler.getActiveCamera().getWorldPosition())
        enemyHandler.update(dt,t)
        globalCollisionHandler.update()
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
