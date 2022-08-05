package cga.exercise.components.enemy

import cga.exercise.components.collision.Collider
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.globalCollisionHandler
import cga.exercise.game.globalDepthSortRenderer
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

abstract class Enemy (val myCreator : EnemyHandler, enemyGeo : EnemyGeo, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null) : Transformable(modelMatrix,parent) {

        var thisGeo = Renderable(mutableListOf<Mesh>(), Matrix4f(), this)
        val colliderList = mutableListOf<Collider>()
        var playerposition = Vector3f(0f)
        var shouldIdie = false

        var hp = 6

        var absturz = false
        var fallSpeed = 0f
        var fallAcceleration = 0.15f

        var deathTime = 0f
        var despawnTime = 1f

        var collided = false
        var alpha = 1f

        init {

        }

        fun render(shaderProgram: ShaderProgram){

                for (each in thisGeo.renderList){
                    each.material?.opacityMultiplier = alpha
                }

                val cloneEnemy = Renderable(mutableListOf<Mesh>(), Matrix4f())
                cloneEnemy.setModelMatrix(thisGeo.getWorldModelMatrix())

                for (each in thisGeo.renderList){
                    cloneEnemy.renderList.add(each.getMeshCopyMaterial())
                }

                globalDepthSortRenderer.addRenderable(cloneEnemy)

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
                for (each in colliderList) globalCollisionHandler.removeEnemyPart(each)
            }
            if (absturz){
                fallSpeed += fallAcceleration * deltaTime
                translate(Vector3f(0f,-fallSpeed,0f))
                if (deathTime <= time) {
                    shouldIdie = true
                    println("Tschaui!")
                }
            }
        }


}