package cga.exercise.components.effects

import cga.exercise.components.shader.ShaderProgram

class EmitterHandler {

    //List of all Emitters
    private var emitterList = mutableListOf<Emitter>()

    fun addEmitter(emitter : Emitter){
        emitterList.add(emitter)
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

    fun updateAllEmitter(t : Float, dt : Float){
        var temp = mutableListOf<Int>()

        for (emitter in emitterList){
            emitter.update(t,dt)
            if (emitter.isDead){
                temp.add(emitterList.indexOf(emitter))
            }
        }
        for (each in temp.reversed()){ emitterList.removeAt(each) }
    }
}