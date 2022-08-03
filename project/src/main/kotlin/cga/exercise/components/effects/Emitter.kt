package cga.exercise.components.effects

import cga.exercise.components.geometry.*
import cga.exercise.components.shader.ShaderProgram
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30

class Emitter (
    var x : Float,
    var y : Float,
    var z : Float,
    material: Material,
    var count : Int,
    var minScale : Float,
    var maxScale : Float,
    var minSpreadVec : Vector3f = Vector3f(0f,0.01f,0f),
    var maxSpreadVec : Vector3f = Vector3f(0f,0.01f,0f),
    var spreadRadius : Float = 45f,
    var acceleration : Float = 1.0f,
    var accelerationVector: Vector3f = Vector3f(0f,0f,0f),
    var minDeathTime : Float = 1.0f,
    var maxDeathTime : Float = 1.0f,
    var sizeOverLife : Float = 1.0f,
    var alphaOverLife : Float = 1.0f,
    var colorOverLife : Vector3f = Vector3f(1f),
    var colorLife : Float = 1f,
    var delay : Float = 2f,
    var maxCycles : Int = -1
    )
{
    //Set variables
    val posAndTexcAndNormPos = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 0)
    val posAndTexcAndNormTexc = VertexAttribute (2, GL30.GL_FLOAT,8 * 4, 3 * 4)
    val posAndTexcAndNormNorm = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 5 * 4)
    val posAndTexcAndNormAttrArray = arrayOf(posAndTexcAndNormPos, posAndTexcAndNormTexc, posAndTexcAndNormNorm)
    var allParticles = mutableListOf<Particle>()
    var startTime = 0f
    var delayTime = delay+startTime
    var updateAllowed = false
    var cycleCount = 0
    var isDead = false

    //Import Mesh
    val importObj = OBJLoader.loadOBJ("assets/models/ParticlePlate.obj", true)
    val importedData  = importObj.objects[0].meshes[0]
    val importedMesh = Mesh (importedData.vertexData, importedData.indexData, posAndTexcAndNormAttrArray,false, material)
    var lonelyParticle = Renderable(mutableListOf(importedMesh), Matrix4f())

    init {
        //Set color for color Over life
        for (i in count downTo 1){
            if (colorOverLife != Vector3f(1f,1f,1f)){
                importedMesh.material?.scalingColor = colorOverLife
            }
        }
    }

    fun update(t : Float, dt : Float){
        //Delete all dead particles
        var temp = mutableListOf<Int>()
        allParticles.forEachIndexed{index,particle-> if (t >= particle.death) temp.add(index)}
        for (each in temp.reversed()){ allParticles.removeAt(each) }

        //Set Update Allowed
        if (t >= delayTime && maxCycles == -1){
            updateAllowed = true
            delayTime = t+delay
        }else if (t >= delayTime && cycleCount < maxCycles){
            updateAllowed = true
            delayTime = t+delay
        }else if (t >= delayTime){
            isDead = true
        }



        //Add new particles in delay interval
        if (updateAllowed == true){
            updateAllowed = false

            //Add cycle count if needed
            cycleCount++

            //Add new particles
            for (i in count downTo 1){
                var newParticle = Particle(
                    Matrix4f(),
                    x, y, z,
                    minScale,
                    maxScale,
                    minSpreadVec,
                    maxSpreadVec,
                    spreadRadius,
                    acceleration,
                    accelerationVector,
                    minDeathTime,
                    maxDeathTime,
                    sizeOverLife,
                    alphaOverLife,
                    colorLife)
                allParticles.add(newParticle)
            }
        }


        //Update Particles
        for (particle in allParticles){
            particle.update(t, dt)
        }
    }

    fun render(shaderProgram : ShaderProgram){
        for (particle in allParticles){
            lonelyParticle.setPosition(particle.getWorldPosition())
            lonelyParticle.setXAxis(particle.getWorldXAxis())
            lonelyParticle.setYAxis(particle.getWorldYAxis())
            lonelyParticle.setZAxis(particle.getWorldZAxis())
            lonelyParticle.scale(particle.getScale())
            //Apply Alpha Change
            if (alphaOverLife != 1f){
                importedMesh.material?.opacityMultiplier = particle.newAlpha
            }
            //Apply Color Change
            if (colorLife != 1f){
                importedMesh.material?.colorScaling = particle.newColorScaling
            }
            if (colorOverLife != Vector3f(1f,1f,1f)){
                importedMesh.material?.scalingColor = colorOverLife
            }

            lonelyParticle.render(shaderProgram)
        }
    }

}