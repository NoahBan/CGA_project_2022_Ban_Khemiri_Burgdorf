package cga.exercise.components.player

import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Math
import  org.joml.Math.*
import org.lwjgl.opengl.GL30

class PlayerGeo {

    val body : Renderable
    val wingL : Renderable
    val wingR : Renderable
    val weaponRoot : Renderable
    val weaponMid : Renderable
    val weaponEnd : Renderable
    val schuss : Renderable
    val fire : Renderable

    init {

        body = ModelLoader.loadModel("assets/models/X_Wing/X_Body.obj",
            toRadians(0f),
            toRadians(0f),
            toRadians(0f)
        )!!

        wingL = ModelLoader.loadModel(
            "assets/models/X_Wing/WingOL.obj",
            toRadians(0f),
            toRadians(0f),
            toRadians(0f)
        )!!

        wingR = ModelLoader.loadModel("assets/models/X_Wing/WingOR.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f)
        )!!

        weaponRoot = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Root.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!

        weaponMid = ModelLoader.loadModel("assets/models/X_Wing/Waffe_Mid.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!

        weaponEnd = ModelLoader.loadModel("assets/models/X_Wing/Waffe_End.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!

        schuss = ModelLoader.loadModel("assets/models/X_Wing/schuss.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!

        fire = ModelLoader.loadModel("assets/models/X_Wing/fire.obj",
            Math.toRadians(0f),
            Math.toRadians(0f),
            Math.toRadians(0f))!!

        for (each in fire.renderList) {
            each.material?.diff?.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)
            each.material?.emit?.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)
            each.material?.opacity = 1f
        }
    }
}