package cga.exercise.components.geometry

import org.joml.Math
import org.joml.Matrix4f
import org.joml.Vector3f

open class Transformable(private var modelMatrix: Matrix4f = Matrix4f(), var parent: Transformable? = null) {
    /**
     * Returns copy of object model matrix
     * @return modelMatrix
     */
    open fun getModelMatrix(): Matrix4f {

        return Matrix4f(modelMatrix)


    }

    /**
     * Returns multiplication of world and object model matrices.
     * Multiplication has to be recursive for all parents.
     * Hint: scene graph
     * @return world modelMatrix
     */
    fun getWorldModelMatrix(): Matrix4f {
        var temp = Matrix4f(getModelMatrix())

        if (this.parent == null) {
            return temp
        } else {
            return parent!!.getWorldModelMatrix().mul(temp)
        }
    }

    /**
     * Rotates object around its own origin.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     */
    fun rotate(pitch: Float, yaw: Float, roll: Float) {

        modelMatrix.rotateXYZ(Math.toRadians(pitch),Math.toRadians(yaw),Math.toRadians(roll))

    }

    /**
     * Rotates object around given rotation center.
     * @param pitch radiant angle around x-axis ccw
     * @param yaw radiant angle around y-axis ccw
     * @param roll radiant angle around z-axis ccw
     * @param altMidpoint rotation center
     */
    fun rotateAroundPoint(pitch: Float, yaw: Float, roll: Float, altMidpoint: Vector3f) {
        val temp = Matrix4f().translate(altMidpoint)
        temp.rotateXYZ(pitch,yaw,roll)
        temp.translate(Vector3f(altMidpoint).negate())
        modelMatrix = temp.mul(modelMatrix)
    }

    /**
     * Translates object based on its own coordinate system.
     * @param deltaPos delta positions
     */
    fun translate(deltaPos: Vector3f) {
         modelMatrix.translate(deltaPos)
    }

    /**
     * Translates object based on its parent coordinate system.
     * Hint: this operation has to be left-multiplied
     * @param deltaPos delta positions (x, y, z)
     */
    fun preTranslate(deltaPos: Vector3f) {
        if (this.parent != null) modelMatrix.mul(this.parent!!.getWorldModelMatrix())
    }

    /**
     * Scales object related to its own origin
     * @param scale scale factor (x, y, z)
     */
    fun scale(scale: Vector3f) {
        modelMatrix.scale(scale)
    }

    /**
     * Returns position based on aggregated translations.
     * Hint: last column of model matrix
     * @return position
     */
    fun getPosition(): Vector3f {

        return Vector3f(
            modelMatrix.m30(),
            modelMatrix.m31(),
            modelMatrix.m32()
        )
    }

    /**
     * Returns position based on aggregated translations incl. parents.
     * Hint: last column of world model matrix
     * @return position
     */
    fun getWorldPosition(): Vector3f {
        var temp = getWorldModelMatrix()

        return Vector3f(
            temp.m30(),
            temp.m31(),
            temp.m32()
        )
    }

    /**
     * Returns x-axis of object coordinate system
     * Hint: first normalized column of model matrix
     * @return x-axis
     */
    fun getXAxis(): Vector3f {
        return Vector3f(modelMatrix.m00(),modelMatrix.m01(),modelMatrix.m02()).normalize()
    }

    /**
     * Returns y-axis of object coordinate system
     * Hint: second normalized column of model matrix
     * @return y-axis
     */
    fun getYAxis(): Vector3f {
        return Vector3f(modelMatrix.m10(),modelMatrix.m11(),modelMatrix.m12()).normalize()
    }

    /**
     * Returns z-axis of object coordinate system
     * Hint: third normalized column of model matrix
     * @return z-axis
     */
    fun getZAxis(): Vector3f {
        return Vector3f(modelMatrix.m20(),modelMatrix.m21(),modelMatrix.m22()).normalize()
    }

    /**
     * Returns x-axis of world coordinate system
     * Hint: first normalized column of world model matrix
     * @return x-axis
     */
    fun getWorldXAxis(): Vector3f {
        var tmpMat = getWorldModelMatrix()
        return Vector3f(tmpMat.m00(),tmpMat.m01(),tmpMat.m02()).normalize()
    }

    /**
     * Returns y-axis of world coordinate system
     * Hint: second normalized column of world model matrix
     * @return y-axis
     */
    fun getWorldYAxis(): Vector3f {
        var tmpMat = getWorldModelMatrix()
        return Vector3f(tmpMat.m10(),tmpMat.m11(),tmpMat.m12()).normalize()
    }

    /**
     * Returns z-axis of world coordinate system
     * Hint: third normalized column of world model matrix
     * @return z-axis
     */
    fun getWorldZAxis(): Vector3f {
        var tmpMat = getWorldModelMatrix()
        return Vector3f(tmpMat.m20(),tmpMat.m21(),tmpMat.m22()).normalize()
    }
}