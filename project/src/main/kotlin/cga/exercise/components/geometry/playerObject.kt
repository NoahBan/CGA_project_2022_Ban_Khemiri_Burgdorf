package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.exercise.components.utility.*
import cga.framework.ModelLoader
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

class PlayerObject(modelMatrix : Matrix4f, parent: Transformable? = null) : Transformable(modelMatrix, parent) {

    private var deltaTime = 0f
    private var time = 0f


    private val maxUpDownRotation = Math.toRadians(30f)
    private val maxLeftRightRotation = Math.toRadians(70f)
    private val rotationResetSpeed = 0.1f
    private val maxUp = 8f
    private val maxRight = 12.5f
    private val maxDown = -7f
    private val maxLeft = maxRight * -1f
    private val speed = 15f
    private val rotationUpDownSpeed = Math.toRadians(160f)
    private val rotationLeftRightSpeed = Math.toRadians(160f)
    private val wingRotationSpeed = 1f
    private val weaponMoveSpeed = 1f

    var rollParent = Transformable(Matrix4f(), this)

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

    var weaponOLposition = 0f
    var weaponORposition = 0f
    var weaponULposition = 0f
    var weaponURposition = 0f

    var moveUp = false
    var moveDown = false
    var moveLeft = false
    var moveRight = false

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
        xBody.parent = rollParent
//        xBody.scale(Vector3f(0.03f,0.03f,0.03f))

