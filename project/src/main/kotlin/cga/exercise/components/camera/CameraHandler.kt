package cga.exercise.components.camera

class CameraHandler(val cameraList : MutableList<Camera> = mutableListOf()) {

    private var activeCam = 0

    fun nextCam() {
        activeCam++
        activeCam %= cameraList.size
    }

    fun prevCam() {
        if ((activeCam - 1) < 0) activeCam = cameraList.size - 1
        else activeCam--
    }

    fun getActiveCamera() : Camera = cameraList[activeCam]

    fun addCamera(newCam : Camera) = cameraList.add(newCam)

}