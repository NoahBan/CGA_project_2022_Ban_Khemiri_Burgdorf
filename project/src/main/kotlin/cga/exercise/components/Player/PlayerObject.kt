package cga.exercise.components.Player

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.utility.*
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f

class PlayerObject(modelMatrix : Matrix4f, parent: Transformable? = null) : Transformable(modelMatrix, parent) {

    private var deltaTime = 0f
    private var time = 0f

    private val playerGeo = PlayerGeo()

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

    var rollParent = Transformable(Matrix4f(), this)

    val playerPartsList : MutableList<PlayerPart>

    private val body_Part : PlayerBody
    private val wingOL : PlayerWing
    private val wingOR : PlayerWing
    private val wingUR : PlayerWing
    private val wingUL : PlayerWing

    var moveUp = false
    var moveDown = false
    var moveLeft = false
    var moveRight = false

    init {
        body_Part = PlayerBody(playerGeo,Matrix4f(),rollParent)
        wingOL = PlayerWing(playerGeo,"OL",Matrix4f(),rollParent)
        wingOR = PlayerWing(playerGeo,"OR",Matrix4f(),rollParent)
        wingUR = PlayerWing(playerGeo,"UR",Matrix4f(),rollParent)
        wingUL = PlayerWing(playerGeo,"UL",Matrix4f(),rollParent)
        playerPartsList = mutableListOf(
            body_Part, wingOL, wingOR, wingUR, wingUL
        )
    }

    fun render(shaderProgram : ShaderProgram){
        for (each in playerPartsList) each.render(shaderProgram)
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
    fun setMoveDown() {
        moveDown = true
    }

    fun moveUpDown(dt : Float, dir : Float){
        var matrix = getModelMatrix()
        val newVal = clampf(matrix.m31()+speed*dt* dir, maxDown,maxUp)
        matrix.m31(newVal)

        var eulers = Vector3f(0f,0f,0f)
        matrix.getEulerAnglesZYX(eulers)
        var newRotation = eulers[0] + rotationUpDownSpeed * dt * dir
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
    fun setMoveRight(){
        moveRight = true
    }

    fun moveLeftRight(dt : Float, dir: Float){
        var matrix = getModelMatrix()
        val newVal = clampf(matrix.m30()-speed*dt* dir, maxLeft,maxRight)
        matrix.m30(newVal)
        setModelMatrix(matrix)

        var xBodyMat = rollParent.getModelMatrix()
        var eulers = Vector3f(0f,0f,0f)
        xBodyMat.getEulerAnglesZYX(eulers)
        var newRotation = eulers[2] + rotationLeftRightSpeed * dt * dir
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

    fun toggleWingMode(){
        wingOL.toggleWingMode()
        wingOR.toggleWingMode()
        wingUL.toggleWingMode()
        wingUR.toggleWingMode()
    }

    fun update(deltaTime: Float, time: Float){
        setDT(deltaTime)
        setT(time)

        if (moveUp && !moveDown) moveUpDown(deltaTime, 1f)
        if (moveDown && !moveUp) moveUpDown(deltaTime, -1f)
        if (moveLeft && !moveRight) moveLeftRight(deltaTime, 1f)
        if (moveRight && !moveLeft) moveLeftRight(deltaTime, -1f)

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

        for (each in playerPartsList) each.update(deltaTime, time)
    }


}