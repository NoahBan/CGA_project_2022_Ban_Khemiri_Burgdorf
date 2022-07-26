package cga.exercise.components.player

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

abstract class PlayerPart (modelMatrix : Matrix4f, parent: Transformable? = null) : Transformable(modelMatrix, parent){
    open fun render(shader : ShaderProgram){}
    open fun update(deltaTime: Float, time: Float){}
}