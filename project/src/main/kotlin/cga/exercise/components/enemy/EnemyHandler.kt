package cga.exercise.components.enemy

import cga.exercise.components.player.PlayerObject
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random



class EnemyHandler {

    val enemyGeo : EnemyGeo

    val enemyList = mutableListOf<Enemy>()

    var nextSpawntime = 5f
    val newSpawnMin = 2
    val newSpawnMax = 5

    init {
        enemyGeo = EnemyGeo()
    }

    fun createEnemy(){
        var randomType = Random.nextInt(0,2)
        var pos = Matrix4f()
        when (randomType){
            0 -> {addEnemy(EnemyAsteroid(this, enemyGeo, pos))}
            1 -> {addEnemy(EnemyShuttle(this, enemyGeo, pos))}
        }
    }

    fun addEnemy(newEnemy : Enemy){
        enemyList.add(newEnemy)
    }

    fun destroy(index : Int){
        enemyList.removeAt(index)
    }

    fun render(shaderProgram: ShaderProgram){
        for (each in enemyList.asReversed()) each.render(shaderProgram)
    }


    fun update(deltaTime : Float, time : Float, playerposition: Vector3f){

        if (nextSpawntime <= time){
            nextSpawntime = time + Random.nextInt(newSpawnMin,newSpawnMax).toFloat()
            createEnemy()
        }

        for (each in enemyList) {
            each.update(deltaTime, time)
            each.playerposition = playerposition
        }

        val tmp = mutableListOf<Int>()
        enemyList.forEachIndexed { index, element ->
            if (element.shouldIdie) tmp.add(index)
        }
        for (each in tmp.asReversed()) destroy(each)
    }
}