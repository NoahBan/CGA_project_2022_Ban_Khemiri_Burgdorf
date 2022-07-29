package cga.exercise.components.enemy

import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random

class EnemyHandler {

    val enemyGeo : EnemyGeo

    val enemyList = mutableListOf<Enemy>()

    init {
        enemyGeo = EnemyGeo()
    }

    fun createEnemy(){
        var pos = Matrix4f()
        val randomX = Random.nextInt(-10*10,10*10)/10f
        val randomY = Random.nextInt(-10*10,10*10)/10f
        pos.setTranslation(Vector3f(0f, 0f, -10f))
        addEnemy(EnemySphere(this, EnemyGeo(), pos))
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

    fun update(deltaTime : Float, time : Float){
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