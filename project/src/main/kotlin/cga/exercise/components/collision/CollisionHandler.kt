package cga.exercise.components.collision

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f


class CollisionHandler {

    //CollisionModels
    private var enemyList = mutableListOf<Renderable>()
    var enemyCollision = mutableListOf<Vector4f>(
        Vector4f(0f,0.7f,0f,1f))

    private var allyList = mutableListOf<Renderable>()
    var allyCollision = mutableListOf<Vector4f>(
        Vector4f(0f,0.7f,0f,10f))

    private var enemyProjectileList = mutableListOf<Renderable>()
    var enemyProjectileCollision = mutableListOf<Vector4f>(
        Vector4f(0f,0f,0f,1f))

    private var allyProjectileList = mutableListOf<Renderable>()
    var allyProjectileCollision = mutableListOf<Vector4f>(
        Vector4f(0f,0f,0f,1f))

    private var allObjects = listOf(enemyList,allyList,enemyProjectileList,allyProjectileList)

    fun checkCollision(): MutableList<Renderable> {
        var collidedList = mutableListOf<Renderable>()

        //Check Ally-Projectile hitting Enemy
        for (projectile in allyProjectileList) {
            for (enemy in enemyList){
                for (projectileHitbox in allyProjectileCollision){
                    for (enemyCollision in enemyCollision){
                        if (inRadiusOf(projectile.getWorldPosition(),1f, Vector3f(enemy.getWorldPosition().x+enemyCollision[0],enemy.getWorldPosition().y+enemyCollision[1],enemy.getWorldPosition().z+enemyCollision[2]),enemyCollision[3])){
                            println("Ally Projectile hit Enemy")
                            collidedList.add(enemy)
                            collidedList.add(projectile)
                            break
                        }else{
                        }
                    }
                }
            }
        }
        //Check Enemy-Projectile hitting Ally
        for (projectile in enemyProjectileList) {
            for (ally in allyList){
                for (projectileHitbox in enemyProjectileCollision){
                    for (allyCollision in allyCollision){
                        if (inRadiusOf(projectile.getWorldPosition(),1f, Vector3f(ally.getWorldPosition().x+allyCollision[0],ally.getWorldPosition().y+allyCollision[1],ally.getWorldPosition().z+allyCollision[2]),allyCollision[3])){
                            println("Enemy Projectile hit Ally")
                            collidedList.add(ally)
                            collidedList.add(projectile)
                            break
                        }else{
                        }
                    }
                }
            }
        }
        return collidedList
    }

    fun addAlly(ally: Renderable){
        allyList.add(ally)
    }

    fun addEnemy(enemy: Renderable){
        enemyList.add(enemy)
    }

    fun addAllyProjectile(projectile: Renderable){
        allyProjectileList.add(projectile)
    }

    fun addEnemyProjectile(projectile: Renderable){
        enemyProjectileList.add(projectile)
    }

    fun inRangeOf(coordinate1 : Float, range1 : Float, coordinate2 : Float, range2 : Float): Boolean {
        if ((coordinate1-range1 <= coordinate2+range2 && coordinate1-range1 >= coordinate2-range2) ||
            (coordinate1+range1 >= coordinate2-range2 && coordinate1+range1 <= coordinate2+range2)) {
            return true
        }
        return false
    }

    fun inRadiusOf(coordinate1 : Vector3f, range1 : Float, coordinate2 : Vector3f, range2 : Float): Boolean {
        if (coordinate1.sub(coordinate2).length() <= range1+range2 ){
            return true
        }
        return false
    }

    fun showCollision(staticShader : ShaderProgram, hitBoxObject: Renderable){

        for (list in allObjects){
            for (ally in allyList){
                for (Vec in allyCollision){
                    hitBoxObject.scaling(Vector3f(Vec[3],Vec[3],Vec[3]))
                    hitBoxObject.translate(Vector3f(ally.getWorldPosition().x+Vec[0],ally.getWorldPosition().y+Vec[1],ally.getWorldPosition().z+Vec[2]))
                    hitBoxObject.render(staticShader)
                }
            }
        }

        for (enemy in enemyList){
            for (Vec in enemyCollision){
                hitBoxObject.scaling(Vector3f(Vec[3],Vec[3],Vec[3]))
                hitBoxObject.setPosition(Vector3f(enemy.getWorldPosition().x+Vec[0],enemy.getWorldPosition().y+Vec[1],enemy.getWorldPosition().z+Vec[2]))
                hitBoxObject.render(staticShader)
            }
        }

        for (projectile in enemyProjectileList){
            for (Vec in allyProjectileCollision){
                hitBoxObject.scaling(Vector3f(Vec[3],Vec[3],Vec[3]))
                hitBoxObject.setPosition(Vector3f(projectile.getWorldPosition().x+Vec[0],projectile.getWorldPosition().y+Vec[1],projectile.getWorldPosition().z+Vec[2]))
                hitBoxObject.render(staticShader)
            }
        }

        for (projectile in allyProjectileList){
            for (Vec in enemyProjectileCollision){
                hitBoxObject.scaling(Vector3f(Vec[3],Vec[3],Vec[3]))
                hitBoxObject.setPosition(Vector3f(projectile.getWorldPosition().x+Vec[0],projectile.getWorldPosition().y+Vec[1],projectile.getWorldPosition().z+Vec[2]))
                hitBoxObject.render(staticShader)
            }
        }
    }
}