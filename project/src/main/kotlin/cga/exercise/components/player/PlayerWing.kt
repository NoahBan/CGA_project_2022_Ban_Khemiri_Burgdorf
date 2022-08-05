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
import kotlin.random.nextInt

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


    public var wingDestroyed = false

    private var rotationDir = 1f

    private var weapon : PlayerWeapon

    val turbineLight : PointLight
    val turbineLightColor = Vector3f(76/255f,213/255f,253/255f)
    val turbineLightIntensityMax = 32
    val turbineLightIntensityMin = 28

    var collided = false

    val collider1 : Collider
    val collider2 : Collider
    val collider3 : Collider
    val collider4 : Collider
    val collider5 : Collider
    val collider6 : Collider
    val collider7 : Collider
    val collider8 : Collider
    val collider9 : Collider
    val collider10 : Collider
    val collider11 : Collider
    val collider12 : Collider
    val collider13 : Collider

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
                turbineFireTransform.setPosition(Vector3f(-0.32f, 0.54f, 0.6f))
                turbineFireMesh = Renderable(playerGeo.fire.renderList, Matrix4f(), turbineFireTransform)
                turbineLight = PointLight(AttenuationType.QUADRATIC,turbineLightColor, 0.3f, Matrix4f(), turbineFireTransform)

            }
            WingType.OR -> {
                weapon = PlayerWeapon(rotationDir, playerGeo, Matrix4f(), this)
                wingGeo.renderList = playerGeo.wingR.renderList
                setPosition(Vector3f(0.453f,0.09f,0.735f))
                wingGeo.parent = this
                minRotation *= -1f
                weapon.setPosition(Vector3f(2.22f,0.9f,-0.69f))
                turbineFireTransform = Transformable(Matrix4f(), this)
                turbineFireTransform.setPosition(Vector3f(0.32f, 0.54f, 0.6f))
                turbineFireMesh = Renderable(playerGeo.fire.renderList, Matrix4f(), turbineFireTransform)
                turbineLight = PointLight(AttenuationType.QUADRATIC,turbineLightColor, 0.3f, Matrix4f(), turbineFireTransform)
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
                turbineFireTransform = Transformable(Matrix4f(), this)
                turbineFireTransform.setPosition(Vector3f(-0.32f, -0.54f, 0.6f))
                turbineFireMesh = Renderable(playerGeo.fire.renderList, Matrix4f(), turbineFireTransform)
                turbineLight = PointLight(AttenuationType.LINEAR,turbineLightColor, 0.3f, Matrix4f(), turbineFireTransform)
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
                turbineFireTransform = Transformable(Matrix4f(), this)
                turbineFireTransform.setPosition(Vector3f(0.32f, -0.54f, 0.6f))
                turbineFireMesh = Renderable(playerGeo.fire.renderList, Matrix4f(), turbineFireTransform)
                turbineLight = PointLight(AttenuationType.LINEAR,turbineLightColor, 0.3f, Matrix4f(), turbineFireTransform)
            }
        }
        turbineLight.translate(Vector3f(0f,0f,0.3f))

        wingSetRotation(0f)

        globalLightHandler.addPointLight(turbineLight)

        collider1 = Collider(ColliderType.PLAYERCOLLIDER,0.3f)
        collider1.parent = wingGeo
        collider1.setPosition(Vector3f(0.4f*rotationDir,0.5F,0f))

        colliderWeapon = Collider(ColliderType.PLAYERCOLLIDER,0.1f)
        colliderWeapon.parent = weapon.weaponEndGeo

        collider2 = Collider(ColliderType.PLAYERCOLLIDER,0.4f)
        collider2.parent = wingGeo
        collider2.setPosition(Vector3f(0.4f*rotationDir,0.4F,-0.5f))
        collider3 = Collider(ColliderType.PLAYERCOLLIDER,0.4f)
        collider3.parent = wingGeo
        collider3.setPosition(Vector3f(0.4f*rotationDir,0.4F,-1f))
        collider4 = Collider(ColliderType.PLAYERCOLLIDER,0.3f)
        collider4.parent = wingGeo
        collider4.setPosition(Vector3f(0.9f*rotationDir,0.4F,-0.1f))
        collider5 = Collider(ColliderType.PLAYERCOLLIDER,0.3f)
        collider5.parent = wingGeo
        collider5.setPosition(Vector3f(0.9f*rotationDir,0.4F,-0.5f))
        collider6 = Collider(ColliderType.PLAYERCOLLIDER,0.3f)
        collider6.parent = wingGeo
        collider6.setPosition(Vector3f(0.9f*rotationDir,0.4F,-0.9f))

        collider7 = Collider(ColliderType.PLAYERCOLLIDER,0.25f)
        collider7.parent = wingGeo
        collider7.setPosition(Vector3f(1.4f*rotationDir,0.55F,-0.1f))
        collider8 = Collider(ColliderType.PLAYERCOLLIDER,0.25f)
        collider8.parent = wingGeo
        collider8.setPosition(Vector3f(1.4f*rotationDir,0.55F,-0.5f))
        collider9 = Collider(ColliderType.PLAYERCOLLIDER,0.25f)
        collider9.parent = wingGeo
        collider9.setPosition(Vector3f(1.4f*rotationDir,0.55F,-0.9f))

        collider10 = Collider(ColliderType.PLAYERCOLLIDER,0.25f)
        collider10.parent = wingGeo
        collider10.setPosition(Vector3f(1.9f*rotationDir,0.7F,-0.1f))
        collider11 = Collider(ColliderType.PLAYERCOLLIDER,0.25f)
        collider11.parent = wingGeo
        collider11.setPosition(Vector3f(1.9f*rotationDir,0.7F,-0.5f))
        collider12 = Collider(ColliderType.PLAYERCOLLIDER,0.25f)
        collider12.parent = wingGeo
        collider12.setPosition(Vector3f(1.9f*rotationDir,0.7F,-0.9f))

        collider13 = Collider(ColliderType.PLAYERCOLLIDER,0.25f)
        collider13.parent = wingGeo
        collider13.setPosition(Vector3f(2.2f*rotationDir,0.85F,-1.3f))

        colliderList = listOf(collider1, collider2, collider3, collider4, collider5, collider6, collider7, collider8,
            collider9, collider10, collider11, collider12, collider13, colliderWeapon)

    }

    fun getShotPos() : Vector3f = weapon.getShotPos()

    fun toggleWingMode(){
        moveWingOut = !moveWingOut
    }

    override fun render(shaderProgram : ShaderProgram){
        wingGeo.render(shaderProgram)
        weapon.render(shaderProgram)
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
        turbineLight.intensity = Random.nextInt(turbineLightIntensityMin,turbineLightIntensityMax)/100f
            for (each in colliderList) {
                each.update()
                if(each.collided) wingDestroyed = true
            }
        if (wingDestroyed){
            moveWingOut = false
        }

        if (moveWingOut && wingOut == false ){ //&& !wingDestroyed
            wingRotationT = clampf( wingRotationT + deltaTime * wingRotationSpeed, 0f,1f)
            wingSetRotation(wingRotationT)
        }

        if (!moveWingOut && wingIn == false){
            wingRotationT = clampf( wingRotationT - deltaTime * wingRotationSpeed, 0f,1f)
            wingSetRotation(wingRotationT)
        }



        turbineFireMesh.renderList[0].material?.movingV = Random.nextInt(30,70)/10f
        weapon.update(deltaTime, time, wingOut)
            if (!moveWingOut && wingIn == false ){ //&& !wingDestroyed
                wingRotationT = clampf( wingRotationT - deltaTime * wingRotationSpeed, 0f,1f)
                wingSetRotation(wingRotationT)
            }
        for (each in colliderList) each.update()
    }

}