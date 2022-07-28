package cga.exercise.components.projectile

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.AttenuationType
import cga.exercise.components.light.PointLight
import cga.exercise.components.player.PlayerGeo
import cga.exercise.components.player.WingType
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.utility.clampf
import cga.exercise.game.globalCollisionHandler
import cga.exercise.game.globalLightHandler
import org.joml.Matrix4f
import org.joml.Vector3f

class PlayerProjectile(val creationTime : Float, renderList : MutableList<Mesh>, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null) : Renderable (renderList, modelMatrix, parent){

    val lifeTime = 2.5f
    val deathTime : Float
    val light : PointLight
    private val speed = 4f

    val collider : Collider

    init {
        deathTime = creationTime + lifeTime
        light = PointLight(AttenuationType.LINEAR,Vector3f(1f,0f,0f), 10f, this.getWorldModelMatrix())
        globalLightHandler.addPointLight(light)

        collider = Collider(ColliderType.PLAYERPROJICTILECOLLIDER, 0.15f)
        collider.parent = this
        collider.setPosition(Vector3f(0f,0f,-1.8f))
    }

    var shouldIdie = false


    fun update(deltaTime: Float, time: Float){
        translate(Vector3f(0f,0f, -speed))
        light.setPosition(this.getPosition())
        if (deathTime-time < 2f) light.intensity -= deltaTime * 3
        if (deathTime <= time) shouldIdie = true
        if (collider.collided) shouldIdie = true
//        if (getPosition()[1] < -7f) shouldIdie = true
        if (shouldIdie) globalCollisionHandler.removeAllyProjectile(collider)
    }

    override fun render(shaderProgram : ShaderProgram) {
        collider.render(shaderProgram)
        super.render(shaderProgram)
    }

}