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

class PlayerObject(modelMatrix : Matrix4f, parent: Transformable? = null) : Transformable(modelMatrix, parent) {

    private var deltaTime = 0f
    private var time = 0f

    private val maxUp = 8f
    private val maxRight = 12.5f
    private val maxDown = -7f
    private val maxLeft = maxRight * -1f
    private val speed = 10f
    private val wingRotationSpeed = 1f

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

    val wingOLinMat : Matrix4f
    val wingOLoutMat : Matrix4f
    val wingURinMat : Matrix4f
    val wingURoutMat : Matrix4f

    val wing0RinMat : Matrix4f
    val wingORoutMat : Matrix4f
    val wingULinMat : Matrix4f
    val wingULoutMat : Matrix4f

    val waffeMidOutMat : Matrix4f
    val waffeMidInMat : Matrix4f

    val waffeEndOutMat : Matrix4f
    val waffeEndInMat : Matrix4f

    var wingOLposition = 0f
    var wingORposition = 0f
    var wingULposition = 0f
    var wingURposition = 0f

    var moveWingOLout = false
    var moveWingORout = false
    var moveWingULout = false
    var moveWingURout = false

    var wingOLin = true
    var wingORin = true
    var wingULin = true
    var wingURin = true
    var wingOLout = false
    var wingORout = false
    var wingULout = false
    var wingURout = false

    var weaponOLin = true
    var weaponORin = true
    var weaponULin = true
    var weaponURin = true
    var weaponOLout = false
    var weaponORout = false
    var weaponULout = false
    var weaponURout = false


    init {
        val pureWhiteTex = Texture2D("assets/textures/pureColor/pureWhite.png", true)
        pureWhiteTex.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)
        val pureBlackTex = Texture2D("assets/textures/pureColor/pureBlack.png", true)
        pureBlackTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
        val pureRedTex = Texture2D("assets/textures/pureColor/pureRed.png", true)
        pureRedTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

//        val matSphere = Material(
//            pureWhiteTex,
//            pureBlackTex,
//            pureWhiteTex
//        )
//
//        val matRed = Material(
//            pureRedTex,
//            pureBlackTex,
//            pureBlackTex
//        )

        //Body
        xBody = ModelLoader.loadModel("assets/models/X_Wing/X_Body.obj",
        Math.toRadians(0f),
        Math.toRadians(0f),
        Math.toRadians(0f))!!
        xBody.parent = this
        xBody.scale(Vector3f(0.03f,0.03f,0.03f))

