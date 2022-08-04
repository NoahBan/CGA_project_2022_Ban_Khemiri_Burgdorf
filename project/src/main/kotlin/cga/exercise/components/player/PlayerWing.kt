package cga.exercise.components.player

import cga.exercise.components.collision.Collider
import cga.exercise.components.collision.ColliderType
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.light.AttenuationType
import cga.exercise.components.light.PointLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.utility.clampf
import cga.exercise.components.utility.lerpf
import cga.exercise.components.utility.setEuler
import cga.exercise.game.globalLightHandler
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.random.Random

enum class WingType (val wingType: Int){
    OL(0),
    OR(1),
    UL(2),
    UR(3)
}

class PlayerWing(playerGeo : PlayerGeo, wingType : WingType, modelMatrix : Matrix4f, parent: Transformable? = null) : PlayerPart(modelMatrix, parent) {

    var wingGeo = Renderable(playerGeo.wingL.renderList, Matrix4f())
    var wingType = wingType

    private var deltaTime = 0f
    private var time = 0f

    private var wingIn = true
    var wingOut = false

    private var moveWingOut = false

    var turbineFireTransform : Transformable
    var turbineFireMesh : Renderable

    private var wingRotationT = 0F
    private val wingRotationSpeed = 1f
    private val maxRotation = Math.toRadians(0f)
    private var minRotation = Math.toRadians(18f)

    private var wingDestroyed = false
    private var rotationDir = 1f

    private var weapon : PlayerWeapon

    val turbineLight : PointLight
    val turbineLightColor = Vector3f(76/255f,213/255f,253/255f)

    val collider1 : Collider
    val colliderWeapon : Collider
    val colliderList : List<Collider>

