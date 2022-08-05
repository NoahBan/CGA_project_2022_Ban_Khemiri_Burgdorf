package cga.exercise.components.player

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.projectile.PlayerProjectile
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.utility.*
import cga.exercise.game.globalLightHandler
import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f

class PlayerObject(modelMatrix : Matrix4f, parent: Transformable? = null) : Transformable(modelMatrix, parent) {

    private var deltaTime = 0f
    private var time = 0f


    private val playerGeo : PlayerGeo

    private val maxUpDownRotation = Math.toRadians(30f)
    private val maxLeftRightRotation = Math.toRadians(70f)
    private val rotationResetSpeed = 0.1f
    private val maxUp = 10f
    private val maxRight = 18f
    private val maxDown = -7f
    private val maxLeft = maxRight * -1f
    private val speed = 15f
    private val rotationUpDownSpeed = Math.toRadians(160f)
    private val rotationLeftRightSpeed = Math.toRadians(160f)
    private var countFunctioningWings = 4
    private var allWingsDestroyed = false
    public  var isDead = false
    private var driftOfSpeed = 0f

    var nextWeaponToShoot = 0

    var rollParent = Transformable(Matrix4f(), this)

    val playerPartsList : MutableList<PlayerPart>
    var wingList : MutableList<PlayerWing>

    private val body : PlayerBody
    private val wingOL : PlayerWing
    private val wingOR : PlayerWing
    private val wingUR : PlayerWing
    private val wingUL : PlayerWing

    private val weaponAlignTarget = Transformable(Matrix4f(), this)

    var moveUp = false
    var moveDown = false
    var moveLeft = false
    var moveRight = false

    var shoot = false

    val playerProjectileList = mutableListOf<PlayerProjectile>()

    init {
        playerGeo = PlayerGeo()

        body = PlayerBody(playerGeo,Matrix4f(),rollParent)
        wingOL = PlayerWing(playerGeo,WingType.OL,Matrix4f(),rollParent)
        wingOR = PlayerWing(playerGeo,WingType.OR,Matrix4f(),rollParent)
        wingUL = PlayerWing(playerGeo,WingType.UL,Matrix4f(),rollParent)
        wingUR = PlayerWing(playerGeo,WingType.UR,Matrix4f(),rollParent)
        playerPartsList = mutableListOf(
            wingOL, wingOR, wingUR, wingUL, body
        )
        wingList = mutableListOf(
            wingOL,wingUR,wingUL,wingOR
        )


        weaponAlignTarget.translate(Vector3f(0f,0f,-500f))
    }
    fun calculateNextWeaponToShoot(currentWeapon : Int): Int{
        var found = false
        if (countFunctioningWings == 0) return -1
        var nextWeapon = (currentWeapon + 1) % wingList.size
        while (!found){
            if (wingList[nextWeapon].wingDestroyed){
                nextWeapon = (nextWeapon + 1) % wingList.size
            } else {
                found = true
            }
        }
        return nextWeapon
    }
    fun setShoot(){shoot = true}
    fun shoot(weapon : Int){
        val shot = wingList[weapon].getShotPos()

        var target = weaponAlignTarget.getWorldPosition()
        var up = Vector3f(0f, 1f, 0f)
        var newMatrix = Matrix4f()
        newMatrix.lookAt(shot, target, up).invert().normalize3x3()

        val newProjectile = PlayerProjectile(time,playerGeo.schuss.renderList, newMatrix)

        playerProjectileList.add(newProjectile)

//        println(playerProjectileList.size)
    }

    fun render(shaderProgram : ShaderProgram){
        for (each in playerPartsList) each.render(shaderProgram)
        for (each in playerProjectileList) each.render(shaderProgram)
    }

    fun setDT (newDt : Float){
        deltaTime = newDt
    }
    fun setT (newTime : Float){
        time = newTime
    }

    fun setMoveUp(){
        moveUp = true
    }
    fun setMoveDown() {
        moveDown = true
    }

    fun moveUpDown(dt : Float, dir : Float){
        var matrix = getModelMatrix()
        val newVal = clampf(matrix.m31()+speed*dt* dir, maxDown,maxUp)
        matrix.m31(newVal)

        var eulers = Vector3f(0f,0f,0f)
        matrix.getEulerAnglesZYX(eulers)
        var newRotation = eulers[0] + rotationUpDownSpeed * dt * dir
        matrix = setEuler(matrix, clampf(newRotation, -maxUpDownRotation,maxUpDownRotation),eulers[1],eulers[2])

        setModelMatrix(matrix)
    }

