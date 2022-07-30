package cga.exercise.components.enemy

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.game.globalCollisionHandler
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random

class EnemyAsteroid(myCreator : EnemyHandler, enemyGeo : EnemyGeo, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null
) : Enemy(myCreator, enemyGeo,modelMatrix,parent) {

    val collider1 : Collider

    val rotationOffset = Transformable(Matrix4f(),this)
    val scaleOffset = Transformable(Matrix4f(),rotationOffset)

    val sphereSpeed = 40f

    var scale = 1f

    val maxRotationSpeed = 360*2
    val randomPitch : Float
    val randomRoll : Float
    val randomYaw : Float


    init {
        scale = Random.nextInt(8,20)/10f
        println(scale)

        val asteroidType = Random.nextInt(0,3)

        when(asteroidType){
            0 ->{
                thisGeo = Renderable(enemyGeo.asteroid1.renderList,Matrix4f(), scaleOffset)
            }
            1 ->{
                thisGeo = Renderable(enemyGeo.asteroid2.renderList,Matrix4f(), scaleOffset)
            }
            else ->{
                thisGeo = Renderable(enemyGeo.asteroid3.renderList,Matrix4f(), scaleOffset)
            }
        }
        collider1 = Collider(ColliderType.ENEMYCOLLIDER, scale, Matrix4f(), rotationOffset)
        addCollider(collider1)

        scaleOffset.scale(Vector3f(scale,scale,scale))

        randomPitch = Math.toRadians(Random.nextInt(1,maxRotationSpeed).toDouble()).toFloat()
        randomRoll = Math.toRadians(Random.nextInt(1,maxRotationSpeed).toDouble()).toFloat()
        randomYaw = Math.toRadians(Random.nextInt(1,maxRotationSpeed).toDouble()).toFloat()


        var pos = Matrix4f()
        val randomX = Random.nextInt(-10*10,10*10)/10f
        val randomY = Random.nextInt(-10*10,10*10)/10f
        pos.setTranslation(Vector3f(randomX, randomY, -500f))
        modelMatrix.set(pos)
    }

    override fun update(deltaTime: Float, time: Float) {
        rotationOffset.rotate(randomPitch*deltaTime,randomRoll*deltaTime,randomYaw*deltaTime)
        translate(Vector3f(0f,0f,deltaTime*sphereSpeed))
        if(getPosition()[2] >= 2f){
            shouldIdie = true
            for (each in colliderList) globalCollisionHandler.removeEnemyPart(each)
        }
        super.update(deltaTime, time)
    }


}