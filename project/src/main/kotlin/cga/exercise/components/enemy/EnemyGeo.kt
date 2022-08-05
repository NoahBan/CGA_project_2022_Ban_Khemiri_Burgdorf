package cga.exercise.components.enemy

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Math.*
import org.lwjgl.opengl.GL30


class EnemyGeo {

    val shuttle : Renderable
    val asteroid1 : Renderable
    val asteroid2 : Renderable
    val asteroid3 : Renderable

    init {
        shuttle = ModelLoader.loadModel("assets/models/Enemies/EnemyShuttle.obj",
            toRadians(0f),
            toRadians(0f),
            toRadians(0f)
        )!!
        for (each in shuttle.renderList) {
            each.material?.diff?.setTexParams(
                GL30.GL_REPEAT,
                GL30.GL_REPEAT,
                GL30.GL_LINEAR_MIPMAP_LINEAR,
                GL30.GL_LINEAR_MIPMAP_LINEAR
            )
            each.material?.emit?.setTexParams(
                GL30.GL_REPEAT,
                GL30.GL_REPEAT,
                GL30.GL_LINEAR_MIPMAP_LINEAR,
                GL30.GL_LINEAR_MIPMAP_LINEAR
            )
        }

        asteroid1 = ModelLoader.loadModel("assets/models/Enemies/asteroids/Asteroid_1_LOW_MODEL_.obj",
            toRadians(0f),
            toRadians(0f),
            toRadians(0f)
        )!!
        for (each in asteroid1.renderList) {
            each.material?.diff?.setTexParams(
                GL30.GL_REPEAT,
                GL30.GL_REPEAT,
                GL30.GL_LINEAR_MIPMAP_LINEAR,
                GL30.GL_LINEAR_MIPMAP_LINEAR
            )
            each.material?.emit?.setTexParams(
                GL30.GL_REPEAT,
                GL30.GL_REPEAT,
                GL30.GL_LINEAR_MIPMAP_LINEAR,
                GL30.GL_LINEAR_MIPMAP_LINEAR
            )
        }

        asteroid2 = ModelLoader.loadModel("assets/models/Enemies/asteroids/Asteroid_2_LOW_MODEL_.obj",
            toRadians(0f),
            toRadians(0f),
            toRadians(0f)
        )!!
        for (each in asteroid2.renderList) {
            each.material?.diff?.setTexParams(
                GL30.GL_REPEAT,
                GL30.GL_REPEAT,
                GL30.GL_LINEAR_MIPMAP_LINEAR,
                GL30.GL_LINEAR_MIPMAP_LINEAR
            )
            each.material?.emit?.setTexParams(
                GL30.GL_REPEAT,
                GL30.GL_REPEAT,
                GL30.GL_LINEAR_MIPMAP_LINEAR,
                GL30.GL_LINEAR_MIPMAP_LINEAR
            )
        }

        asteroid3 = ModelLoader.loadModel("assets/models/Enemies/asteroids/Asteroid_3_LOW_MODEL_.obj",
            toRadians(0f),
            toRadians(0f),
            toRadians(0f)
        )!!
        for (each in asteroid3.renderList) {
            each.material?.diff?.setTexParams(
                GL30.GL_REPEAT,
                GL30.GL_REPEAT,
                GL30.GL_LINEAR_MIPMAP_LINEAR,
                GL30.GL_LINEAR_MIPMAP_LINEAR
            )
            each.material?.emit?.setTexParams(
                GL30.GL_REPEAT,
                GL30.GL_REPEAT,
                GL30.GL_LINEAR_MIPMAP_LINEAR,
                GL30.GL_LINEAR_MIPMAP_LINEAR
            )
        }

    }

}