    fun vertRotationReset(dt : Float){
        var matrix = getModelMatrix()
        var eulers = Vector3f(0f,0f,0f)
        matrix.getEulerAnglesZYX(eulers)
        var newRotation = lerpV3f(eulers,Vector3f(0f,0f,0f), rotationResetSpeed)
        matrix = setEuler(matrix, newRotation[0],newRotation[1],newRotation[2])

        setModelMatrix(matrix)
    }
    fun setMoveLeft(){
        moveLeft = true
    }
    fun setMoveRight(){
        moveRight = true
    }

    fun moveLeftRight(dt : Float, dir: Float){
        var matrix = getModelMatrix()
        val newVal = clampf(matrix.m30()-speed*dt* dir, maxLeft,maxRight)
        matrix.m30(newVal)
        setModelMatrix(matrix)

        var xBodyMat = rollParent.getModelMatrix()
        var eulers = Vector3f(0f,0f,0f)
        xBodyMat.getEulerAnglesZYX(eulers)
        var newRotation = eulers[2] + rotationLeftRightSpeed * dt * dir
        xBodyMat = setEuler(xBodyMat, eulers[0], eulers[1],clampf(newRotation, -maxLeftRightRotation,maxLeftRightRotation))
        rollParent.setModelMatrix(xBodyMat)
    }

    fun horizRotationReset(dt : Float){
        var matrix = rollParent.getModelMatrix()
        var eulers = Vector3f(0f,0f,0f)
        matrix.getEulerAnglesZYX(eulers)
        var newRotation = lerpV3f(eulers,Vector3f(0f,0f,0f), rotationResetSpeed)
        matrix = setEuler(matrix, newRotation[0],newRotation[1],newRotation[2])

        rollParent.setModelMatrix(matrix)
    }

    fun toggleWingMode(){
        wingOL.toggleWingMode()
        wingOR.toggleWingMode()
        wingUL.toggleWingMode()
        wingUR.toggleWingMode()
    }

    fun update(deltaTime: Float, time: Float){
        setDT(deltaTime)
        setT(time)
        if (!body.bodyDestroyed && !allWingsDestroyed) {
            if (moveUp && !moveDown) moveUpDown(deltaTime, 1f)
            moveUp = false
            if (moveDown && !moveUp) moveUpDown(deltaTime, -1f)
            moveDown = false
            if (moveLeft && !moveRight) moveLeftRight(deltaTime, 1f)
            moveLeft = false
            if (moveRight && !moveLeft) moveLeftRight(deltaTime, -1f)
            moveRight = false

            if (!moveDown && !moveUp) {
                vertRotationReset(deltaTime)
            }
            if (!moveLeft && !moveRight) {
                horizRotationReset(deltaTime)
            }

            for (each in playerPartsList) each.update(deltaTime, time)
            for (each in playerProjectileList) each.update(deltaTime, time)
            countFunctioningWings = 4
            for (each in wingList) if (each.wingDestroyed) countFunctioningWings--
            if (countFunctioningWings == 0) allWingsDestroyed = true
                println("functioning wings  " + countFunctioningWings)
                println(allWingsDestroyed)
            if (shoot && !allWingsDestroyed){
                nextWeaponToShoot = calculateNextWeaponToShoot(nextWeaponToShoot)
                if (wingList[nextWeaponToShoot].weapon.weaponOut){
                    shoot(nextWeaponToShoot)
                }
            }
            shoot = false

            val tmp = mutableListOf<Int>()
            playerProjectileList.forEachIndexed { index, element ->
                if (element.shouldIdie){
                    tmp.add(index)
                    globalLightHandler.removePointLight(element.light)
                }
            }
                for (each in tmp.asReversed()) playerProjectileList.removeAt(each)
            }
        else
            {
                for (each in playerPartsList) each.update(deltaTime, time)
                for (each in playerProjectileList) each.update(deltaTime, time)
                isDead = true
                var flightDirection = 1
                if (getWorldPosition().x < 0) flightDirection = -1
                if (driftOfSpeed < 1.5f) {
                    translate(Vector3f(flightDirection * driftOfSpeed / 4,driftOfSpeed / 2, driftOfSpeed))
                    rotate(flightDirection * driftOfSpeed,driftOfSpeed * flightDirection, driftOfSpeed)
                    driftOfSpeed += 0.014f
                }
            }
    }
}