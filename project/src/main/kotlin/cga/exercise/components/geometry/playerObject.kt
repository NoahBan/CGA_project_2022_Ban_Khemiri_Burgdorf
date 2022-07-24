package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.exercise.components.utility.clampf
import cga.exercise.components.utility.lerpMatrices
import cga.framework.ModelLoader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30
import org.lwjgl.system.MathUtil
import java.lang.reflect.UndeclaredThrowableException
import kotlin.math.min

class PlayerObject(modelMatrix : Matrix4f, parent: Transformable? = null) : Transformable(modelMatrix, parent) {

    private var dt = 0f

    val playerGeo : MutableList<Renderable>
    private val xBody : Renderable
    private val wingOL : Renderable
    private val wingOR : Renderable
    private val wingUR : Renderable
    private val wingUL : Renderable

    private val waffeUL_Root : Renderable
    private val waffeUL_Mid : Renderable
    private val waffeUL_End : Renderable

    private val waffeUR_Root : Renderable
    private val waffeUR_Mid : Renderable
    private val waffeUR_End : Renderable

    private val waffeOR_Root : Renderable
    private val waffeOR_Mid : Renderable
    private val waffeOR_End : Renderable

    private val waffeOL_Root : Renderable
    private val waffeOL_Mid : Renderable
    private val waffeOL_End : Renderable

    var wingOLmoving = false
    var wingORmoving = false
    var wingULmoving = false
    var wingURmoving = false

    val wingOLflatMat : Matrix4f
    val wingOLxMat : Matrix4f
    val wingURflatMat : Matrix4f
    val wingURxMat : Matrix4f

    val wing0RflatMat : Matrix4f
    val wingORxMat : Matrix4f
    val wingULflatMat : Matrix4f
    val wingULxMat : Matrix4f

    var wingOLposition = 0f
    var wingORposition = 0f
    var wingULposition = 0f
    var wingURposition = 0f


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

