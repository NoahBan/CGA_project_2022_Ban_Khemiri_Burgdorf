package cga.exercise.components.enemy

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Math.*


class EnemyGeo {

    val sphere : Renderable

    init {
        sphere = ModelLoader.loadModel("assets/models/Enemies/EnemySphere.obj",
        toRadians(0f),
        toRadians(0f),
        toRadians(0f)
        )!!
    }

}