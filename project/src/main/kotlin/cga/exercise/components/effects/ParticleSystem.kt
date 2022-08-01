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
    y : Float ,
    z : Float,
    material: Material,
    count : Int,
    minScale : Float,
    maxScale : Float,
    deathTime : Float = 1.0f,
    posXSpread : Float = 0.01f,
    negXSpread : Float = 0.01f,
    posYSpread : Float = 0.01f,
    negYSpread : Float = 0.01f,
    posZSpread : Float = 0.0f,
    negZSpread : Float = 0.0f,
    minRadius : Float = 1.0f,
    maxRadius : Float = 1.0f,
    alphaOverLife : Float = 1.0f,
    colorOverLife : Float = 1.0f,
    sizeOverLife : Float = 1.0f,
    acceleration : Float = 1.0f,
    accelerationVector: Float = 1.0f)
    //val colorOverLife : Vector3f
{
    //Import Plane
    val posAndTexcAndNormPos = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 0)
    val posAndTexcAndNormTexc = VertexAttribute (2, GL30.GL_FLOAT,8 * 4, 3 * 4)
    val posAndTexcAndNormNorm = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 5 * 4)
    val posAndTexcAndNormAttrArray = arrayOf(posAndTexcAndNormPos, posAndTexcAndNormTexc, posAndTexcAndNormNorm)
    var allParticles = mutableListOf<Renderable>()
    var allParticleVectors = mutableListOf<Vector3f>()

    init {
        //Erstellen und importieren der Planes
        for (i in count downTo 1){
            val importObj = OBJLoader.loadOBJ("assets/models/ParticlePlane.obj", true)
            val importedData  = importObj.objects[0].meshes[0]
            val importedMesh = Mesh (importedData.vertexData, importedData.indexData, posAndTexcAndNormAttrArray,false, material)

            val importedPlane = Renderable(mutableListOf(importedMesh), Matrix4f(), null)
            allParticles.add(importedPlane)
        }

        for (particle in allParticles){

            //Rotation
            setCorrectRotation(particle)
            var randomRoll = 0 + Math.random() * (0-360)

            var currentRota = particle.getRotation()
            particle.rotate(currentRota.x,currentRota.y,currentRota.z+randomRoll.toFloat())

            //Scaling
            var randomScale = minScale + Math.random() * (maxScale-minScale)
            var scalingVec = Vector3f(randomScale.toFloat(),randomScale.toFloat(),randomScale.toFloat())
            println(scalingVec)
            particle.scale(Vector3f(scalingVec))


            //Spread
            val randomX = -negXSpread + Math.random() * (posXSpread-(-negXSpread))
            val randomY = -negYSpread + Math.random() * (posYSpread-(-negYSpread))
            val randomZ = -negZSpread + Math.random() * (posZSpread-(-negZSpread))
            allParticleVectors.add(Vector3f(randomX.toFloat(),randomY.toFloat(),randomZ.toFloat()))
        }
    }

    fun scaleNow() {
        for (particle in allParticles){

        }
    }
    fun update(){
        for (particle in allParticles){
            //Rotate To Cam
            setCorrectRotation(particle)

            //Spread To
            var particleX = particle.getWorldPosition().x
            var particleY = particle.getWorldPosition().y
            var particleZ = particle.getWorldPosition().z

            var newX = allParticleVectors[allParticles.indexOf(particle)].x
            var newY = allParticleVectors[allParticles.indexOf(particle)].y
            var newZ = allParticleVectors[allParticles.indexOf(particle)].z

            particle.setPosition(Vector3f(particleX+newX,particleY+newY,particleZ+newZ))
        }
    }

    fun setCorrectRotation(plane: Renderable){
        //var rotation = plane.getRotation()


        plane.rotate(0f,0f,0f)

        var eye = plane.getPosition()
        var center = cameraHandler.getActiveCamera().getWorldPosition()
        var up  = plane.getYAxis()

        var matrix = (Matrix4f().lookAt(eye,center,up)).invert()

        plane.setXAxis(Vector3f(matrix.m00(),matrix.m01(),matrix.m02()).normalize())
        plane.setYAxis(Vector3f(matrix.m10(),matrix.m11(),matrix.m12()).normalize())
        plane.setZAxis(Vector3f(matrix.m20(),matrix.m21(),matrix.m22()).normalize())

    }

    fun render(shaderProgram : ShaderProgram){
        for (particle in allParticles){
            particle.render(shaderProgram)
        }
    }

}