package cga.exercise.components.effects

import cga.exercise.components.geometry.Transformable
import cga.exercise.game.cameraHandler
import org.joml.Matrix4f
import org.joml.Vector3f
import java.util.Vector

class Particle (
    modelMatrix : Matrix4f = Matrix4f(),
    x : Float,
    y : Float,
    z : Float,
    minScale : Float = 1f,
    maxScale : Float = 1f,
    minSpreadVec : Vector3f = Vector3f(0f,0.01f,0f),
    maxSpreadVec : Vector3f = Vector3f(0f,0.01f,0f),
    spreadRadius : Float = 10f,
    var acceleration : Float = 1.0f,
    accelerationVector: Vector3f = Vector3f(0f,0f,0f),
    var minDeathTime : Float = 1.0f,
    var maxDeathTime : Float = 1.0f,
    var sizeOverLife : Float = 1.0f,
    var alphaOverLife : Float = 1.0f,
    var colorLife : Float = 1.0f,
    parent: Transformable ?= null)
    : Transformable(modelMatrix, parent) {

    var spreadVec = Vector3f()
    var scalingVec = Vector3f()
    var spreadAccelVec = Vector3f(accelerationVector)
    var accel = Vector3f(acceleration)
    var sizeOverLifeVec = Vector3f(sizeOverLife)
    var death = 5f
    var firstUpdate = true
    var newAlpha = 1f
    var newColorScaling = 1f

    init {
        //Scale between min and max
        val randomScale = minScale + Math.random() * (maxScale-minScale)
        scalingVec = Vector3f(randomScale.toFloat())
        this.scale(scalingVec)

        //Generate New Vector within Length of min and max spread
        var randomXLength = randomBetween(minSpreadVec.x,maxSpreadVec.x)
        var randomYLength = randomBetween(minSpreadVec.y,maxSpreadVec.y)
        var randomZLength = randomBetween(minSpreadVec.z,maxSpreadVec.z)
        spreadVec = Vector3f(randomXLength.toFloat(),randomYLength.toFloat(),randomZLength.toFloat())

        //Generate New Vector within radius of spread vector
        var randomXInRange = randomBetween(-spreadRadius,spreadRadius)
        var randomYInRange = randomBetween(-spreadRadius,spreadRadius)
        var randomZInRange = randomBetween(-spreadRadius,spreadRadius)
        spreadVec.rotateX(Math.toRadians(randomXInRange).toFloat())
        spreadVec.rotateY(Math.toRadians(randomYInRange).toFloat())
        spreadVec.rotateZ(Math.toRadians(randomZInRange).toFloat())

        //Align Cam
        setCorrectRotation()
        //Random Roll
        val randomRoll = randomBetween(0f,360f)
        val currentRota = this.getRotation()
        this.rotate(currentRota.x,currentRota.y,currentRota.z+randomRoll.toFloat())
        this.setPosition(Vector3f(x, y, z))
    }

    fun setCorrectRotation(){
        this.rotate(0f,0f,0f)

        val eye = this.getPosition()
        val center = cameraHandler.getActiveCamera().getWorldPosition()
        val up  = this.getYAxis()

        var matrix = Matrix4f()
        matrix = (matrix.lookAt(eye,center,up)).invert()
        this.setXAxis(Vector3f(matrix.m00(),matrix.m01(),matrix.m02()).normalize())
        this.setYAxis(Vector3f(matrix.m10(),matrix.m11(),matrix.m12()).normalize())
        this.setZAxis(Vector3f(matrix.m20(),matrix.m21(),matrix.m22()).normalize())

        this.scale(scalingVec)
    }

    fun spreadTo(dt : Float){
        val dtMultiplier = dt*144

        //Spread To
        val particleX = this.getWorldPosition().x
        val particleY = this.getWorldPosition().y
        val particleZ = this.getWorldPosition().z

        if (spreadAccelVec == Vector3f(0f,0f,0f)){
            this.setPosition(this.getWorldPosition().add(spreadVec.mul(accel)))
            spreadVec.mul(acceleration)
        }else{
            this.setPosition(Vector3f(
                particleX+(spreadVec.x+spreadAccelVec.x)*dtMultiplier,
                particleY+(spreadVec.y+spreadAccelVec.y)*dtMultiplier,
                particleZ+(spreadVec.z+spreadAccelVec.z)*dtMultiplier))
            spreadAccelVec = spreadAccelVec.mul(accel.mul(dtMultiplier))
        }
    }

    fun update(t : Float, dt : Float){
        val dtMultiplier = dt*144

        setCorrectRotation()
        spreadTo(dt)

        this.scale(Vector3f(sizeOverLifeVec))
        sizeOverLifeVec.mul(sizeOverLife * dtMultiplier)

        if (firstUpdate){
            firstUpdate = false
            var deathTime = minDeathTime + Math.random() * (maxDeathTime-minDeathTime)
            death = (deathTime+t).toFloat()
        }

        if (alphaOverLife != 1f){
            newAlpha = newAlpha * alphaOverLife * dtMultiplier
        }

        if (alphaOverLife != 1f){
            newColorScaling = newColorScaling * colorLife * dtMultiplier
        }
    }

    fun randomBetween(min:Float, max:Float): Double {
        return (min + Math.random() * (max-min))
    }
}