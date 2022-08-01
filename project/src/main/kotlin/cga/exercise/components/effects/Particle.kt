package cga.exercise.components.effects

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.game.cameraHandler
import org.joml.Matrix4f
import org.joml.Vector3f
import java.util.Vector

class Particle (
    renderList : MutableList<Mesh>,
    modelMatrix : Matrix4f = Matrix4f(),
    x : Float,
    y : Float,
    z : Float,
    minScale : Float = 1f,
    maxScale : Float = 1f,
    minSpreadVec : Vector3f = Vector3f(0f,0.01f,0f),
    maxSpreadVec : Vector3f = Vector3f(0f,0.01f,0f),
    spreadRadius : Float = 10f,
    acceleration : Float = 1.0f,
    accelerationVector: Vector3f = Vector3f(0f,0f,0f),
    parent: Transformable ?= null)
    : Renderable(renderList,modelMatrix, parent) {

    var spreadVec = Vector3f()
    var scalingVec = Vector3f()
    var spreadAccelVec = Vector3f(accelerationVector)
    var accel = Vector3f(acceleration,acceleration,acceleration)

    init {
        val randomScale = minScale + Math.random() * (maxScale-minScale)
        scalingVec = Vector3f(randomScale.toFloat())

        var randomXInRange = -spreadRadius + Math.random() * (spreadRadius-(-spreadRadius))
        var randomYInRange = -spreadRadius + Math.random() * (spreadRadius-(-spreadRadius))
        var randomZInRange = -spreadRadius + Math.random() * (spreadRadius-(-spreadRadius))

        var randomXLength = minSpreadVec.x + Math.random() * (maxSpreadVec.x-minSpreadVec.x)
        var randomYLength = minSpreadVec.y + Math.random() * (maxSpreadVec.y-minSpreadVec.y)
        var randomZLength = minSpreadVec.z + Math.random() * (maxSpreadVec.z-minSpreadVec.z)

        spreadVec = Vector3f(randomXLength.toFloat(),randomYLength.toFloat(),randomZLength.toFloat())

        spreadVec.rotateX(Math.toRadians(randomXInRange).toFloat())
        spreadVec.rotateY(Math.toRadians(randomYInRange).toFloat())
        spreadVec.rotateZ(Math.toRadians(randomZInRange).toFloat())

        //Zur Kamera ausrichten
        setCorrectRotation()

        //Scaling
        this.scale(scalingVec)

        //Rotation
        val randomRoll = 0 + Math.random() * (0-360)
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

    fun spreadTo(){
        //Spread To
        val particleX = this.getWorldPosition().x
        val particleY = this.getWorldPosition().y
        val particleZ = this.getWorldPosition().z

        spreadAccelVec = spreadAccelVec.mul(accel)

        this.setPosition(Vector3f(particleX+spreadVec.x+spreadAccelVec.x,particleY+spreadVec.y+spreadAccelVec.y,particleZ+spreadVec.z+spreadAccelVec.z))
    }

    fun update(){
        setCorrectRotation()
        spreadTo()
    }
}