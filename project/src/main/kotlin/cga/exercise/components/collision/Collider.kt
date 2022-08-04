package cga.exercise.components.collision

import cga.exercise.components.geometry.*
import cga.exercise.components.player.PlayerWeapon
import cga.exercise.components.player.WingType
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.exercise.game.globalCollisionHandler
import cga.exercise.game.globalLightHandler
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

enum class ColliderType (val colliderType: Int){
    PLAYERCOLLIDER(0),
    PLAYERPROJICTILECOLLIDER(1),
    ENEMYCOLLIDER(2),
    ENEMYPROJECTILECOLLIDER(3)
}

class Collider (colliderType : ColliderType,var radius : Float = 1f, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable ?= null) : Transformable(modelMatrix, parent){

    var collisionObject : Renderable

    var collided = false

    var collidedMesh : Mesh
    var notCollidedMesh : Mesh


    init {
        when(colliderType){
            ColliderType.PLAYERCOLLIDER -> {
                globalCollisionHandler.addShipPart(this)
                collidedMesh = globalCollisionHandler.colliderGeo.redCollisionSphereGeo
                notCollidedMesh = globalCollisionHandler.colliderGeo.blueCollisionSphereGeo
            }
            ColliderType.PLAYERPROJICTILECOLLIDER -> {
                globalCollisionHandler.addAllyProjectile(this)
                collidedMesh = globalCollisionHandler.colliderGeo.redCollisionSphereGeo
                notCollidedMesh = globalCollisionHandler.colliderGeo.greenCollisionSphereGeo
            }
            ColliderType.ENEMYCOLLIDER -> {
                globalCollisionHandler.addEnemyPart(this)
                collidedMesh = globalCollisionHandler.colliderGeo.redCollisionSphereGeo
                notCollidedMesh = globalCollisionHandler.colliderGeo.greenCollisionSphereGeo
            }
            ColliderType.ENEMYPROJECTILECOLLIDER -> {
                globalCollisionHandler.addEnemyProjectile(this)
                collidedMesh = globalCollisionHandler.colliderGeo.redCollisionSphereGeo
                notCollidedMesh = globalCollisionHandler.colliderGeo.greenCollisionSphereGeo
            }
        }
        collisionObject = Renderable(mutableListOf(notCollidedMesh),Matrix4f(),this)
        collisionObject.scale(Vector3f(radius,radius,radius))
    }

    fun showCollision(){
        if(collided) collisionObject.renderList = mutableListOf(collidedMesh)
        if(!collided) collisionObject.renderList = mutableListOf(notCollidedMesh)
    }

    fun render(shaderProgram : ShaderProgram){
        collisionObject.render(shaderProgram)
    }

    fun update(){
        showCollision()
    }



//    fun setCollidedMat(){
//        importedMesh.material = matRed
//    }
//
//    fun setStandardMat(){
//        if(colorType == "White")
//            importedMesh.material = matWhite
//        if(colorType == "Blue")
//            importedMesh.material = matBlue
//        if(colorType == "Red")
//            importedMesh.material = matRed
//        if(colorType == "Green")
//            importedMesh.material = matGreen
//    }
}