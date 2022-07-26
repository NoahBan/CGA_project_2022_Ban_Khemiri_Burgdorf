package cga.exercise.components.light

import cga.exercise.components.camera.Camera
import cga.exercise.components.geometry.Transformable
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

open class DirectionalLight (
     var lightColor : Vector3f = Vector3f(1F,1F,1F),
     var intensity : Float,
    val direction : Vector3f
) {
    fun get() : Vector3f{
        return direction
    }
}