        wingOL = ModelLoader.loadModel("assets/models/X_Wing/WingOL.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        wingOL.parent = xBody
        wingOL.setPosition(Vector3f(-15f*0.03f,3f*0.03f,24.5f*0.03f))


        waffeOL_Root = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOL_Root.parent = wingOL
        waffeOL_Root.setPosition(Vector3f(-74f*0.03f,30f*0.03f,-23f*0.03f))

        waffeOL_Mid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOL_Mid.parent = waffeOL_Root
        waffeOL_Mid.setPosition(Vector3f(0f,0.2f*0.03f,-50f*0.03f))

        waffeOL_End = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        waffeOL_End.parent = waffeOL_Mid
        waffeOL_End.setPosition(Vector3f(0f,0.5f*0.03f,-36f*0.03f))
        waffeOL_End.rotate(0f,0f,-30f)


        wingOR = ModelLoader.loadModel("assets/models/X_Wing/WingOR.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!
        wingOR.parent = xBody
        wingOR.setPosition(Vector3f(15f*0.03f,3f*0.03f,24.5f*0.03f))

        waffeOR_Root = Renderable(waffeOL_Root.renderList, Matrix4f(), wingOR)
        waffeOR_Root.setPosition(Vector3f(74f*0.03f,30f*0.03f,-23f*0.03f))

        waffeOR_Mid = Renderable(waffeOL_Mid.renderList, Matrix4f(), waffeOR_Root)
        waffeOR_Mid.setPosition(Vector3f(0f,0.2f*0.03f,-50f*0.03f))

        waffeOR_End = Renderable(waffeOL_End.renderList, Matrix4f(), waffeOR_Mid)
        waffeOR_End.setPosition(Vector3f(0f,0.5f*0.03f,-36f*0.03f))
        waffeOR_End.rotate(0f,0f,-30f)



        wingUL = Renderable(wingOR.renderList, Matrix4f(), xBody)
        wingUL.mirror(1f,0f,0f,0f,0f,0f)
        wingUL.mirror(0f,1f,0f,0f,0f,0f)
        wingUL.setPosition(Vector3f(-15f*0.03f,-2f*0.03f,24.5f*0.03f))

        waffeUL_Root = Renderable(waffeOL_Root.renderList, Matrix4f(), wingUL)
        waffeUL_Root.setPosition(Vector3f(74f*0.03f,30f*0.03f,-23f*0.03f))

        waffeUL_Mid = Renderable(waffeOL_Mid.renderList, Matrix4f(), waffeUL_Root)
        waffeUL_Mid.setPosition(Vector3f(0f,0.2f*0.03f,-50f*0.03f))

        waffeUL_End = Renderable(waffeOL_End.renderList, Matrix4f(), waffeUL_Mid)
        waffeUL_End.setPosition(Vector3f(0f,0.5f*0.03f,-36f*0.03f))
        waffeUL_End.rotate(0f,0f,-30f)



        wingUR = Renderable(wingOL.renderList, Matrix4f(), xBody)
        wingUR.mirror(1f,0f,0f,0f,0f,0f)
        wingUR.mirror(0f,1f,0f,0f,0f,0f)
        wingUR.setPosition(Vector3f(15f*0.03f,-2f*0.03f,24.5f*0.03f))

        waffeUR_Root = Renderable(waffeOL_Root.renderList, Matrix4f(), wingUR)
        waffeUR_Root.setPosition(Vector3f(-74f*0.03f,30f*0.03f,-23f*0.03f))

        waffeUR_Mid = Renderable(waffeOL_Mid.renderList, Matrix4f(), waffeUR_Root)
        waffeUR_Mid.setPosition(Vector3f(0f,0.2f*0.03f,-50f*0.03f))

        waffeUR_End = Renderable(waffeOL_End.renderList, Matrix4f(), waffeUR_Mid)
        waffeUR_End.setPosition(Vector3f(0f,0.5f*0.03f,-36f*0.03f))
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
        waffeUL_Mid.translate(Vector3f(0f,0f,45f*0.03f))
        waffeMidInMat = waffeUL_Mid.getModelMatrix()

        waffeUL_Mid.setModelMatrix(waffeMidInMat)
        waffeUR_Mid.setModelMatrix(waffeMidInMat)
        waffeOL_Mid.setModelMatrix(waffeMidInMat)
        waffeOR_Mid.setModelMatrix(waffeMidInMat)



        waffeEndOutMat = waffeUL_End.getModelMatrix()
        waffeUL_End.translate(Vector3f(0f,0f,10f*0.03f))
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
    fun setMoveUp(){
        moveUp = true
    }
    fun moveUp(dt : Float){
        var matrix = getModelMatrix()
        val newVal = clampf(matrix.m31()+speed*dt, maxDown,maxUp)
        matrix.m31(newVal)

        var eulers = Vector3f(0f,0f,0f)
        matrix.getEulerAnglesZYX(eulers)
        var newRotation = eulers[0] + rotationUpDownSpeed * dt
        matrix = setEuler(matrix, clampf(newRotation, -maxUpDownRotation,maxUpDownRotation),eulers[1],eulers[2])

        setModelMatrix(matrix)
    }
    fun setMoveDown(){
        moveDown = true
    }
    fun moveDown(dt : Float){
        var matrix = getModelMatrix()
        val newVal = clampf(matrix.m31()-speed*dt, maxDown,maxUp)
        matrix.m31(newVal)

        var eulers = Vector3f(0f,0f,0f)
        matrix.getEulerAnglesZYX(eulers)
        var newRotation = eulers[0] - rotationUpDownSpeed * dt
        matrix = setEuler(matrix, clampf(newRotation, -maxUpDownRotation,maxUpDownRotation),eulers[1],eulers[2])

        setModelMatrix(matrix)
    }
    fun vertRotationReset(dt : Float){
        var matrix = getModelMatrix()
        var eulers = Vector3f(0f,0f,0f)
        matrix.getEulerAnglesZYX(eulers)
        var newRotation = lerpV3f(eulers,Vector3f(0f,0f,0f), rotationResetSpeed)
        matrix = setEuler(matrix, newRotation[0],newRotation[1],newRotation[2])

        setModelMatrix(matrix)
    }
    fun setMoveLeft(){
        moveLeft = true
    }
    fun moveLeft(dt : Float){
        var matrix = getModelMatrix()
        val newVal = clampf(matrix.m30()-speed*dt, maxLeft,maxRight)
        matrix.m30(newVal)
        setModelMatrix(matrix)

        var xBodyMat = rollParent.getModelMatrix()
        var eulers = Vector3f(0f,0f,0f)
        xBodyMat.getEulerAnglesZYX(eulers)
        var newRotation = eulers[2] + rotationLeftRightSpeed * dt
        xBodyMat = setEuler(xBodyMat, eulers[0], eulers[1],clampf(newRotation, -maxLeftRightRotation,maxLeftRightRotation))
        rollParent.setModelMatrix(xBodyMat)
    }
    fun setMoveRight(){
        moveRight = true
    }
    fun moveRight(dt : Float){
        var matrix = getModelMatrix()
        val newVal = clampf(matrix.m30()+speed*dt, maxLeft,maxRight)
        matrix.m30(newVal)
        setModelMatrix(matrix)

        var xBodyMat = rollParent.getModelMatrix()
        var eulers = Vector3f(0f,0f,0f)
        xBodyMat.getEulerAnglesZYX(eulers)
        var newRotation = eulers[2] - rotationLeftRightSpeed * dt
        xBodyMat = setEuler(xBodyMat, eulers[0], eulers[1],clampf(newRotation, -maxLeftRightRotation,maxLeftRightRotation))
        rollParent.setModelMatrix(xBodyMat)
    }
    fun horizRotationReset(dt : Float){
        var matrix = rollParent.getModelMatrix()
        var eulers = Vector3f(0f,0f,0f)
        matrix.getEulerAnglesZYX(eulers)
        var newRotation = lerpV3f(eulers,Vector3f(0f,0f,0f), rotationResetSpeed)
        matrix = setEuler(matrix, newRotation[0],newRotation[1],newRotation[2])

        rollParent.setModelMatrix(matrix)
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


    fun weaponOLmove(t : Float){
        waffeOL_Mid.setModelMatrix(lerpMatrices(waffeMidInMat,waffeMidOutMat, clampf(weaponOLposition,0f,1f)))
        waffeOL_End.setModelMatrix(lerpMatrices(waffeEndInMat,waffeEndOutMat,clampf(weaponOLposition,0f,1f)))
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
    fun weaponORmove(t : Float){
        waffeOR_Mid.setModelMatrix(lerpMatrices(waffeMidInMat,waffeMidOutMat, clampf(weaponORposition,0f,1f)))
        waffeOR_End.setModelMatrix(lerpMatrices(waffeEndInMat,waffeEndOutMat,clampf(weaponORposition,0f,1f)))
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
    fun weaponULmove(t : Float){
        waffeUL_Mid.setModelMatrix(lerpMatrices(waffeMidInMat,waffeMidOutMat,clampf(weaponULposition,0f,1f)))
        waffeUL_End.setModelMatrix(lerpMatrices(waffeEndInMat,waffeEndOutMat,clampf(weaponULposition,0f,1f)))
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
    fun weaponURmove(t : Float){
        waffeUR_Mid.setModelMatrix(lerpMatrices(waffeMidInMat,waffeMidOutMat, clampf(weaponURposition,0f,1f)))
        waffeUR_End.setModelMatrix(lerpMatrices(waffeEndInMat,waffeEndOutMat,clampf(weaponURposition,0f,1f)))
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
    }



    fun update(deltaTime: Float, time: Float){
        setDT(deltaTime)
        setT(time)

        if (moveWingOLout && wingOLout == false){
            wingOLposition = clampf(wingOLposition + deltaTime * wingRotationSpeed, 0f,1f)
            wingOLrotate(clampf(wingOLposition, 0f,1f))
        }
        if (moveWingORout && wingORout == false){
            wingORposition = clampf(wingORposition + deltaTime * wingRotationSpeed, 0f,1f)
            wingORrotate(clampf(wingORposition, 0f,1f))
        }
        if (moveWingULout && wingULout == false){
            wingULposition = clampf(wingULposition + deltaTime * wingRotationSpeed, 0f,1f)
            wingULrotate(clampf(wingULposition, 0f,1f))
        }
        if (moveWingURout && wingURout == false){
            wingURposition = clampf(wingURposition + deltaTime * wingRotationSpeed, 0f,1f)
            wingURrotate(clampf(wingURposition, 0f,1f))
        }

        if (!moveWingOLout && wingOLin == false) {
            wingOLposition = clampf(wingOLposition - deltaTime * wingRotationSpeed, 0f,1f)
            wingOLrotate(clampf(wingOLposition, 0f, 1f))
        }
        if (!moveWingORout && wingORin == false) {
            wingORposition = clampf(wingORposition - deltaTime * wingRotationSpeed, 0f,1f)
            wingORrotate(clampf(wingORposition, 0f, 1f))
        }
        if (!moveWingULout && wingULin == false) {
            wingULposition = clampf(wingULposition - deltaTime * wingRotationSpeed, 0f,1f)
            wingULrotate(clampf(wingULposition, 0f, 1f))
        }
        if (!moveWingURout && wingURin == false) {
            wingURposition = clampf(wingURposition - deltaTime * wingRotationSpeed, 0f,1f)
            wingURrotate(clampf(wingURposition, 0f, 1f))
        }

        if (wingOLout && !weaponOLout){
            weaponOLposition = clampf(weaponOLposition + deltaTime * weaponMoveSpeed, 0f,1f)
            weaponOLmove(deltaTime)
        }
        if (wingORout && !weaponORout){
            weaponORposition = clampf(weaponORposition + deltaTime * weaponMoveSpeed, 0f,1f)
            weaponORmove(deltaTime)
        }
        if (wingULout && !weaponULout){
            weaponULposition = clampf(weaponULposition + deltaTime * weaponMoveSpeed, 0f,1f)
            weaponULmove(deltaTime)
        }
        if (wingURout && !weaponURout){
            weaponURposition = clampf(weaponURposition + deltaTime * weaponMoveSpeed, 0f,1f)
            weaponURmove(deltaTime)
        }

        if (!wingOLout && !weaponOLin){
            weaponOLposition = clampf(weaponOLposition - deltaTime * weaponMoveSpeed, 0f,1f)
            weaponOLmove(deltaTime)
        }
        if (!wingORout && !weaponORin){
            weaponORposition = clampf(weaponORposition - deltaTime * weaponMoveSpeed, 0f,1f)
            weaponORmove(deltaTime)
        }
        if (!wingULout && !weaponULin){
            weaponULposition = clampf(weaponULposition - deltaTime * weaponMoveSpeed, 0f,1f)
            weaponULmove(deltaTime)
        }
        if (!wingURout && !weaponURin){
            weaponURposition = clampf(weaponURposition - deltaTime * weaponMoveSpeed, 0f,1f)
            weaponURmove(deltaTime)
        }

        if (moveUp && !moveDown) moveUp(deltaTime)
        if (moveDown && !moveUp) moveDown(deltaTime)
        if (moveLeft && !moveRight) moveLeft(deltaTime)
        if (moveRight && !moveLeft) moveRight(deltaTime)

        if (!moveDown && !moveUp) {
            vertRotationReset(deltaTime)
        }
        if (!moveLeft && !moveRight) {
            horizRotationReset(deltaTime)
        }

        moveUp = false
        moveDown = false
        moveLeft = false
        moveRight = false
    }


}