package cga.exercise.components.enemy

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.utility.BezierCurve
import cga.exercise.components.utility.QuadraticBezierCurve
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
    val movementCurve : QuadraticBezierCurve

    init {
        thisGeo.renderList = enemyGeo.shuttle.renderList
        collider1 = Collider(ColliderType.ENEMYCOLLIDER, 3f, Matrix4f(), this)
        addCollider(collider1)

        var pos = Matrix4f()
        val randomStartX = Random.nextInt(-15,15).toFloat()
        val randomStartY = Random.nextInt(-7,10).toFloat()

        val randomMidY = Random.nextInt(-7,10).toFloat()

        val randomEndX = Random.nextInt(-12,125).toFloat()
        val randomEndY = Random.nextInt(-7,8).toFloat()

//        private val maxUp = 8f
//        private val maxRight = 12.5f
//        private val maxDown = -7f

        var pList = mutableListOf<Vector3f>(
            Vector3f(-20f, 0f, -500f),
            Vector3f(40f, 0f, -250f),
            Vector3f(0f, 0f, 0f)
        )
        movementCurve = QuadraticBezierCurve(Vector3f(
            -20f, 0f, -500f),
            Vector3f(40f, 0f, -250f),
            Vector3f(0f, 0f, 0f)
        )
        this.setModelMatrix(movementCurve.getPosAndRota(posOnCurve))
    }

    override fun update(deltaTime: Float, time: Float) {
        if(!absturz){
            this.setModelMatrix(movementCurve.getPosAndRota(posOnCurve))
            posOnCurve = clampf(posOnCurve + deltaTime * shuttleSpeed, 0f,1f)
        }
        if (absturz) translate(Vector3f(0f, 0f,-1f))
        if(posOnCurve == 1f){
            shouldIdie = true
            for (each in colliderList) globalCollisionHandler.removeEnemyPart(each)
        }
        super.update(deltaTime, time)
    }

}