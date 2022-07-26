package cga.exercise.components.light

import cga.exercise.components.geometry.*
import cga.exercise.components.texture.Texture2D
import cga.framework.OBJLoader
import org.joml.*
import org.lwjgl.opengl.GL30

// 4.1.1
// A - Emissive Material
// B - Ambient Light Color
// C - Diffuse Reflection
// D - Specular Reflection //Sharpens highlight, makes it more intense

//4.1.2
// Skalarprodukt könnte negativ sein, wenn wir das Objekt von hinten betrachten.
open class PointLight (
         val attenuationType : AttenuationType,
         var lightColor : Vector3f = Vector3f(1F,1F,1F),
         var intensity : Float,
        modelMatrix : Matrix4f = Matrix4f(),
        parent: Transformable? = null,
        var lightVisible : Boolean = false)
        : Transformable(modelMatrix, parent) {

        val lightSphere : Renderable

        init {
                val posAndTexcAndNormPos = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 0)
                val posAndTexcAndNormTexc = VertexAttribute (2, GL30.GL_FLOAT,8 * 4, 3 * 4)
                val posAndTexcAndNormNorm = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 5 * 4)

                val posAndTexcAndNormAttrArray = arrayOf(posAndTexcAndNormPos, posAndTexcAndNormTexc, posAndTexcAndNormNorm)
                val pureBlackTex = Texture2D("assets/textures/pureColor/pureBlack.png", true)
                pureBlackTex.setTexParams(GL30.GL_REPEAT, GL30.GL_REPEAT, GL30.GL_LINEAR_MIPMAP_LINEAR, GL30.GL_LINEAR_MIPMAP_LINEAR)
                val pureWhiteTex = Texture2D("assets/textures/pureColor/pureWhite.png", true)
                pureWhiteTex.setTexParams(GL30.GL_REPEAT,GL30.GL_REPEAT,GL30.GL_LINEAR_MIPMAP_LINEAR,GL30.GL_LINEAR_MIPMAP_LINEAR)

                val matLightSphere = Material(
                        pureBlackTex,
                        pureWhiteTex,
                        pureBlackTex
                )
                val importObjSphere = OBJLoader.loadOBJ("assets/models/sphere.obj", true)
                val importedSphereData  = importObjSphere.objects[0].meshes[0]
                val importedLightSphereMesh = Mesh (importedSphereData.vertexData, importedSphereData.indexData, posAndTexcAndNormAttrArray,false, matLightSphere)
                lightSphere = Renderable(mutableListOf(importedLightSphereMesh), Matrix4f(), null)
                lightSphere.scale(Vector3f(0.05F))
        }

fun getPremultLightPos(viewMatrix : Matrix4f) : Vector3f {
        val worldModelMatrix = Matrix4f(getWorldModelMatrix())

        val calcLightPos = Vector4f(
                viewMatrix.m00() * worldModelMatrix.m30() + viewMatrix.m10() * worldModelMatrix.m31() + viewMatrix.m20() * worldModelMatrix.m32() + viewMatrix.m30() * worldModelMatrix.m33(),
                viewMatrix.m01() * worldModelMatrix.m30() + viewMatrix.m11() * worldModelMatrix.m31() + viewMatrix.m21() * worldModelMatrix.m32() + viewMatrix.m31() * worldModelMatrix.m33(),
                viewMatrix.m02() * worldModelMatrix.m30() + viewMatrix.m12() * worldModelMatrix.m31() + viewMatrix.m22() * worldModelMatrix.m32() + viewMatrix.m32() * worldModelMatrix.m33(),
                viewMatrix.m03() * worldModelMatrix.m30() + viewMatrix.m13() * worldModelMatrix.m31() + viewMatrix.m23() * worldModelMatrix.m32() + viewMatrix.m33() * worldModelMatrix.m33()
        )

        return Vector3f(calcLightPos[0], calcLightPos[1], calcLightPos[2])
    }

}