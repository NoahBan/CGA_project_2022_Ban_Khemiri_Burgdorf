package cga.exercise.components.utility

import org.joml.Vector3f

fun Vector3f.lerp(to: Vector3f, t: Float) = lerpV3f(this, to, t)

fun lerpV3f(from: Vector3f, to: Vector3f, t: Float) = Vector3f(
        lerpf(from[0], to[0], t),
        lerpf(from[1], to[1], t),
        lerpf(from[2], to[2], t)
)

fun lerpf(from: Float, to: Float, t: Float) = from + (to - from) * t