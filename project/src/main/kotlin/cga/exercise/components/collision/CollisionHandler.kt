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
    private var enemyPartList = mutableListOf<Collider>()
    private var playerPartList = mutableListOf<Collider>()
    private var enemyProjectileList = mutableListOf<Collider>()
    private var allyProjectileList = mutableListOf<Collider>()
    private var wallList = mutableListOf<Collider>()

    val colliderGeo : ColliderGeo = ColliderGeo()

    private var allCollidedObjects = mutableListOf<Collider>()

    fun checkListsWith(list1: List<Collider>, list2: List<Collider>, collisionType : String = "Radius", message : String = ""){

        for (objectInList1 in list1){
            for (objectInList2 in list2){
                if (inRadiusOf(objectInList1.getWorldPosition(),objectInList1.radius,objectInList2.getWorldPosition(),objectInList2.radius)){
                    println(message)
                    allCollidedObjects.add(objectInList1)
                    objectInList1.collided = true
                    allCollidedObjects.add(objectInList2)
                    objectInList2.collided = true
                }else{
                }
            }
        }
    }

    fun checkCollision(){

        for (collider in allCollidedObjects) {
            collider.collided = false
        }

        allCollidedObjects.removeAll(allCollidedObjects)

        checkListsWith(allyProjectileList,enemyPartList,"Radius","Ally Projectile hit Enemy Part")
        checkListsWith(enemyProjectileList,playerPartList,"Radius","Enemy Projectile hit Enemy Part")
        checkListsWith(enemyPartList,playerPartList,"Radius","Enemy Part hit Ally Part")
    }

    fun addShipPart(shipPart: Collider){
        playerPartList.add(shipPart)
    }

    fun removeShipPart(shipPart: Collider){
        playerPartList.remove(shipPart)
    }

    fun addEnemyPart(shipPart: Collider){
        enemyPartList.add(shipPart)
    }

    fun removeEnemyPart(shipPart: Collider){
        enemyPartList.remove(shipPart)
    }

    fun addAllyProjectile(projectile: Collider){
        allyProjectileList.add(projectile)
    }

    fun removeAllyProjectile(projectile: Collider){
        allyProjectileList.remove(projectile)
    }

    fun addEnemyProjectile(projectile: Collider){
        enemyProjectileList.add(projectile)
    }

    fun removeEnemyProjectile(projectile: Collider){
        enemyProjectileList.remove(projectile)
    }

    fun inRadiusOf(coordinate1 : Vector3f, range1 : Float, coordinate2 : Vector3f, range2 : Float): Boolean {
        if (coordinate1.sub(coordinate2).length() <= range1+range2 ){
            return true
        }
        return false
    }

    fun render(shaderProgram : ShaderProgram){
        for (each in enemyPartList) each.render(shaderProgram)
        for (each in playerPartList) each.render(shaderProgram)
        for (each in enemyProjectileList) each.render(shaderProgram)
        for (each in allyProjectileList) each.render(shaderProgram)
    }

    fun inRangeOf(coordinate1 : Vector3f, range1 : Float, coordinate2 : Vector3f, range2 : Float): Boolean {

        if ((coordinate1.x-range1 <= coordinate2.x+range2 && coordinate1.x-range1 >= coordinate2.x-range2) ||
            (coordinate1.x+range1 >= coordinate2.x-range2 && coordinate1.x+range1 <= coordinate2.x+range2)) {
            if ((coordinate1.y-range1 <= coordinate2.y+range2 && coordinate1.y-range1 >= coordinate2.y-range2) ||
                (coordinate1.y+range1 >= coordinate2.y-range2 && coordinate1.y+range1 <= coordinate2.y+range2)){
                if ((coordinate1.z-range1 <= coordinate2.z+range2 && coordinate1.z-range1 >= coordinate2.z-range2) ||
                    (coordinate1.z+range1 >= coordinate2.z-range2 && coordinate1.z+range1 <= coordinate2.z+range2)){
                    return true
                }
            }
        }
        return false
    }

    fun update(){
        //println("AllyProjectile List:  " + allyProjectileList.size)
        checkCollision()
    }
}