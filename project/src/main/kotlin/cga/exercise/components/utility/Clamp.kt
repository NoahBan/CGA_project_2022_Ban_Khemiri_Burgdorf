package cga.exercise.components.utility

fun clampf (value : Float, min : Float, max : Float):Float{
    if (value < min) return min
    if (value > max) return max
    return value
}