package cga.exercise.components.enemy

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random

//enum class ENEMIES(val enemy : Enemy){
//    SPHERE(EnemySphere(this, EnemyGeo())),
//    SHUTTLE(1)
//}

class EnemyHandler {

    val enemyGeo : EnemyGeo

    val enemyList = mutableListOf<Enemy>()
//    val enemySphere : EnemySphere

    var nextSpawntimeShuttle = 5f
    var newSpawnMinShuttle = 8
    var newSpawnMaxShuttle = 12

    var nextSpawntimeAsteroid = 2f
    var nextSpawntimeAsteroid2 = 4f
    var newSpawnMinAsteroid = 4
    var newSpawnMaxAsteroid = 8

    var hardMode = false

    init {
        enemyGeo = EnemyGeo()
    }

    fun createEnemyRandom(){
        var randomType = Random.nextInt(0,2)
        var pos = Matrix4f()
        when (randomType){
            0 -> {addEnemy(EnemyAsteroid(this, enemyGeo, pos))}
            1 -> {addEnemy(EnemyShuttle(this, enemyGeo, pos))}
        }
    }

    fun createEnemyShuttle(){
        var pos = Matrix4f()
        addEnemy(EnemyShuttle(this, enemyGeo, pos))
    }
    fun createEnemyAsteroid(){
        var pos = Matrix4f()
        addEnemy(EnemyAsteroid(this, enemyGeo, pos))
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

//    var test = true

    fun update(deltaTime : Float, time : Float, playerposition: Vector3f){
        if (!hardMode && time >= 30f){
            hardMode = !hardMode
            newSpawnMinShuttle = 5
            newSpawnMaxShuttle = 9
            newSpawnMinAsteroid = 3
            newSpawnMaxAsteroid = 5
        }
        if (nextSpawntimeShuttle <= time){
            nextSpawntimeShuttle = time + Random.nextInt(newSpawnMinShuttle,newSpawnMaxShuttle).toFloat()
            createEnemyShuttle()
        }
        if (nextSpawntimeAsteroid <= time){
            nextSpawntimeAsteroid = time + Random.nextInt(newSpawnMinAsteroid,newSpawnMaxAsteroid).toFloat()
            createEnemyAsteroid()
        }
        if (nextSpawntimeAsteroid2 <= time){
            nextSpawntimeAsteroid2 = time + Random.nextInt(newSpawnMinAsteroid,newSpawnMaxAsteroid).toFloat()
            createEnemyAsteroid()
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