        wingOL = ModelLoader.loadModel("assets/models/X_Wing/WingOL.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        wingOL.renderList[0].material = matSphere //Body
        wingOL.renderList[1].material = matSphere //Body
        wingOL.renderList[2].material = matSphere //Body
        wingOL.renderList[3].material = matSphere //Body
        wingOL.renderList[4].material = matSphere //Body
        wingOL.renderList[5].material = matSphere //Body
        wingOL.parent = xBody
        wingOL.setPosition(Vector3f(-15f,3f,24.5f))


        waffeOL_Root = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOL_Root.renderList[0].material = matSphere //Body
        waffeOL_Root.parent = wingOL
        waffeOL_Root.setPosition(Vector3f(-74f,30f,-23f))

        waffeOL_Mid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOL_Mid.renderList[0].material = matSphere //Body
        waffeOL_Mid.parent = waffeOL_Root
        waffeOL_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        waffeOL_End = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOL_End.renderList[0].material = matSphere //Body
        waffeOL_End.parent = waffeOL_Mid
        waffeOL_End.setPosition(Vector3f(0f,0.5f,-36f))
        waffeOL_End.rotate(0f,0f,-30f)


        wingOR = ModelLoader.loadModel("assets/models/X_Wing/WingOR.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        wingOR.renderList[0].material = matSphere //Body
        wingOR.renderList[1].material = matSphere //Body
        wingOR.renderList[2].material = matSphere //Body
        wingOR.renderList[3].material = matSphere //Body
        wingOR.renderList[4].material = matSphere //Body
        wingOR.renderList[5].material = matSphere //Body
        wingOR.parent = xBody
        wingOR.setPosition(Vector3f(15f,3f,24.5f))

        waffeOR_Root = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOR_Root.renderList[0].material = matSphere //Body
        waffeOR_Root.parent = wingOR
        waffeOR_Root.setPosition(Vector3f(74f,30f,-23f))

        waffeOR_Mid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOR_Mid.renderList[0].material = matSphere //Body
        waffeOR_Mid.parent = waffeOR_Root
        waffeOR_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        waffeOR_End = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOR_End.renderList[0].material = matSphere //Body
        waffeOR_End.parent = waffeOR_Mid
        waffeOR_End.setPosition(Vector3f(0f,0.5f,-36f))
        waffeOR_End.rotate(0f,0f,-30f)



        wingUR = ModelLoader.loadModel("assets/models/X_Wing/WingUR.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        wingUR.renderList[0].material = matSphere //Body
        wingUR.renderList[1].material = matSphere //Body
        wingUR.renderList[2].material = matSphere //Body
        wingUR.renderList[3].material = matSphere //Body
        wingUR.renderList[4].material = matSphere //Body
        wingUR.renderList[5].material = matSphere //Body
        wingUR.parent = xBody
        wingUR.setPosition(Vector3f(15f,-2f,24.5f))

        waffeUR_Root = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeUR_Root.renderList[0].material = matSphere //Body
        waffeUR_Root.parent = wingUR
        waffeUR_Root.rotate(0f,0f,180f)
        waffeUR_Root.setPosition(Vector3f(74f,-30f,-23f))

        waffeUR_Mid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeUR_Mid.renderList[0].material = matSphere //Body
        waffeUR_Mid.parent = waffeUR_Root
        waffeUR_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        waffeUR_End = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeUR_End.renderList[0].material = matSphere //Body
        waffeUR_End.parent = waffeUR_Mid
        waffeUR_End.setPosition(Vector3f(0f,0.5f,-36f))
        waffeUR_End.rotate(0f,0f,-30f)


        wingUL = ModelLoader.loadModel("assets/models/X_Wing/WingUL.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        wingUL.renderList[0].material = matSphere //Body
        wingUL.renderList[1].material = matSphere //Body
        wingUL.renderList[2].material = matSphere //Body
        wingUL.renderList[3].material = matSphere //Body
        wingUL.renderList[4].material = matSphere //Body
        wingUL.renderList[5].material = matSphere //Body
        wingUL.parent = xBody
        wingUL.setPosition(Vector3f(-15f,-2f,24.5f))

        waffeUL_Root = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeUL_Root.renderList[0].material = matSphere //Body
        waffeUL_Root.parent = wingUL
        waffeUL_Root.rotate(0f,0f,180f)
        waffeUL_Root.setPosition(Vector3f(-74f,-30f,-23f))

        waffeUL_Mid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeUL_Mid.renderList[0].material = matSphere //Body
        waffeUL_Mid.parent = waffeUL_Root
        waffeUL_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        waffeUL_End = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeUL_End.renderList[0].material = matSphere //Body
        waffeUL_End.parent = waffeUL_Mid
        waffeUL_End.setPosition(Vector3f(0f,0.5f,-36f))
        waffeUL_End.rotate(0f,0f,-30f)


        wingOLxMat = Matrix4f(wingOL.getModelMatrix())
        wingURxMat = Matrix4f(wingUR.getModelMatrix())
        wingORxMat = Matrix4f(wingOR.getModelMatrix())
        wingULxMat = Matrix4f(wingUL.getModelMatrix())

        wingOL.rotate(0f,0f,18f)
        wingOLflatMat = Matrix4f(wingOL.getModelMatrix())
        wingUR.rotate(0f,0f,18f)
        wingURflatMat = Matrix4f(wingUR.getModelMatrix())
        wingOR.rotate(0f,0f,-18f)
        wing0RflatMat = Matrix4f(wingOR.getModelMatrix())
        wingUL.rotate(0f,0f,-18f)
        wingULflatMat = Matrix4f(wingUL.getModelMatrix())





        playerGeo = mutableListOf(
            xBody, wingOL, wingOR, wingUR, wingUL,
            waffeUL_Root,waffeUL_Mid,waffeUL_End,
            waffeUR_Root,waffeUR_Mid,waffeUR_End,
            waffeOR_Root,waffeOR_Mid,waffeOR_End,
            waffeOL_Root,waffeOL_Mid,waffeOL_End
        )
    }

    fun render(shader : ShaderProgram){
        for (each in playerGeo) each.render(shader)
    }

    fun setDT (newDt : Float){
        dt = newDt
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

    fun wingsMoving() : Boolean = wingOLmoving && wingORmoving && wingULmoving && wingURmoving

    fun wingORrotate(t : Float) : Boolean{
        wingORmoving = true
        wingOR.setModelMatrix(lerpMatrices(wing0RflatMat, wingORxMat, t))
        if (t == 0f || t == 1f) wingORmoving = false
        return wingORmoving
    }
    fun wingOLrotate(t : Float) : Boolean{
        wingOLmoving = true
        wingOL.setModelMatrix(lerpMatrices(wingOLflatMat, wingOLxMat, t))
        if (t == 0f || t == 1f) wingOLmoving = false
        return wingOLmoving
    }

    fun wingURrotate(t : Float) : Boolean{
        wingURmoving = true
        wingUR.setModelMatrix(lerpMatrices(wingURflatMat, wingURxMat, t))
        if (t == 0f || t == 1f) wingURmoving = false
        return wingURmoving
    }
    fun wingULrotate(t : Float) : Boolean{
        wingULmoving = true
        wingUL.setModelMatrix(lerpMatrices(wingULflatMat, wingULxMat, t))
        if (t == 0f || t == 1f) wingULmoving = false
        return wingULmoving
    }

    fun rotateAllWings(toMode : String){
        var direction : Float
        if (toMode == "x"){direction = 1f}
        else if (toMode == "flat"){direction = -1f}
        else {throw java.lang.Exception ("toMode must be 'x' or 'flat'")
        }
        wingOLposition = clampf(wingOLposition + dt * direction, 0f,1f)
        wingORposition = clampf(wingORposition + dt * direction, 0f,1f)
        wingULposition = clampf(wingULposition + dt * direction, 0f,1f)
        wingURposition = clampf(wingURposition + dt * direction, 0f,1f)
        wingOLrotate(wingOLposition)
        wingORrotate(wingORposition)
        wingULrotate(wingULposition)
        wingURrotate(wingURposition)
    }


}