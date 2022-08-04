package cga.exercise.components.enemy

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Math.*


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

        asteroid1 = ModelLoader.loadModel("assets/models/Enemies/asteroids/Asteroid_1_LOW_MODEL_.obj",
            toRadians(0f),
            toRadians(0f),
            toRadians(0f)
        )!!
        asteroid2 = ModelLoader.loadModel("assets/models/Enemies/asteroids/Asteroid_2_LOW_MODEL_.obj",
            toRadians(0f),
            toRadians(0f),
            toRadians(0f)
        )!!
        asteroid3 = ModelLoader.loadModel("assets/models/Enemies/asteroids/Asteroid_3_LOW_MODEL_.obj",
            toRadians(0f),
            toRadians(0f),
            toRadians(0f)
        )!!

    }

}