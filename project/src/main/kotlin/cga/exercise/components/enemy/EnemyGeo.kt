package cga.exercise.components.enemy

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Math.*


class EnemyGeo {

    val sphere : Renderable
    val shuttle : Renderable

    init {
        sphere = ModelLoader.loadModel("assets/models/Enemies/EnemySphere.obj",
        toRadians(0f),
        toRadians(0f),
        toRadians(0f)
        )!!

        shuttle = ModelLoader.loadModel("assets/models/Enemies/EnemyShuttle.obj",
            toRadians(0f),
            toRadians(0f),
            toRadians(0f)
        )!!
    }

}