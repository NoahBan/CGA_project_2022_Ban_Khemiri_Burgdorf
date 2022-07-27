package cga.exercise.components.projectile

import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.AttenuationType
import cga.exercise.components.light.PointLight
import cga.exercise.components.player.PlayerGeo
import cga.exercise.components.player.WingType
import cga.exercise.components.utility.clampf
import cga.exercise.game.globalLightHandler
import org.joml.Matrix4f
import org.joml.Vector3f

class PlayerProjectile(creationTime : Float, renderList : MutableList<Mesh>, modelMatrix : Matrix4f = Matrix4f(), parent: Transformable? = null) : Renderable (renderList, modelMatrix, parent){

    val lifeTime = 2.5f
    val deathTime : Float
    val light : PointLight
    private val speed = 4f

    init {
        deathTime = creationTime + lifeTime
        light = PointLight(AttenuationType.LINEAR,Vector3f(1f,0f,0f), 10f, this.getWorldModelMatrix())
        globalLightHandler.addPointLight(light)
    }

    var shouldIdie = false




    fun update(deltaTime: Float, time: Float){
        translate(Vector3f(0f,0f, -speed))
        light.setPosition(this.getPosition())
        if(deathTime-time < 2f) light.intensity -= deltaTime * 3
        if (deathTime <= time) shouldIdie = true
//        if (getPosition()[1] < -7f) shouldIdie = true
    }
}