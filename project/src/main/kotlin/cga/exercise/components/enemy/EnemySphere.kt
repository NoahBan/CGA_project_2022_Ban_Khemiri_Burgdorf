package cga.exercise.components.enemy

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Transformable
import org.joml.Matrix4f
import org.joml.Vector3f

class EnemySphere(myCreator : EnemyHandler, enemyGeo : EnemyGeo, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null
) : Enemy(myCreator, enemyGeo,modelMatrix,parent) {

    val collider1 : Collider


    init {
        for (each in enemyGeo.sphere.renderList) thisGeo.renderList.add(each)
        collider1 = Collider(ColliderType.ENEMYCOLLIDER, 1f, Matrix4f(), this)
        addCollider(collider1)
    }



}