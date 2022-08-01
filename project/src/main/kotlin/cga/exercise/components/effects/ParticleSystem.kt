package cga.exercise.components.effects

import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.cameraHandler
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

class ParticleSystem (
    x : Float,
    y : Float,
    z : Float,
    material: Material,
    count : Int,
    minScale : Float,
    maxScale : Float,
    minSpreadVec : Vector3f = Vector3f(0f,0.01f,0f),
    maxSpreadVec : Vector3f = Vector3f(0f,0.01f,0f),
    spreadRadius : Float = 45f,
    acceleration : Float = 1.0f,
    accelerationVector: Vector3f = Vector3f(0f,0f,0f),
    minDeathTime : Float = 1.0f,
    maxDeathTime : Float = 1.0f,
    alphaOverLife : Float = 1.0f,
    colorOverLife : Float = 1.0f,
    sizeOverLife : Float = 1.0f)
    //val colorOverLife : Vector3f
{
    //Import Plane
    val posAndTexcAndNormPos = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 0)
    val posAndTexcAndNormTexc = VertexAttribute (2, GL30.GL_FLOAT,8 * 4, 3 * 4)
    val posAndTexcAndNormNorm = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 5 * 4)
    val posAndTexcAndNormAttrArray = arrayOf(posAndTexcAndNormPos, posAndTexcAndNormTexc, posAndTexcAndNormNorm)
    var allParticles = mutableListOf<Particle>()

    init {
        //Erstellen und importieren der Planes zu Particles
        for (i in count downTo 1){
            val importObj = OBJLoader.loadOBJ("assets/models/ParticlePlane.obj", true)
            val importedData  = importObj.objects[0].meshes[0]
            val importedMesh = Mesh (importedData.vertexData, importedData.indexData, posAndTexcAndNormAttrArray,false, material)

            val importedPlane = Particle(mutableListOf(importedMesh), Matrix4f(), x, y, z, minScale, maxScale, minSpreadVec,maxSpreadVec,spreadRadius,acceleration,accelerationVector,minDeathTime,maxDeathTime)
            allParticles.add(importedPlane)
        }
    }

    fun update(t : Float){
        var temp = mutableListOf<Int>()

        allParticles.forEachIndexed{index,particle->
            if (t >= particle.death) temp.add(index)
        }

        for (each in temp.reversed()){
            allParticles.removeAt(each)
        }

        for (particle in allParticles){
            particle.update(t)
        }
    }

    fun render(shaderProgram : ShaderProgram){
        for (particle in allParticles){
            particle.render(shaderProgram)
        }
    }

}