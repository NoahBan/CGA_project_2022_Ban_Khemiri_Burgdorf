package cga.exercise.components.projectile

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.AttenuationType
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.globalCollisionHandler
import cga.exercise.game.globalLightHandler
import org.joml.Matrix4f
import org.joml.Vector3f

class PlayerProjectile(val creationTime : Float, renderList : MutableList<Mesh>, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null) : Renderable (renderList, modelMatrix, parent){

    val lifeTime = 2.5f
    val deathTime : Float
    val light : PointLight
    private val speed = 4f

    val colliderFront : Collider
    val colliderMid : Collider
    val colliderBack : Collider

    val colliderList : MutableList<Collider>

    init {
        deathTime = creationTime + lifeTime
        light = PointLight(AttenuationType.LINEAR,Vector3f(1f,0f,0f), 10f, this.getWorldModelMatrix())
        globalLightHandler.addPointLight(light)

        colliderFront = Collider(ColliderType.PLAYERPROJICTILECOLLIDER, 0.15f)
        colliderFront.parent = this
        colliderFront.setPosition(Vector3f(0f,0f,-1.8f))

        colliderMid = Collider(ColliderType.PLAYERPROJICTILECOLLIDER, 0.15f)
        colliderMid.parent = this
        colliderMid.setPosition(Vector3f(0f,0f,-1.8f-2))

        colliderBack = Collider(ColliderType.PLAYERPROJICTILECOLLIDER, 0.15f)
        colliderBack.parent = this
        colliderBack.setPosition(Vector3f(0f,0f,-1.8f+2))

        colliderList = mutableListOf(colliderFront)//,colliderMid,colliderBack)
    }

    var shouldIdie = false


    fun update(deltaTime: Float, time: Float){
        translate(Vector3f(0f,0f, -speed))
        light.setPosition(this.getPosition())
        if (deathTime-time < 2f) light.intensity -= deltaTime * 3
        if (deathTime <= time) shouldIdie = true
        for (each in colliderList) if (each.collided) shouldIdie = true
//        if (getPosition()[1] < -7f) shouldIdie = true
        if (shouldIdie) for (each in colliderList) globalCollisionHandler.removeAllyProjectile(each)
    }

    override fun render(shaderProgram : ShaderProgram) {
        for(each in colliderList) each.render(shaderProgram)
        super.render(shaderProgram)
    }

}