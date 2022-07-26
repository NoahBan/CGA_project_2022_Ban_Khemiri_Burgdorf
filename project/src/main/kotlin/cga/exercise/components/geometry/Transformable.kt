package cga.exercise.components.geometry

import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f

open class Transformable(private var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {

    open fun getModelMatrix(): Matrix4f {
        return Matrix4f(modelMatrix)
    }

    fun getWorldModelMatrix(): Matrix4f {
        var temp = Matrix4f(modelMatrix)

        if (this.parent == null) {
            return temp
        } else {
            return parent!!.getWorldModelMatrix().mul(temp)
        }
    }

    fun rotate(pitch: Float, yaw: Float, roll: Float) {
        //modelMatrix.rotateXYZ((pitch),(yaw),(roll))
        modelMatrix.rotateXYZ(Math.toRadians(pitch),Math.toRadians(yaw),Math.toRadians(roll))
    }

    fun setRotation(pitch: Float, yaw: Float, roll: Float){
        modelMatrix.setRotationXYZ(Math.toRadians(pitch),Math.toRadians(yaw),Math.toRadians(roll))
    }

    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        val temp = Matrix4f().translate(altMidpoint)
        var mMatrix = Matrix4f(modelMatrix)
        temp.translate(Vector3f(altMidpoint).negate())
        temp.rotateXYZ(pitch,yaw,roll)
        modelMatrix = temp.mul(mMatrix)
    }

    fun rotateAroundWorldPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        val temp = Matrix4f().translate(altMidpoint)
        var mMatrix = Matrix4f(getWorldModelMatrix())
        temp.translate(Vector3f(altMidpoint).negate())
        temp.rotateXYZ(pitch,yaw,roll)
        modelMatrix = temp.mul(mMatrix)
    }

    fun translate(deltaPos: Vector3f) {
        modelMatrix.translate(deltaPos)
    }

    fun setPosition(deltaPos: Vector3f) {
        modelMatrix.m30(deltaPos[0])
        modelMatrix.m31(deltaPos[1])
        modelMatrix.m32(deltaPos[2])
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: this operation has to be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    fun preTranslate(deltaPos: Vector3f) {
        if (this.parent != null) modelMatrix.mul(this.parent!!.getWorldModelMatrix())
    }

    fun scale(scale : Vector3f) {
        modelMatrix.scale(scale)
    }

    fun scaling(scale : Vector3f){
        modelMatrix.scaling(scale)
    }

    fun getPosition(): Vector3f {

        return Vector3f(
            modelMatrix.m30(),
            modelMatrix.m31(),
            modelMatrix.m32()
        )
    }

    fun getWorldPosition(): Vector3f {
        var temp = getWorldModelMatrix()

        return Vector3f(
            temp.m30(),
            temp.m31(),
            temp.m32()
        )
    }

    fun getRotation(): Vector3f {
        var yaw = 0f
        var pitch = 0f
        var roll = 0f

        if (getWorldModelMatrix().m00().toDouble() == 1.0 || modelMatrix.m00().toDouble() == -1.0) {
            yaw = Math.atan2(modelMatrix.m02(), modelMatrix.m23())
            //pitch and roll remain = 0;
        } else {
            yaw = Math.atan2(-modelMatrix.m20(), modelMatrix.m00())
            pitch = Math.asin(modelMatrix.m10())
            roll = Math.atan2(-modelMatrix.m12(), modelMatrix.m11())
        }
        return Vector3f(-pitch, -yaw, -roll)
    }


    fun getXAxis(): Vector3f {
        return Vector3f(modelMatrix.m00(),modelMatrix.m01(),modelMatrix.m02()).normalize()
    }

    fun getYAxis(): Vector3f {
        return Vector3f(modelMatrix.m10(),modelMatrix.m11(),modelMatrix.m12()).normalize()
    }

    fun getZAxis(): Vector3f {
        return Vector3f(modelMatrix.m20(),modelMatrix.m21(),modelMatrix.m22()).normalize()
    }

    fun getWorldXAxis(): Vector3f {
        var tmpMat = getWorldModelMatrix()
        return Vector3f(tmpMat.m00(),tmpMat.m01(),tmpMat.m02()).normalize()
    }

    fun getWorldYAxis(): Vector3f {
        var tmpMat = getWorldModelMatrix()
        return Vector3f(tmpMat.m10(),tmpMat.m11(),tmpMat.m12()).normalize()
    }

    fun getWorldZAxis(): Vector3f {
        var tmpMat = getWorldModelMatrix()
        return Vector3f(tmpMat.m20(),tmpMat.m21(),tmpMat.m22()).normalize()
    }

    fun setXAxis(vector3f : Vector3f) {
        var tmpMat = getWorldModelMatrix()
        modelMatrix.m00(vector3f[0])
        modelMatrix.m01(vector3f[1])
        modelMatrix.m02(vector3f[2])
    }

    fun setYAxis(vector3f : Vector3f) {
        var tmpMat = getWorldModelMatrix()
        modelMatrix.m10(vector3f[0])
        modelMatrix.m11(vector3f[1])
        modelMatrix.m12(vector3f[2])
    }

    fun setZAxis(vector3f : Vector3f) {
        var tmpMat = getWorldModelMatrix()
        modelMatrix.m20(vector3f[0])
        modelMatrix.m21(vector3f[1])
        modelMatrix.m22(vector3f[2])
    }
}