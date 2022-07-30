package cga.exercise.components.effects

import cga.exercise.components.camera.Camera
import cga.exercise.components.camera.CameraHandler
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.player.PlayerObject
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.game.cameraHandler
import cga.framework.OBJLoader
import org.joml.Matrix4f
import org.joml.Vector3d
import org.joml.Vector3f
import org.lwjgl.opengl.GL30
import javax.swing.Renderer

class ParticleSystem (
    x : Float,
    var y:Float ,
    var z: Float,
    material: Material,
    //EmitterVars
    val count : Int,
    val scale : Float,
    val rotation : Float)
    /**val rotationPerSecond : Float,
    val deathTime : Float,
    val minRadius : Float,
    val maxRadius : Float,
    val posXSpread : Float,
    val negXSpread : Float,
    val posYSpread : Float,
    val negYSpread : Float,
    val posZSpread : Float,
    val negZSpread : Float,
    val alphaOverLife : Float,
    val sizeOverLife : Float,
    val acceleration : Float,
    val accelerationVector:  **/
    //val colorOverLife : Vector3f
{
    //Import Plane
    val posAndTexcAndNormPos = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 0)
    val posAndTexcAndNormTexc = VertexAttribute (2, GL30.GL_FLOAT,8 * 4, 3 * 4)
    val posAndTexcAndNormNorm = VertexAttribute (3, GL30.GL_FLOAT,8 * 4, 5 * 4)
    val posAndTexcAndNormAttrArray = arrayOf(posAndTexcAndNormPos, posAndTexcAndNormTexc, posAndTexcAndNormNorm)
    //Make Renderable
    val importObj = OBJLoader.loadOBJ("assets/models/ParticlePlane.obj", true)
    val importedData  = importObj.objects[0].meshes[0]
    val importedMesh = Mesh (importedData.vertexData, importedData.indexData, posAndTexcAndNormAttrArray,false, material)
    val plane = Renderable(mutableListOf(importedMesh), Matrix4f(), null)

    var allParticles = mutableList<Renderable>() = MutableList<Renderable>

    init {

        allParticles.add(plane)


        plane.translate(Vector3f(x,y,z))
    }

    fun setCorrectRotation(){
        plane.setRotation(0f,0f,0f)

        var eye = plane.getPosition()
        var center = cameraHandler.getActiveCamera().getWorldPosition()
        var up  = plane.getYAxis()

        var matrix = (Matrix4f().lookAt(eye,center,up)).invert()

        plane.setXAxis(Vector3f(matrix.m00(),matrix.m01(),matrix.m02()).normalize())
        plane.setYAxis(Vector3f(matrix.m10(),matrix.m11(),matrix.m12()).normalize())
        plane.setZAxis(Vector3f(matrix.m20(),matrix.m21(),matrix.m22()).normalize())
    }

    fun render(shaderProgram : ShaderProgram){
        plane.render(shaderProgram)
    }

    fun update(){
        setCorrectRotation()
    }

}