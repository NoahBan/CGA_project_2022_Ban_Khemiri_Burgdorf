package cga.exercise.game

import cga.exercise.components.collision.CollisionHandler
import cga.exercise.components.camera.Camera
import cga.exercise.components.geometry.*
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glClearColor
import org.lwjgl.opengl.GL30

class Scene2(private val window: GameWindow) {

    private val staticShader : ShaderProgram

    private val importedBike : Renderable
    private val importedEnemyBike : Renderable
    private val importedSphere : Renderable
    private val importedGround : Renderable
    private val importedCollisionBox1 : Renderable
    private val importedCollisioSphere1 : Renderable

    private val sceneCam : Camera
    private val collisionHandler : CollisionHandler
    var xposBefore : Double = 0.0

    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/vertexShdr.glsl", "assets/shaders/fragmentShdr.glsl")

        //initial opengl state
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        GL11.glDisable(GL11.GL_CULL_FACE); GLError.checkThrow()
        GL11.glEnable(GL11.GL_DEPTH_TEST); GLError.checkThrow()
        GL11.glDepthFunc(GL11.GL_LESS); GLError.checkThrow()
        GL11.glEnable(GL11.GL_CULL_FACE)
        GL11.glFrontFace(GL11.GL_CCW)
        GL11.glCullFace(GL11.GL_BACK)

        //define ibo
        val posAndTexcAndNormPos = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 0)
        val posAndTexcAndNormTexc = VertexAttribute (2, GL30.GL_FLOAT,8 * 4, 3 * 4)
        val posAndTexcAndNormNorm = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 5 * 4)
        val posAndTexcAndNormAttrArray = arrayOf(posAndTexcAndNormPos, posAndTexcAndNormTexc, posAndTexcAndNormNorm)

        //Textures
        //pureTextures
        val pureBlackTex = Texture2D("assets/textures/pureColor/pureBlack.png", true)
        pureBlackTex.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)
        val pureRedTex = Texture2D("assets/textures/pureColor/pureRed.png", true)
        pureRedTex.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)
        val pureWhiteTex = Texture2D("assets/textures/pureColor/pureWhite.png", true)
        pureWhiteTex.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)

        //Sphere Texture
        val sphereDiffuseTex = Texture2D("assets/textures/sphere.png", true)
        sphereDiffuseTex.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)
        val sphereEmissionTex = Texture2D("assets/textures/sphere_emissive.png", true)
        sphereEmissionTex.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)
        val lightSphereEmissionTex = Texture2D("assets/textures/lightSphereEmissive.png", true)
        lightSphereEmissionTex.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)

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
        val matGroundSphere = Material(
            pureWhiteTex,
            pureRedTex,
            pureWhiteTex
        )

        //Sphere Geo
        val importObjSphere = OBJLoader.loadOBJ("assets/models/sphere.obj", true)
        val importedSphereData  = importObjSphere.objects[0].meshes[0]
        val importedSphereMesh = Mesh (importedSphereData.vertexData, importedSphereData.indexData, posAndTexcAndNormAttrArray,false, matSphere)
        //Ground
        val importObjGround = OBJLoader.loadOBJ("assets/models/ground.obj", true)
        val importedGroundData  = importObjGround.objects[0].meshes[0]
        val importedGroundMesh = Mesh (importedGroundData.vertexData, importedGroundData.indexData, posAndTexcAndNormAttrArray,false, matGroundSphere)
        //Collision Object Geo
        val importObjBox = OBJLoader.loadOBJ("assets/models/CollisionObject.obj", true)
        val importedBoxData  = importObjBox.objects[0].meshes[0]
        val importedBoxMesh = Mesh (importedBoxData.vertexData, importedBoxData.indexData, posAndTexcAndNormAttrArray,false, matLightSphere)
        //Collision Sphere Geo
        val importObjCollisionSphere = OBJLoader.loadOBJ("assets/models/CollisionSphere.obj", true)
        val importedCollisionSphereData  = importObjCollisionSphere.objects[0].meshes[0]
        val importedCollisionSphereMesh = Mesh (importedCollisionSphereData.vertexData, importedCollisionSphereData.indexData, posAndTexcAndNormAttrArray,false, matLightSphere)
        //Bike
        importedBike = ModelLoader.loadModel("assets/Light Cycle/HQ_Movie cycle.obj",
            Math.toRadians(-90f),
            Math.toRadians(90.0f),
            Math.toRadians(0f))!!
        importedEnemyBike = ModelLoader.loadModel("assets/Light Cycle/HQ_Movie cycle.obj",
            Math.toRadians(-90f),
            Math.toRadians(90.0f),
            Math.toRadians(0f))!!
        //Meshes to Renderables
        importedSphere = Renderable(mutableListOf(importedSphereMesh), Matrix4f(), null)
        importedGround = Renderable(mutableListOf(importedGroundMesh), Matrix4f(), null)
        importedCollisionBox1 = Renderable(mutableListOf(importedBoxMesh), Matrix4f(), null)
        importedCollisioSphere1 = Renderable(mutableListOf(importedCollisionSphereMesh), Matrix4f(), null)

        //Setup Objects
        sceneCam = Camera(70f, 16f/9f, 0.1F, 100.0F, Matrix4f(), importedBike)
        sceneCam.rotate(-20F,0F,0F)
        sceneCam.translate(Vector3f(0F,1F,10.0F))

        importedSphere.translate(Vector3f(0f,1f,-4f))
        importedEnemyBike.translate(Vector3f(-5f,0f,-5f))
        importedEnemyBike.rotate(0f,90f,0f)

        //Collision Setup
        collisionHandler = CollisionHandler()
        collisionHandler.addAlly(importedBike)
        collisionHandler.addEnemy(importedEnemyBike)
        collisionHandler.addEnemyProjectile(importedSphere)
    }

    fun render(dt: Float, t: Float) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        glClearColor(0.1f, 0.1f, 0.2f, 1.0f)
        staticShader.use()

        sceneCam.bind(staticShader)

        importedSphere.render(staticShader)
        importedBike.render(staticShader)
        importedEnemyBike.render(staticShader)
        importedGround.render(staticShader)

        collisionHandler.checkCollision()
        collisionHandler.showCollision(staticShader, importedCollisioSphere1)
    }

    fun update(dt: Float, t: Float) {
        if(window.getKeyState(GLFW.GLFW_KEY_W)){
            importedBike.translate(Vector3f(0f,0f,-10f*dt))
        }
        if(window.getKeyState(GLFW.GLFW_KEY_S)){
            importedBike.translate(Vector3f(0f,0f,10f*dt))
        }
        if(window.getKeyState(GLFW.GLFW_KEY_A)){
            importedBike.rotate(0f,150f*dt,0f)
        }
        if(window.getKeyState(GLFW.GLFW_KEY_D)){
            importedBike.rotate(0f,-150f*dt,0f)
        }
        if(window.getKeyState(GLFW.GLFW_KEY_R)){
            importedBike.translate(Vector3f(0f,0.1f,0f))
        }
        if(window.getKeyState(GLFW.GLFW_KEY_F)){
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