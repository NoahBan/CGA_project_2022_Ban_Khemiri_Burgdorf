package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.ModelLoader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

class PlayerObject(modelMatrix : Matrix4f, parent: Transformable? = null) : Transformable(modelMatrix, parent) {

    val playerGeo : MutableList<Renderable>
    private val xBody : Renderable
    private val WingOL : Renderable
    private val WingOR : Renderable
    private val WingUR : Renderable
    private val WingUL : Renderable

    private val WaffeUL_Root : Renderable
    private val WaffeUL_Mid : Renderable
    private val WaffeUL_End : Renderable

    private val WaffeUR_Root : Renderable
    private val WaffeUR_Mid : Renderable
    private val WaffeUR_End : Renderable

    private val WaffeOR_Root : Renderable
    private val WaffeOR_Mid : Renderable
    private val WaffeOR_End : Renderable

    private val WaffeOL_Root : Renderable
    private val WaffeOL_Mid : Renderable
    private val WaffeOL_End : Renderable



    init {
        val pureWhiteTex = Texture2D("assets/textures/pureColor/pureWhite.png", true)
        pureWhiteTex.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)
        val pureBlackTex = Texture2D("assets/textures/pureColor/pureBlack.png", true)
        pureBlackTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

        val matSphere = Material(
            pureWhiteTex,
            pureBlackTex,
            pureWhiteTex
        )

        //Body
        xBody = ModelLoader.loadModel("assets/models/X_Wing/X_Body.obj",
        Math.toRadians(0f),
        Math.toRadians(0f),
        Math.toRadians(0f))!!
        xBody.renderList[0].material = matSphere //Body
        xBody.renderList[1].material = matSphere //Nase
        xBody.renderList[2].material = matSphere //Gitter
        xBody.renderList[3].material = matSphere //CockpitGlas
        xBody.renderList[4].material = matSphere //CockpitRahmen
        xBody.renderList[5].material = matSphere //TopKram
        xBody.renderList[6].material = matSphere //R2 Kop
        xBody.renderList[7].material = matSphere //R2 Kop
        xBody.parent = this
        xBody.scale(Vector3f(0.03f,0.03f,0.03f))

