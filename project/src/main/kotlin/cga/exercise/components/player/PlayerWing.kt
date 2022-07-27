package cga.exercise.components.player

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.utility.clampf
import cga.exercise.components.utility.lerpf
import cga.exercise.components.utility.setEuler
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f

enum class WingType (val wingType: Int){
    OL(0),
    OR(1),
    UL(2),
    UR(3)
}

class PlayerWing(playerGeo : PlayerGeo, wingType : WingType, modelMatrix : Matrix4f, parent: Transformable? = null) : PlayerPart(modelMatrix, parent) {

    var wingGeo = Renderable(playerGeo.wingL.renderList, Matrix4f())

    private var deltaTime = 0f
    private var time = 0f

    private var wingIn = true
    var wingOut = false

    private var moveWingOut = false

    private var wingRotationT = 0F
    private val wingRotationSpeed = 1f
    private val maxRotation = Math.toRadians(0f)
    private var minRotation = Math.toRadians(18f)

    private var wingDestroyed = false

    private var rotationDir = 1f

    private var weapon : PlayerWeapon

    init {
        when(wingType){
            WingType.OL -> {
                rotationDir = -1f
                weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
                setPosition(Vector3f(-0.45f, 0.09f, 0.735f))
                wingGeo.parent = this
                weapon.mirror(1f, 0f, 0f, 0f, 0f, 0f)
                weapon.mirror(0f, 1f, 0f, 0f, 0f, 0f)
                weapon.rotate(0f, 0f, 180f)
                weapon.setPosition(Vector3f(-2.22f, 0.9f, -0.69f))
                weapon.rotate(0f, 0f, 180f)
            }
            WingType.OR -> {
                weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
                wingGeo.renderList = playerGeo.wingR.renderList
                setPosition(Vector3f(0.453f,0.09f,0.735f))
                wingGeo.parent = this
                minRotation *= -1f
                weapon.setPosition(Vector3f(2.22f,0.9f,-0.69f))
            }
            WingType.UL -> {
                weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
                wingGeo.renderList = playerGeo.wingR.renderList
                setPosition(Vector3f(-0.45f,-0.09f,0.735f))
                wingGeo.mirror(1f,0f,0f,0f,0f,0f)
                wingGeo.mirror(0f,1f,0f,0f,0f,0f)
                wingGeo.parent = this
                minRotation *= -1f
                weapon.rotate(0f,0f,180f)
                weapon.setPosition(Vector3f(-2.22f,-30f*0.03f,-0.69f))
            }
            WingType.UR -> {
                rotationDir = -1f
                weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
                wingGeo.mirror(1f,0f,0f,0f,0f,0f)
                wingGeo.mirror(0f,1f,0f,0f,0f,0f)
                setPosition(Vector3f(0.45f,-0.09f,0.735f))
                wingGeo.parent = this
                weapon.mirror(1f,0f,0f,0f,0f,0f)
                weapon.mirror(0f,1f,0f,0f,0f,0f)
                weapon.rotate(0f,0f,180f)
                weapon.setPosition(Vector3f(2.22f,-0.9f,-0.69f))
            }
        }
        wingSetRotation(0f)
    }

    fun getShotPos() : Matrix4f = weapon.getShotPos()

    fun toggleWingMode(){
        moveWingOut = !moveWingOut
    }

    override fun render(shaderProgram : ShaderProgram){
        wingGeo.render(shaderProgram)
        weapon.render(shaderProgram)
    }

    fun wingSetRotation(t : Float){
        var matrix = getModelMatrix()
        matrix = setEuler(matrix, 0f, 0f, lerpf(minRotation, maxRotation,t))
        setModelMatrix(matrix)
        if (t == 0f) {
            wingIn = true
            wingOut = false
        }else
        if (t == 1f) {
            wingIn = false
            wingOut = true
        } else {
            wingIn = false
            wingOut = false
        }
    }

    fun setDT (newDt : Float){
        deltaTime = newDt
    }
    fun setT (newTime : Float){
        time = newTime
    }

    override fun update(deltaTime: Float, time: Float){
        setDT(deltaTime)
        setT(time)

        if (moveWingOut && wingOut == false){
            wingRotationT = clampf( wingRotationT + deltaTime * wingRotationSpeed, 0f,1f)
            wingSetRotation(wingRotationT)
        }

        if (!moveWingOut && wingIn == false){
            wingRotationT = clampf( wingRotationT - deltaTime * wingRotationSpeed, 0f,1f)
            wingSetRotation(wingRotationT)
        }
        weapon.update(deltaTime, time, wingOut)
    }

}