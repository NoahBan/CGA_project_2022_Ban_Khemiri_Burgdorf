package cga.exercise.components.enemy

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.utility.BezierCurve
import cga.exercise.components.utility.clampf
import cga.exercise.game.globalCollisionHandler
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random

class EnemyShuttle(myCreator : EnemyHandler, enemyGeo : EnemyGeo, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null
) : Enemy(myCreator, enemyGeo,modelMatrix,parent) {

    val collider1 : Collider
    val shuttleSpeed = 0.1f

    var posOnCurve = 0.005f
    val movementCurve : BezierCurve

    init {
        for (each in enemyGeo.shuttle.renderList) thisGeo.renderList.add(each)
        collider1 = Collider(ColliderType.ENEMYCOLLIDER, 3f, Matrix4f(), this)
        addCollider(collider1)

        var pos = Matrix4f()
        val randomStartX = Random.nextInt(-15*1000,15*1000)/1000f
        val randomStartY = Random.nextInt(-7*1000,10*1000)/1000f

        val randomMidY = Random.nextInt(-7*1000,10*10000)/1000f

        val randomEndX = Random.nextInt(-12*1000,125*1000)/1000f
        val randomEndY = Random.nextInt(-7*1000,8*1000)/1000f

//        private val maxUp = 8f
//        private val maxRight = 12.5f
//        private val maxDown = -7f

        movementCurve = BezierCurve(
            Vector3f(randomStartX, randomStartY, -1000f),
            Vector3f(-randomStartX*1.5f, randomMidY, -500f),
            Vector3f(-randomStartX, randomEndY, 5f)
        )

        this.setModelMatrix(movementCurve.calcPosAndRota(posOnCurve))

    }

    override fun update(deltaTime: Float, time: Float) {
        if(!absturz){
            this.setModelMatrix(movementCurve.calcPosAndRota(posOnCurve))
            posOnCurve = clampf(posOnCurve + deltaTime * shuttleSpeed, 0f,1f)
        }
        if (absturz) translate(Vector3f(0f, 0f,-1f))
        if(posOnCurve == 1f){
            println("stirb!")
            shouldIdie = true
            for (each in colliderList) globalCollisionHandler.removeEnemyPart(each)
        }
        super.update(deltaTime, time)
    }

}