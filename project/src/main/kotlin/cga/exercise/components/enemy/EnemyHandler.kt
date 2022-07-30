package cga.exercise.components.enemy

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random
import kotlin.random.nextInt

//enum class ENEMIES(val enemy : Enemy){
//    SPHERE(EnemySphere(this, EnemyGeo())),
//    SHUTTLE(1)
//}

class EnemyHandler {

    val enemyGeo : EnemyGeo

    val enemyList = mutableListOf<Enemy>()
//    val enemySphere : EnemySphere

    var nextSpawntime = 5f
    val newSpawnMin = 5
    val newSpawnMax = 8

    init {
        enemyGeo = EnemyGeo()
    }

    fun createEnemy(){
        val randomType = Random.nextInt(0,2)
        var pos = Matrix4f()
        val randomX = Random.nextInt(-10*10,10*10)/10f
        val randomY = Random.nextInt(-10*10,10*10)/10f
        pos.setTranslation(Vector3f(randomX, randomY, -10f))
        when (randomType){
            0 -> {addEnemy(EnemySphere(this, enemyGeo, pos))}
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
        for (each in enemyList) each.render(shaderProgram)
    }

//    var test = true

    fun update(deltaTime : Float, time : Float){


//         if (test) addEnemy(EnemyShuttle(this, enemyGeo, Matrix4f()))
//        test = false

        if (nextSpawntime <= time){
            nextSpawntime = time + Random.nextInt(newSpawnMin,newSpawnMax).toFloat()
            createEnemy()
        }

        for (each in enemyList) {
            each.update(deltaTime, time)
        }

        val tmp = mutableListOf<Int>()
        enemyList.forEachIndexed { index, element ->
            if (element.shouldIdie) tmp.add(index)
        }
        for (each in tmp.asReversed()) destroy(each)
    }

}