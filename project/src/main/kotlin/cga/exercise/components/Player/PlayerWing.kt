package cga.exercise.components.Player

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.utility.clampf
import cga.exercise.components.utility.lerpf
import cga.exercise.components.utility.setEuler
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f

class PlayerWing(playerGeo : PlayerGeo, wingType : String, modelMatrix : Matrix4f, parent: Transformable? = null) : PlayerPart(modelMatrix, parent) {

    var wingGeo = Renderable(playerGeo.wingL.renderList, Matrix4f())

    private var deltaTime = 0f
    private var time = 0f

    private var wingIn = true
    private var wingOut = false

    private var moveWingOut = false

    private var wingRotationT = 0F
    private val wingRotationSpeed = 1f
    private val maxRotation = Math.toRadians(0f)
    private var minRotation = Math.toRadians(18f)

    private var wingDestroyed = false

    private var rotationDir = 1f

    private lateinit var weapon : PlayerWeapon

    init {
        if (wingType == "OL"){
            rotationDir = -1f
            weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
            setPosition(Vector3f(-15f*0.03f,3f*0.03f,24.5f*0.03f))
            wingGeo.parent = this
            weapon.mirror(1f,0f,0f,0f,0f,0f)
            weapon.mirror(0f,1f,0f,0f,0f,0f)
            weapon.rotate(0f,0f,180f)
            weapon.setPosition(Vector3f(-74f*0.03f,30f*0.03f,-23f*0.03f))
            weapon.rotate(0f,0f, 180f)
        }else
        if (wingType == "OR"){
            weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
            wingGeo.renderList = playerGeo.wingR.renderList
            setPosition(Vector3f(15f*0.03f,3f*0.03f,24.5f*0.03f))
            wingGeo.parent = this
            minRotation *= -1f
            weapon.setPosition(Vector3f(74f*0.03f,30f*0.03f,-23f*0.03f))
        }else
        if (wingType == "UL"){
            weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
            wingGeo.renderList = playerGeo.wingR.renderList
            setPosition(Vector3f(-15f*0.03f,-2f*0.03f,24.5f*0.03f))
            wingGeo.mirror(1f,0f,0f,0f,0f,0f)
            wingGeo.mirror(0f,1f,0f,0f,0f,0f)
            wingGeo.parent = this
            minRotation *= -1f
            weapon.rotate(0f,0f,180f)
            weapon.setPosition(Vector3f(-74f*0.03f,-30f*0.03f,-23f*0.03f))
        } else
        if (wingType == "UR"){
            rotationDir = -1f
            weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
            wingGeo.mirror(1f,0f,0f,0f,0f,0f)
            wingGeo.mirror(0f,1f,0f,0f,0f,0f)
            setPosition(Vector3f(15f*0.03f,-2f*0.03f,24.5f*0.03f))
            wingGeo.parent = this
            weapon.mirror(1f,0f,0f,0f,0f,0f)
            weapon.mirror(0f,1f,0f,0f,0f,0f)
            weapon.rotate(0f,0f,180f)
            weapon.setPosition(Vector3f(74f*0.03f,-30f*0.03f,-23f*0.03f))
        }
        wingSetRotation(0f)
    }

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