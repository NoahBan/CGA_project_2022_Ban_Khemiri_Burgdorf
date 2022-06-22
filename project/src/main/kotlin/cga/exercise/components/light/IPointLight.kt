package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram

// 4.1.1
// A - Emissive Material
// B - Ambient Light Color
// C - Diffuse Reflection
// D - Specular Reflection

//4.1.2
// Betrachtet man die Funktion cos(x), erkennt man, dass zwischen pi/2 rad bzw 90° deg cos(x) und 3/2 pi bzw 270°
//alle Werte für x negativ sind.  keine Erklärung tbd
interface IPointLight {
    fun bind(shaderProgram: ShaderProgram)
}