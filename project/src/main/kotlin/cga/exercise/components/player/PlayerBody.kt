package cga.exercise.components.player

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.CollisionHandler
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.collisionHandler
import org.joml.Matrix4f
import org.joml.Vector3f

class PlayerBody (playerGeo : PlayerGeo, modelMatrix : Matrix4f, parent: Transformable? = null) : PlayerPart(modelMatrix, parent) {

    var bodyGeo = Renderable(playerGeo.body.renderList, Matrix4f() )

    var collider1 = Collider(radius = 0.6f)
    var collider2 = Collider(radius = 0.6f)
    var collider3 = Collider(radius = 0.55f)
    var collider4 = Collider(radius = 0.5f)
    var collider5 = Collider(radius = 0.5f)
    var collider6 = Collider(radius = 0.45f)
    var collider7 = Collider(radius = 0.4f)
    var collider8 = Collider(radius = 0.35f)
    var collider9 = Collider(radius = 0.3f)
    var collider10 = Collider(radius = 0.28f)
    var collider11 = Collider(radius = 0.25f)
    var collider12 = Collider(radius = 0.25f)
    var collider13 = Collider(radius = 0.25f)
    var collider14 = Collider(radius = 0.23f)
    var collider15 = Collider(radius = 0.25f)
    var collider16 = Collider(radius = 0.23f)
    var collisionList = mutableListOf<Collider>()

    init {
        bodyGeo.parent = this

        collider1.setPosition(Vector3f(0f,0f,0.5f))
        collisionList.add(collider1)
        collider1.parent = this

        collider2.setPosition(Vector3f(0f,0f,-0.1f))
        collisionList.add(collider2)
        collider2.parent = this

        collider3.setPosition(Vector3f(0f,0.1f,-0.7f))
        collisionList.add(collider3)
        collider3.parent = this

        collider4.setPosition(Vector3f(0f,0.1f,-1.2f))
        collisionList.add(collider4)
        collider4.parent = this

        collider5.setPosition(Vector3f(0f,0.1f,-1.6f))
        collisionList.add(collider5)
        collider5.parent = this

        collider6.setPosition(Vector3f(0f,0.1f,-2.1f))
        collisionList.add(collider6)
        collider6.parent = this

        collider7.setPosition(Vector3f(0f,0.1f,-2.6f))
        collisionList.add(collider7)
        collider7.parent = this

        collider8.setPosition(Vector3f(0f,0.1f,-3.0f))
        collisionList.add(collider8)
        collider8.parent = this

        collider9.setPosition(Vector3f(0f,0.1f,-3.4f))
        collisionList.add(collider9)
        collider9.parent = this

        collider10.setPosition(Vector3f(0f,0.1f,-3.7f))
        collisionList.add(collider10)
        collider10.parent = this

        collider11.setPosition(Vector3f(0f,0.1f,-4.0f))
        collisionList.add(collider11)
        collider11.parent = this

        collider12.setPosition(Vector3f(0f,0.1f,-4.3f))
        collisionList.add(collider12)
        collider12.parent = this

        collider13.setPosition(Vector3f(0f,0.1f,-4.0f))
        collisionList.add(collider13)
        collider13.parent = this

        collider14.setPosition(Vector3f(0f,0.1f,-4.3f))
        collisionList.add(collider14)
        collider14.parent = this

        collider15.setPosition(Vector3f(0f,0.1f,-4.6f))
        collisionList.add(collider15)
        collider15.parent = this

        collider16.setPosition(Vector3f(0f,0.1f,-4.9f))
        collisionList.add(collider16)
        collider16.parent = this

        for (hitbox in collisionList){
            collisionHandler.addShipPart(hitbox)
        }

    }

    override fun render(shaderProgram : ShaderProgram){
        bodyGeo.render(shaderProgram)
    }
}