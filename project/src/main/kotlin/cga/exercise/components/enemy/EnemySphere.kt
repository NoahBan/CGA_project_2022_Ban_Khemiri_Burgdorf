package cga.exercise.components.enemy

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Transformable
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random

class EnemySphere(myCreator : EnemyHandler, enemyGeo : EnemyGeo, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null
) : Enemy(myCreator, enemyGeo,modelMatrix,parent) {

    val collider1 : Collider

    val sphereSpeed = 40f


    init {
        thisGeo.renderList = enemyGeo.sphere.renderList
        collider1 = Collider(ColliderType.ENEMYCOLLIDER, 1f, Matrix4f(), this)
        addCollider(collider1)

        var pos = Matrix4f()
        val randomX = Random.nextInt(-10*10,10*10)/10f
        val randomY = Random.nextInt(-10*10,10*10)/10f
        pos.setTranslation(Vector3f(randomX, randomY, -500f))
        modelMatrix.set(pos)
    }

    override fun update(deltaTime: Float, time: Float) {
        translate(Vector3f(0f,0f,deltaTime*sphereSpeed))
        super.update(deltaTime, time)
    }


}