        wingOL = ModelLoader.loadModel("assets/models/X_Wing/WingOL.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        wingOL.parent = xBody
        wingOL.setPosition(Vector3f(-15f,3f,24.5f))


        waffeOL_Root = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOL_Root.parent = wingOL
        waffeOL_Root.setPosition(Vector3f(-74f,30f,-23f))

        waffeOL_Mid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOL_Mid.parent = waffeOL_Root
        waffeOL_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        waffeOL_End = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOL_End.parent = waffeOL_Mid
        waffeOL_End.setPosition(Vector3f(0f,0.5f,-36f))
        waffeOL_End.rotate(0f,0f,-30f)


        wingOR = ModelLoader.loadModel("assets/models/X_Wing/WingOR.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        wingOR.parent = xBody
        wingOR.setPosition(Vector3f(15f,3f,24.5f))

        waffeOR_Root = Renderable(waffeOL_Root.renderList, Matrix4f(), wingOR)
        waffeOR_Root.setPosition(Vector3f(74f,30f,-23f))

        waffeOR_Mid = Renderable(waffeOL_Mid.renderList, Matrix4f(), waffeOR_Root)
        waffeOR_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        waffeOR_End = Renderable(waffeOL_End.renderList, Matrix4f(), waffeOR_Mid)
        waffeOR_End.setPosition(Vector3f(0f,0.5f,-36f))
        waffeOR_End.rotate(0f,0f,-30f)



        wingUL = Renderable(wingOR.renderList, Matrix4f(), xBody)
        wingUL.mirror(1f,0f,0f,0f,0f,0f)
        wingUL.mirror(0f,1f,0f,0f,0f,0f)
        wingUL.setPosition(Vector3f(-15f,-2f,24.5f))

        waffeUL_Root = Renderable(waffeOL_Root.renderList, Matrix4f(), wingUL)
        waffeUL_Root.setPosition(Vector3f(74f,30f,-23f))

        waffeUL_Mid = Renderable(waffeOL_Mid.renderList, Matrix4f(), waffeUL_Root)
        waffeUL_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        waffeUL_End = Renderable(waffeOL_End.renderList, Matrix4f(), waffeUL_Mid)
        waffeUL_End.setPosition(Vector3f(0f,0.5f,-36f))
        waffeUL_End.rotate(0f,0f,-30f)



        wingUR = Renderable(wingOL.renderList, Matrix4f(), xBody)
        wingUR.mirror(1f,0f,0f,0f,0f,0f)
        wingUR.mirror(0f,1f,0f,0f,0f,0f)
        wingUR.setPosition(Vector3f(15f,-2f,24.5f))

        waffeUR_Root = Renderable(waffeOL_Root.renderList, Matrix4f(), wingUR)
        waffeUR_Root.setPosition(Vector3f(-74f,30f,-23f))

        waffeUR_Mid = Renderable(waffeOL_Mid.renderList, Matrix4f(), waffeUR_Root)
        waffeUR_Mid.setPosition(Vector3f(0f,0.2f,-50f))

        waffeUR_End = Renderable(waffeOL_End.renderList, Matrix4f(), waffeUR_Mid)
        waffeUR_End.setPosition(Vector3f(0f,0.5f,-36f))
        waffeUR_End.rotate(0f,0f,-30f)



        wingOLoutMat = Matrix4f(wingOL.getModelMatrix())
        wingURoutMat = Matrix4f(wingUR.getModelMatrix())
        wingORoutMat = Matrix4f(wingOR.getModelMatrix())
        wingULoutMat = Matrix4f(wingUL.getModelMatrix())

        wingOL.rotate(0f,0f,18f)
        wingOLinMat = Matrix4f(wingOL.getModelMatrix())
        wingUR.rotate(0f,0f,18f)
        wingURinMat = Matrix4f(wingUR.getModelMatrix())
        wingOR.rotate(0f,0f,-18f)
        wing0RinMat = Matrix4f(wingOR.getModelMatrix())
        wingUL.rotate(0f,0f,-18f)
        wingULinMat = Matrix4f(wingUL.getModelMatrix())



        waffeMidOutMat = waffeUL_Mid.getModelMatrix()
        waffeUL_Mid.translate(Vector3f(0f,0f,45f))
        waffeMidInMat = waffeUL_Mid.getModelMatrix()

        waffeUL_Mid.setModelMatrix(waffeMidInMat)
        waffeUR_Mid.setModelMatrix(waffeMidInMat)
        waffeOL_Mid.setModelMatrix(waffeMidInMat)
        waffeOR_Mid.setModelMatrix(waffeMidInMat)



        waffeEndOutMat = waffeUL_End.getModelMatrix()
        waffeUL_End.translate(Vector3f(0f,0f,10f))
        waffeUL_End.rotate(0f,0f,90f)
        waffeEndInMat = waffeUL_End.getModelMatrix()

        waffeUL_End.setModelMatrix(waffeEndInMat)
        waffeUR_End.setModelMatrix(waffeEndInMat)
        waffeOL_End.setModelMatrix(waffeEndInMat)
        waffeOR_End.setModelMatrix(waffeEndInMat)


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
        deltaTime = newDt
    }
    fun setT (newTime : Float){
        time = newTime
    }
    fun moveUp(dt : Float){
        val matrix = getModelMatrix()
        val newVal = clampf(matrix.m31()+speed*dt, maxDown,maxUp)
        matrix.m31(newVal)
        setModelMatrix(matrix)
    }
    fun moveDown(dt : Float){
        val matrix = getModelMatrix()
        val newVal = clampf(matrix.m31()-speed*dt, maxDown,maxUp)
        matrix.m31(newVal)
        setModelMatrix(matrix)
    }
    fun moveLeft(dt : Float){
        val matrix = getModelMatrix()
        val newVal = clampf(matrix.m30()-speed*dt, maxLeft,maxRight)
        matrix.m30(newVal)
        setModelMatrix(matrix)
    }
    fun moveRight(dt : Float){
        val matrix = getModelMatrix()
        val newVal = clampf(matrix.m30()+speed*dt, maxLeft,maxRight)
        matrix.m30(newVal)
        setModelMatrix(matrix)
    }

//    fun wingsMoving() : Boolean = wingOLmoving && wingORmoving && wingULmoving && wingURmoving

    fun wingOLrotate(t : Float){
        wingOL.setModelMatrix(lerpMatrices(wingOLinMat, wingOLoutMat, clampf(wingOLposition,0f,1f)))
        if (t == 0f) {
            wingOLin = true
            wingOLout = false
        }else
        if (t == 1f) {
            wingOLin = false
            wingOLout = true
        } else {
            wingOLin = false
            wingOLout = false
        }
    }
    fun wingORrotate(t : Float){
        wingOR.setModelMatrix(lerpMatrices(wing0RinMat, wingORoutMat, clampf(t,0f,1f)))
        if (t == 0f) {
            wingORin = true
            wingORout = false
        }else
        if (t == 1f) {
            wingORin = false
            wingORout = true
        } else {
            wingORin = false
            wingORout = false
        }
    }
    fun wingULrotate(t : Float){
        wingUL.setModelMatrix(lerpMatrices(wingULinMat, wingULoutMat, clampf(t,0f,1f)))
        if (t == 0f) {
            wingULin = true
            wingULout = false
        }else
        if (t == 1f) {
            wingULin = false
            wingULout = true
        } else {
            wingULin = false
            wingULout = false
        }
    }
    fun wingURrotate(t : Float){
        wingUR.setModelMatrix(lerpMatrices(wingURinMat, wingURoutMat, clampf(t,0f,1f)))
        if (t == 0f) {
            wingURin = true
            wingURout = false
        }else
        if (t == 1f) {
            wingURin = false
            wingURout = true
        } else {
            wingURin = false
            wingURout = false
        }
    }


    fun waffeOLmove(t : Float){
        waffeOL_Mid.setModelMatrix(lerpMatrices(waffeMidInMat,waffeMidOutMat, clampf(t,0f,1f)))
        waffeOL_End.setModelMatrix(lerpMatrices(waffeEndInMat,waffeEndOutMat,clampf(t,0f,1f)))
        if (t == 0f) {
            weaponOLin = true
            weaponOLout = false
        }else
            if (t == 1f) {
                weaponOLin = false
                weaponOLout = true
            } else {
                weaponOLin = false
                weaponOLout = false
            }
    }
    fun waffeORmove(t : Float){
        waffeOR_Mid.setModelMatrix(lerpMatrices(waffeMidInMat,waffeMidOutMat, clampf(t,0f,1f)))
        waffeOR_End.setModelMatrix(lerpMatrices(waffeEndInMat,waffeEndOutMat,clampf(t,0f,1f)))
        if (t == 0f) {
            weaponORin = true
            weaponORout = false
        }else
            if (t == 1f) {
                weaponORin = false
                weaponORout = true
            } else {
                weaponORin = false
                weaponORout = false
            }
    }
    fun waffeULmove(t : Float){
        waffeUL_Mid.setModelMatrix(lerpMatrices(waffeMidInMat,waffeMidOutMat,clampf(t,0f,1f)))
        waffeUL_End.setModelMatrix(lerpMatrices(waffeEndInMat,waffeEndOutMat,clampf(t,0f,1f)))
        if (t == 0f) {
            weaponULin = true
            weaponULout = false
        }else
            if (t == 1f) {
                weaponULin = false
                weaponULout = true
            } else {
                weaponULin = false
                weaponULout = false
            }
    }
    fun waffeURmove(t : Float){
        waffeUR_Mid.setModelMatrix(lerpMatrices(waffeMidInMat,waffeMidOutMat, clampf(t,0f,1f)))
        waffeUR_End.setModelMatrix(lerpMatrices(waffeEndInMat,waffeEndOutMat,clampf(t,0f,1f)))
        if (t == 0f) {
            weaponURin = true
            weaponURout = false
        }else
            if (t == 1f) {
                weaponURin = false
                weaponURout = true
            } else {
                weaponURin = false
                weaponURout = false
            }
    }



    fun toggleWingMode(){
        moveWingOLout = !moveWingOLout
        moveWingORout = !moveWingORout
        moveWingULout = !moveWingULout
        moveWingURout = !moveWingURout

        println(moveWingOLout)
    }



    fun update(deltaTime: Float, time: Float){
        setDT(deltaTime)
        setT(time)

        if (moveWingOLout && wingOLout == false){
            println("ja")
            wingOLposition = clampf(wingOLposition + deltaTime * wingRotationSpeed, 0f,1f)
            wingOLrotate(clampf(wingOLposition, 0f,1f))
        }
        if (moveWingORout && wingORout == false){
            println("ja")
            wingORposition = clampf(wingORposition + deltaTime * wingRotationSpeed, 0f,1f)
            wingORrotate(clampf(wingORposition, 0f,1f))
        }
        if (moveWingULout && wingULout == false){
            println("ja")
            wingULposition = clampf(wingULposition + deltaTime * wingRotationSpeed, 0f,1f)
            wingULrotate(clampf(wingULposition, 0f,1f))
        }
        if (moveWingURout && wingURout == false){
            println("ja")
            wingURposition = clampf(wingURposition + deltaTime * wingRotationSpeed, 0f,1f)
            wingURrotate(clampf(wingURposition, 0f,1f))
        }

        if (!moveWingOLout && wingOLin == false) {
            println("ja")
            wingOLposition = clampf(wingOLposition - deltaTime * wingRotationSpeed, 0f,1f)
            wingOLrotate(clampf(wingOLposition, 0f, 1f))
        }
        if (!moveWingORout && wingORin == false) {
            println("ja")
            wingORposition = clampf(wingORposition - deltaTime * wingRotationSpeed, 0f,1f)
            wingORrotate(clampf(wingORposition, 0f, 1f))
        }
        if (!moveWingULout && wingULin == false) {
            println("ja")
            wingULposition = clampf(wingULposition - deltaTime * wingRotationSpeed, 0f,1f)
            wingULrotate(clampf(wingULposition, 0f, 1f))
        }
        if (!moveWingURout && wingURin == false) {
            println("ja")
            wingURposition = clampf(wingURposition - deltaTime * wingRotationSpeed, 0f,1f)
            wingURrotate(clampf(wingURposition, 0f, 1f))
        }

    }


}