        WingOL = ModelLoader.loadModel("assets/models/X_Wing/WingOL.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WingOL.renderList[0].material = matSphere //Body
        WingOL.renderList[1].material = matSphere //Body
        WingOL.renderList[2].material = matSphere //Body
        WingOL.renderList[3].material = matSphere //Body
        WingOL.renderList[4].material = matSphere //Body
        WingOL.renderList[5].material = matSphere //Body
        WingOL.parent = xBody
        WingOL.setPosition(Vector3f(-15f,3f,24.5f))

        WaffeOL_Root = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeOL_Root.renderList[0].material = matSphere //Body
        WaffeOL_Root.parent = WingOL
        WaffeOL_Root.setPosition(Vector3f(-74f,30f,-23f))

        WaffeOL_Mid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeOL_Mid.renderList[0].material = matSphere //Body
        WaffeOL_Mid.parent = WaffeOL_Root
        WaffeOL_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        WaffeOL_End = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeOL_End.renderList[0].material = matSphere //Body
        WaffeOL_End.parent = WaffeOL_Mid
        WaffeOL_End.setPosition(Vector3f(0f,0.5f,-36f))
        WaffeOL_End.rotate(0f,0f,-30f)


        WingOR = ModelLoader.loadModel("assets/models/X_Wing/WingOR.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WingOR.renderList[0].material = matSphere //Body
        WingOR.renderList[1].material = matSphere //Body
        WingOR.renderList[2].material = matSphere //Body
        WingOR.renderList[3].material = matSphere //Body
        WingOR.renderList[4].material = matSphere //Body
        WingOR.renderList[5].material = matSphere //Body
        WingOR.parent = xBody
        WingOR.setPosition(Vector3f(15f,3f,24.5f))

        WaffeOR_Root = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeOR_Root.renderList[0].material = matSphere //Body
        WaffeOR_Root.parent = WingOR
        WaffeOR_Root.setPosition(Vector3f(74f,30f,-23f))

        WaffeOR_Mid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeOR_Mid.renderList[0].material = matSphere //Body
        WaffeOR_Mid.parent = WaffeOR_Root
        WaffeOR_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        WaffeOR_End = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeOR_End.renderList[0].material = matSphere //Body
        WaffeOR_End.parent = WaffeOR_Mid
        WaffeOR_End.setPosition(Vector3f(0f,0.5f,-36f))
        WaffeOR_End.rotate(0f,0f,-30f)



        WingUR = ModelLoader.loadModel("assets/models/X_Wing/WingUR.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WingUR.renderList[0].material = matSphere //Body
        WingUR.renderList[1].material = matSphere //Body
        WingUR.renderList[2].material = matSphere //Body
        WingUR.renderList[3].material = matSphere //Body
        WingUR.renderList[4].material = matSphere //Body
        WingUR.renderList[5].material = matSphere //Body
        WingUR.parent = xBody
        WingUR.setPosition(Vector3f(15f,-2f,24.5f))

        WaffeUR_Root = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeUR_Root.renderList[0].material = matSphere //Body
        WaffeUR_Root.parent = WingUR
        WaffeUR_Root.rotate(0f,0f,180f)
        WaffeUR_Root.setPosition(Vector3f(74f,-30f,-23f))

        WaffeUR_Mid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeUR_Mid.renderList[0].material = matSphere //Body
        WaffeUR_Mid.parent = WaffeUR_Root
        WaffeUR_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        WaffeUR_End = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeUR_End.renderList[0].material = matSphere //Body
        WaffeUR_End.parent = WaffeUR_Mid
        WaffeUR_End.setPosition(Vector3f(0f,0.5f,-36f))
        WaffeUR_End.rotate(0f,0f,-30f)


        WingUL = ModelLoader.loadModel("assets/models/X_Wing/WingUL.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WingUL.renderList[0].material = matSphere //Body
        WingUL.renderList[1].material = matSphere //Body
        WingUL.renderList[2].material = matSphere //Body
        WingUL.renderList[3].material = matSphere //Body
        WingUL.renderList[4].material = matSphere //Body
        WingUL.renderList[5].material = matSphere //Body
        WingUL.parent = xBody
        WingUL.setPosition(Vector3f(-15f,-2f,24.5f))

        WaffeUL_Root = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeUL_Root.renderList[0].material = matSphere //Body
        WaffeUL_Root.parent = WingUL
        WaffeUL_Root.rotate(0f,0f,180f)
        WaffeUL_Root.setPosition(Vector3f(-74f,-30f,-23f))

        WaffeUL_Mid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeUL_Mid.renderList[0].material = matSphere //Body
        WaffeUL_Mid.parent = WaffeUL_Root
        WaffeUL_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        WaffeUL_End = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        WaffeUL_End.renderList[0].material = matSphere //Body
        WaffeUL_End.parent = WaffeUL_Mid
        WaffeUL_End.setPosition(Vector3f(0f,0.5f,-36f))
        WaffeUL_End.rotate(0f,0f,-30f)





        playerGeo = mutableListOf(xBody, WingOL, WingOR, WingUR, WingUL,
            WaffeUL_Root,WaffeUL_Mid,WaffeUL_End,
            WaffeUR_Root,WaffeUR_Mid,WaffeUR_End,
            WaffeOR_Root,WaffeOR_Mid,WaffeOR_End,
            WaffeOL_Root,WaffeOL_Mid,WaffeOL_End)


    }

    fun render(shader : ShaderProgram){
        for (each in playerGeo) each.render(shader)
    }

    fun moveUp(dt : Float){
        this.translate(Vector3f(0f, 10f*dt, 0f))
    }
    fun moveDown(dt : Float){
        this.translate(Vector3f(0f, -10f*dt, 0f))
    }
    fun moveLeft(dt : Float){
        this.translate(Vector3f(-10f*dt, 0f, 0f))
    }
    fun moveRight(dt : Float){
        this.translate(Vector3f(10f*dt, 0f, 0f))
    }
}