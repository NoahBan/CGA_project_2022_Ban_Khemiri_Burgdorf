package cga.exercise.components.effects

import cga.exercise.components.camera.Camera
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Material
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.exercise.game.globalCollisionHandler
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

enum class EmiterType (val emiterType: Int){
    Explosion(0),
    Extra(1),
    Supernova(2),
}

class EmitterHandler {
    //List of all Emitters
    private var emitterList = mutableListOf<Emitter>()

    fun addEmitter(emitter : Emitter){
        emitterList.add(emitter)
    }

    fun addEmitterType(emiter: EmiterType, x: Float, y: Float, z: Float){
        when(emiter){
            EmiterType.Explosion -> {

                val pureOrangeTex = Texture2D("assets/textures/pureColor/pureOrange.png", true)
                pureOrangeTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)
                val pureWhiteTex = Texture2D("assets/textures/pureColor/pureWhite.png", true)
                pureWhiteTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

                val explosiveMaterial = Material(
                    pureOrangeTex,
                    pureOrangeTex,
                    pureWhiteTex,
                    60.0f,
                    Vector2f(1f,1f),
                    Vector3f(1f),
                    1f)

                val emitter1 = Emitter(
                    x,y,z,
                    explosiveMaterial,
                    100,
                    1f,
                    0.3f,
                    Vector3f(0f,0.1f,0f),
                    Vector3f(0f,0.3f,0f),
                    180f,
                    0.96f,
                    Vector3f(0f,0f,0f),
                    1.0f,
                    1.2f,
                    0.993f,
                    0.96f,
                    Vector3f(0.2f,0.2f,0.2f),
                    0.97f,
                    0f,
                    1)

                emitterList.add(emitter1)

                val pureRedTex = Texture2D("assets/textures/pureColor/pureRed.png", true)
                pureRedTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

                val explosiveMaterial2 = Material(
                    pureRedTex,
                    pureRedTex,
                    pureWhiteTex,
                    60.0f,
                    Vector2f(1f,1f),
                    Vector3f(1f),
                    1f)

                val emitter2 = Emitter(
                    x,y,z,
                    explosiveMaterial2,
                    100,
                    1f,
                    0.3f,
                    Vector3f(0f,0.1f,0f),
                    Vector3f(0f,0.3f,0f),
                    180f,
                    0.98f,
                    Vector3f(0f,0f,0f),
                    1.5f,
                    1.7f,
                    0.993f,
                    0.99f,
                    Vector3f(0.2f,0.2f,0.2f),
                    0.92f,
                    0f,
                    1)

                emitterList.add(emitter2)

            }
            EmiterType.Supernova -> {

                val pureWhiteTex = Texture2D("assets/textures/pureColor/pureWhite.png", true)
                pureWhiteTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

                val pureOrangeTex = Texture2D("assets/textures/pureColor/pureOrange.png", true)
                pureOrangeTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

                val explosiveMaterial = Material(
                    pureOrangeTex,
                    pureOrangeTex,
                    pureWhiteTex,
                    60.0f,
                    Vector2f(1f,1f),
                    Vector3f(1f),
                    0.7f)

                val emitter1 = Emitter(
                    x,y,z,
                    explosiveMaterial,
                    300,
                    0.5f,
                    1.5f,
                    Vector3f(1f,0f,0f),
                    Vector3f(3f,0f,0f),
                    180f,
                    0.96f,
                    Vector3f(0f,0f,0f),
                    0.3f,
                    0.5f,
                    0.993f,
                    0.98f,
                    Vector3f(1f,1f,1f),
                    0.92f,
                    0.0f,
                    1,
                    1f,1f,0.0f)

                emitterList.add(emitter1)

                val pureRedTex = Texture2D("assets/textures/pureColor/pureRed.png", true)
                pureRedTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

                val explosiveMaterial2 = Material(
                    pureRedTex,
                    pureRedTex,
                    pureWhiteTex,
                    60.0f,
                    Vector2f(1f,1f),
                    Vector3f(1f),
                    1f)

                val emitter2 = Emitter(
                    x,y,z,
                    explosiveMaterial2,
                    50,
                    3f,
                    7f,
                    Vector3f(0f,0.8f,0f),
                    Vector3f(0f,1f,0f),
                    180f,
                    0.98f,
                    Vector3f(0f,0f,0f),
                    1.5f,
                    1.7f,
                    0.98f,
                    0.99f,
                    Vector3f(0.2f,0.2f,0.2f),
                    0.9f,
                    0f,
                    1)

                emitterList.add(emitter2)
            }

            EmiterType.Extra -> {

                val pureWhiteTex = Texture2D("assets/textures/pureColor/pureWhite.png", true)
                pureWhiteTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

                val pureGreyTex = Texture2D("assets/textures/pureColor/pureGrey.png", true)
                pureGreyTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

                val explosiveMaterial = Material(
                    pureGreyTex,
                    pureGreyTex,
                    pureWhiteTex,
                    60.0f,
                    Vector2f(1f,1f),
                    Vector3f(1f),
                    0.7f)

                val emitter1 = Emitter(
                    x,y,z,
                    explosiveMaterial,
                    1,
                    0.03f,
                    0.07f,
                    Vector3f(0.1f,0.1f,0.3f),
                    Vector3f(-0.1f,-0.1f,0.3f),
                    10f,
                    0.9f,
                    Vector3f(0f,0f,-1f),
                    0.3f,
                    0.5f,
                    1f,
                    0.98f,
                    Vector3f(1f,1f,1f),
                    1f,
                    0.1f,
                    1,
                    1f,1f,1f)

                emitterList.add(emitter1)
            }
        }
    }

    fun removeEmitterAt(int : Int){
        emitterList.removeAt(int)
    }

    fun removeEmitter(emitter : Emitter){
        emitterList.remove(emitter)
    }

    fun renderAllEmitter(shader : ShaderProgram){
        for (emitter in emitterList){
            emitter.render(shader)
        }
    }

    fun updateAllEmitter(t: Float, dt: Float, camera : Camera){
        var temp = mutableListOf<Int>()

        for (emitter in emitterList){
            emitter.update(t,dt, camera)
            if (emitter.isDead){
                temp.add(emitterList.indexOf(emitter))
            }
        }
        for (each in temp.reversed()){ emitterList.removeAt(each) }
    }
}