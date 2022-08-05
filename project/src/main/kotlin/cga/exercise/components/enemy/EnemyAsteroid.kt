package cga.exercise.components.enemy

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.utility.BezierCurve
import cga.exercise.components.utility.clampf
import cga.exercise.game.globalCollisionHandler
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random

class EnemyAsteroid(myCreator : EnemyHandler, enemyGeo : EnemyGeo, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null
) : Enemy(myCreator, enemyGeo,modelMatrix,parent) {

    val collider1 : Collider

    val rotationOffset = Transformable(Matrix4f(),this)
    val scaleOffset = Transformable(Matrix4f(),rotationOffset)
    val movementLine : BezierCurve
    var posOnLine = 0.005f

    val asteroidspeed = 0.1f

    var scale = 1f

    val maxRotationSpeed = 360*2
    val randomPitch : Float
    val randomRoll : Float
    val randomYaw : Float
    var fadein = true

    init {
        scale = Random.nextInt(8,40)/10f

        alpha = 0f
        val asteroidType = Random.nextInt(0,3)
        scaleOffset.scale(Vector3f(scale,scale,scale))

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
        var scaleRoot = Math.sqrt(Math.sqrt(scale.toDouble())).toFloat()
        thisGeo.scale(Vector3f(scaleRoot))

        randomPitch = Math.toRadians(Random.nextInt(1,maxRotationSpeed).toDouble()).toFloat()
        randomRoll = Math.toRadians(Random.nextInt(1,maxRotationSpeed).toDouble()).toFloat()
        randomYaw = Math.toRadians(Random.nextInt(1,maxRotationSpeed).toDouble()).toFloat()


        var spawnpoint = Vector3f()
        val randomX = Random.nextInt(-50,50).toFloat()
        val randomY = Random.nextInt(-10*10,10*10)/10f
        spawnpoint = Vector3f(randomX, randomY, -500f)

        movementLine = BezierCurve(
            mutableListOf(
                spawnpoint,
                Vector3f(0f)
            )
        )
        this.setPosition(movementLine.getPos(posOnLine))
    }

    override fun update(deltaTime: Float, time: Float) {
        rotationOffset.rotate(randomPitch * deltaTime,randomRoll * deltaTime,randomYaw * deltaTime)

        if (posOnLine <= 0.10f) movementLine.pointList[movementLine.pointList.lastIndex] = playerposition
        this.setPosition(movementLine.getPos(posOnLine))

        posOnLine = clampf(posOnLine + deltaTime * asteroidspeed, 0f,1f)
        if (fadein) {
            alpha += 0.01f
            if (alpha >= 1f) {
                alpha = 1f
                fadein = false
            }
        }

        if (absturz) {
            alpha -= 0.01f
            for (each in colliderList) globalCollisionHandler.removeEnemyPart(each)
        }

        if(getPosition()[2] >= 2f || posOnLine == 1f){
            shouldIdie = true
            for (each in colliderList) globalCollisionHandler.removeEnemyPart(each)
        }
        super.update(deltaTime, time)
    }

}