    init {
        when(wingType){
            WingType.OL -> {
                rotationDir = -1f
                weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
                setPosition(Vector3f(-0.45f, 0.09f, 0.735f))
                wingGeo.parent = this
                weapon.mirror(1f, 0f, 0f, 0f, 0f, 0f)
                weapon.mirror(0f, 1f, 0f, 0f, 0f, 0f)
                weapon.rotate(0f, 0f, 180f)
                weapon.setPosition(Vector3f(-2.22f, 0.9f, -0.69f))
                weapon.rotate(0f, 0f, 180f)
                turbineFireTransform = Transformable(Matrix4f(), this)
                turbineFireTransform.setPosition(Vector3f(-0.32f, 0.54f, 0.648f))
                turbineFireMesh = Renderable(playerGeo.fire.renderList, Matrix4f(), turbineFireTransform)
                turbineLight = PointLight(AttenuationType.LINEAR,turbineLightColor, 0.2f, Matrix4f(), turbineFireTransform)

            }
            WingType.OR -> {
                weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
                wingGeo.renderList = playerGeo.wingR.renderList
                setPosition(Vector3f(0.453f,0.09f,0.735f))
                wingGeo.parent = this
                minRotation *= -1f
                weapon.setPosition(Vector3f(2.22f,0.9f,-0.69f))
                turbineLight = PointLight(AttenuationType.LINEAR,turbineLightColor, 0.2f, Matrix4f(), this)
                turbineLight.setPosition(Vector3f(0.32f, 0.54f, 0.548f))
                turbineFireTransform = Transformable(Matrix4f(), this)
//                turbineFireTransform.setPosition(Vector3f(-0.311812f, 0.551516f, -0.606068f))
                turbineFireMesh = Renderable(playerGeo.fire.renderList, Matrix4f(), turbineFireTransform)
            }
            WingType.UL -> {
                weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
                wingGeo.renderList = playerGeo.wingR.renderList
                setPosition(Vector3f(-0.45f,-0.09f,0.735f))
                wingGeo.mirror(1f,0f,0f,0f,0f,0f)
                wingGeo.mirror(0f,1f,0f,0f,0f,0f)
                wingGeo.parent = this
                minRotation *= -1f
                weapon.rotate(0f,0f,180f)
                weapon.setPosition(Vector3f(-2.22f,-30f*0.03f,-0.69f))
                turbineLight = PointLight(AttenuationType.LINEAR,turbineLightColor, 0.2f, Matrix4f(), this)
                turbineLight.setPosition(Vector3f(-0.32f, -0.54f, 0.548f))
                turbineFireTransform = Transformable(Matrix4f(), this)
//                turbineFireTransform.setPosition(Vector3f(-0.311812f, 0.551516f, -0.606068f))
                turbineFireMesh = Renderable(playerGeo.fire.renderList, Matrix4f(), turbineFireTransform)
            }
            WingType.UR -> {
                rotationDir = -1f
                weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
                wingGeo.mirror(1f,0f,0f,0f,0f,0f)
                wingGeo.mirror(0f,1f,0f,0f,0f,0f)
                setPosition(Vector3f(0.45f,-0.09f,0.735f))
                wingGeo.parent = this
                weapon.mirror(1f,0f,0f,0f,0f,0f)
                weapon.mirror(0f,1f,0f,0f,0f,0f)
                weapon.rotate(0f,0f,180f)
                weapon.setPosition(Vector3f(2.22f,-0.9f,-0.69f))
                turbineLight = PointLight(AttenuationType.LINEAR,turbineLightColor, 0.2f, Matrix4f(), this)
                turbineLight.setPosition(Vector3f(0.32f, -0.54f, 0.548f))
                turbineFireTransform = Transformable(Matrix4f(), this)
//                turbineFireTransform.setPosition(Vector3f(-0.311812f, 0.551516f, -0.606068f))
                turbineFireMesh = Renderable(playerGeo.fire.renderList, Matrix4f(), turbineFireTransform)
            }
        }
        wingSetRotation(0f)
        collider1 = Collider(ColliderType.PLAYERCOLLIDER,0.3f)
        collider1.parent = wingGeo
        collider1.setPosition(Vector3f(0.4f*rotationDir,0.5F,0f))

        colliderWeapon = Collider(ColliderType.PLAYERCOLLIDER,0.1f)
        colliderWeapon.parent = weapon.weaponEndGeo

        colliderList = listOf(collider1,colliderWeapon)
        globalLightHandler.addPointLight(turbineLight)


    }

    fun getShotPos() : Vector3f = weapon.getShotPos()

    fun toggleWingMode(){
        moveWingOut = !moveWingOut
    }

    override fun render(shaderProgram : ShaderProgram){
        wingGeo.render(shaderProgram)
        weapon.render(shaderProgram)
        turbineFireMesh.render(shaderProgram)
    }

    fun wingSetRotation(t : Float){
        var matrix = getModelMatrix()
        matrix = setEuler(matrix, 0f, 0f, lerpf(minRotation, maxRotation,t))
        setModelMatrix(matrix)
        if (t == 0f) {
            wingIn = true
            wingOut = false
        }else
        if (t == 1f) {
            wingIn = false
            wingOut = true
        } else {
            wingIn = false
            wingOut = false
        }
    }

    fun setDT (newDt : Float){
        deltaTime = newDt
    }
    fun setT (newTime : Float){
        time = newTime
    }

    override fun update(deltaTime: Float, time: Float){
        setDT(deltaTime)
        setT(time)

        if (moveWingOut && wingOut == false){
            wingRotationT = clampf( wingRotationT + deltaTime * wingRotationSpeed, 0f,1f)
            wingSetRotation(wingRotationT)
        }

        if (!moveWingOut && wingIn == false){
            wingRotationT = clampf( wingRotationT - deltaTime * wingRotationSpeed, 0f,1f)
            wingSetRotation(wingRotationT)
        }
        turbineFireTransform.rotate(0f,0f, Random.nextInt(45,180).toFloat())
        weapon.update(deltaTime, time, wingOut)
        for (each in colliderList) each.update()
    }

}