package cga.exercise.components.player

import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.utility.clampf
import cga.exercise.components.utility.lerpV3f
import cga.exercise.components.utility.lerpf
import cga.exercise.components.utility.setEuler
import org.joml.Matrix4f
import org.joml.Vector3f

class PlayerWeapon (rotationDir : Float, playerGeo : PlayerGeo, modelMatrix : Matrix4f, parent: Transformable? = null) : PlayerPart(modelMatrix, parent){

    var weaponRootGeo = Renderable(playerGeo.weaponRoot.renderList, Matrix4f())
    var weaponMidGeo = Renderable(playerGeo.weaponMid.renderList, Matrix4f())
    var weaponEndGeo = Renderable(playerGeo.weaponEnd.renderList, Matrix4f())

    private var deltaTime = 0f
    private var time = 0f

    private var weaponIn = true
    private var weaponOut = false

    private var midMinPos = -0.8f
    private var midMaxPos = -1.5f
    private var midMinRotation = Math.toRadians(-55.0 * rotationDir).toFloat()
    private var midMaxRotation = Math.toRadians(45.0 * rotationDir).toFloat()
    private var endMinPos = -0.72f
    private var endMaxPos = -1.2f

    private var weaponMoveSpeed = 1f

    private var makeWeaponActive = false

    private var weaponPosT = 0f

    init {
        weaponRootGeo.parent = this
        weaponMidGeo.parent = weaponRootGeo
        weaponEndGeo.parent = weaponMidGeo
        weaponInOut(0f)
    }

    fun getShotPos() : Matrix4f = weaponEndGeo.getWorldModelMatrix()

    fun weaponInOut(t : Float){
        var midMatrix = weaponMidGeo.getModelMatrix()
        midMatrix = setEuler(midMatrix,0f,0f, lerpf(midMinRotation, midMaxRotation, t))
        midMatrix.m32(lerpf(midMinPos, midMaxPos, t))
        weaponMidGeo.setModelMatrix(midMatrix)
        weaponEndGeo.setPosition(lerpV3f(Vector3f(0f,0f, endMinPos), Vector3f(0f,0f, endMaxPos), t))
        if (t == 1f) {
            weaponIn = false
            weaponOut = true
        }else
        if (t == 0f){
            weaponIn = true
            weaponOut = false
        }else{
            weaponIn = false
            weaponOut = false
        }
    }
    fun setDT (newDt : Float){
        deltaTime = newDt
    }
    fun setT (newTime : Float){
        time = newTime
    }

    override fun render(shaderProgram : ShaderProgram){
        weaponRootGeo.render(shaderProgram)
        weaponMidGeo.render(shaderProgram)
        weaponEndGeo.render(shaderProgram)
    }

    fun update(deltaTime: Float, time: Float, wingOut : Boolean){
        setDT(deltaTime)
        setT(time)
        setT(time)

        if(wingOut && !weaponOut){
            weaponPosT = clampf(weaponPosT + deltaTime * weaponMoveSpeed, 0f, 1f)
            weaponInOut(weaponPosT)
        }
        if(!wingOut && !weaponIn){
            weaponPosT = clampf(weaponPosT - deltaTime * weaponMoveSpeed, 0f, 1f)
            weaponInOut(weaponPosT)
        }
    }


}