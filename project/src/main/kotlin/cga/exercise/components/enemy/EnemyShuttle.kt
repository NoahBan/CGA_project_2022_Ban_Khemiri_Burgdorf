package cga.exercise.components.enemy

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.effects.EmiterType
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.player.PlayerObject
import cga.exercise.components.utility.QuadraticBezierCurve
import cga.exercise.components.utility.BezierCurve
import cga.exercise.components.utility.clampf
import cga.exercise.game.emitterHandler
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
        // var scaleRoot = Math.sqrt(Math.sqrt(scalefactor.toDouble())).toFloat()
        // thisGeo.scale(Vector3f(scaleRoot))
        // collider1 = Collider(ColliderType.ENEMYCOLLIDER, 3f * scalefactor*2, Matrix4f(), this)
        //addCollider(collider1)

        thisGeo.renderList = enemyGeo.shuttle.renderList
        var scaleRoot = Math.sqrt(Math.sqrt(scalefactor.toDouble())).toFloat()
        thisGeo.scale(Vector3f(scaleRoot))
        collider1 = Collider(ColliderType.ENEMYCOLLIDER, 4.5f * scaleRoot, Matrix4f(), this)
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
            Vector3f(Random.nextInt(-30,30).toFloat(), 0f, -250f),
            Vector3f(0f, 0f, 100f)
        )
        var randomType = Random.nextInt(1,3)
        when (randomType){
            1 -> startdirection = 1f
            2 -> startdirection = -1f
        }
        println(randomType)
        start = Vector3f(startdirection * 200f, 0f, -500f)
        movementCurve = BezierCurve(
            mutableListOf(start,
                Vector3f(Random.nextInt(-40,40).toFloat(), 0f, -450f),
                Vector3f(Random.nextInt(-40,40).toFloat(), -1f, -330f),
                ende)
        )


        this.setModelMatrix(movementCurve.getPosAndRota(posOnCurve))

    }
    override fun update(deltaTime: Float, time: Float) {
        movementCurve.pointList[movementCurve.pointList.lastIndex] = playerposition
        playerposition.z = 0f
        //println("Endpos "+ ende)

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
            emitterHandler.addEmitterType(EmiterType.Supernova,this.getWorldPosition().x,this.getWorldPosition().y,this.getWorldPosition().z)
            explosion = true
        }

        if(posOnCurve == 1f){
            shouldIdie = true
            for (each in colliderList) globalCollisionHandler.removeEnemyPart(each)
        }
        super.update(deltaTime, time)
    }

}