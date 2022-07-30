package cga.exercise.components.enemy

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.globalCollisionHandler
import org.joml.Matrix4f
import org.joml.Vector3f

abstract class Enemy (val myCreator : EnemyHandler, enemyGeo : EnemyGeo, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null) : Transformable(modelMatrix,parent) {

        var thisGeo = Renderable(mutableListOf<Mesh>(), Matrix4f(), this)
        val colliderList = mutableListOf<Collider>()

        var shouldIdie = false

        var hp = 6

        var absturz = false
        var fallSpeed = 0f
        var fallAcceleration = 0.15f

        var deathTime = 0f
        var despawnTime = 1f

        var collided = false

        init {

        }

        fun render(shaderProgram: ShaderProgram){
            for (each in colliderList) each.render(shaderProgram)
            thisGeo.render(shaderProgram)
        }

        fun addCollider(newCollider : Collider) = colliderList.add(newCollider)

        open fun update(deltaTime : Float, time : Float){
            if(hp > 0){
                for (each in colliderList) {
                    each.update()
                    if(each.collided) collided = true
                }
            }
            if (collided) hp--
            collided = false
            if(getPosition()[2] <= -10f)
            if (hp <= 0 && !absturz){
                absturz = true
                deathTime = time + despawnTime
            }
            if (absturz){
                fallSpeed += fallAcceleration * deltaTime
                translate(Vector3f(0f,-fallSpeed,0f))
                if (deathTime <= time) {
                    shouldIdie = true
                    println("Tschaui!")
                    for (each in colliderList) globalCollisionHandler.removeEnemyPart(each)
                }
            }
        }


}