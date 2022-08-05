package cga.exercise.components.enemy

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.effects.EmiterType
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.utility.BezierCurve
import cga.exercise.components.utility.clampf
import cga.exercise.game.globalEmitterHandler
import cga.exercise.game.globalCollisionHandler
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random

class EnemyShuttle(myCreator : EnemyHandler, enemyGeo : EnemyGeo, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null
) : Enemy(myCreator, enemyGeo,modelMatrix,parent) {

    val collider1 : Collider
    val shuttleSpeed = 0.05f

    var scalefactor = 1f
    var posOnCurve = 0.005f
    val movementCurve : BezierCurve
    var explosion = false
    var start = Vector3f(1f)
    var startdirection = 1f

    var ende = Vector3f(0f)

    init {

        thisGeo.renderList = enemyGeo.shuttle.renderList
        var scaleRoot = Math.sqrt(Math.sqrt(scalefactor.toDouble())).toFloat()
        thisGeo.scale(Vector3f(scaleRoot))
        collider1 = Collider(ColliderType.ENEMYCOLLIDER, 4.5f * scaleRoot, Matrix4f(), this)
        addCollider(collider1)

        var randomType = Random.nextInt(1,3)
        when (randomType){
            1 -> startdirection = 1f
            2 -> startdirection = -1f
        }
        println(randomType)
        start = Vector3f(startdirection * 200f, Random.nextInt(0,20).toFloat(), -500f)
        movementCurve = BezierCurve(
            mutableListOf(start,
                Vector3f(Random.nextInt(-70,70).toFloat(), Random.nextInt(-5,40).toFloat(), -450f),
                Vector3f(Random.nextInt(-70,70).toFloat(), Random.nextInt(-5,30).toFloat(), -330f),
                ende)
        )

        this.setModelMatrix(movementCurve.getPosAndRota(posOnCurve))

    }
    override fun update(deltaTime: Float, time: Float) {
        movementCurve.pointList[movementCurve.pointList.lastIndex] = playerposition
        playerposition.z = 0f

        if(!absturz){
            this.setModelMatrix(movementCurve.getPosAndRota(posOnCurve))
            posOnCurve = clampf(posOnCurve + deltaTime * shuttleSpeed, 0f,1f)
        }
        if (absturz){
            translate(Vector3f(0f, 0f,-1f))
            alpha -= 0.01f
            for (each in colliderList) globalCollisionHandler.removeEnemyPart(each)
        }

        if (hp <= 0 && !explosion){
            globalEmitterHandler.addEmitterType(EmiterType.Supernova,this.getWorldPosition().x,this.getWorldPosition().y,this.getWorldPosition().z)
            explosion = true
        }

        if(posOnCurve == 1f){
            shouldIdie = true
            for (each in colliderList) globalCollisionHandler.removeEnemyPart(each)
        }
        super.update(deltaTime, time)
    }

}