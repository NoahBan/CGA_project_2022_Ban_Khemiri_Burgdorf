package cga.exercise.game

import cga.framework.GameWindow

/*
  Created by Fabian on 16.09.2017.
 */
class Game(width: Int,
           height: Int,
           fullscreen: Boolean = false,
           vsync: Boolean = false,
           title: String = "Testgame",
           GLVersionMajor: Int = 4,
           GLVersionMinor: Int = 4) : GameWindow(width, height, fullscreen, vsync, GLVersionMajor, GLVersionMinor, title, 0, 144.0f) {

    private val scene: Scene
    init {
        setCursorVisible(true)
        scene = Scene(this)
    }
    override fun shutdown() = scene.cleanup()

    override fun update(dt: Float, t: Float) = scene.update(dt, t)

    override fun render(dt: Float, t: Float) = scene.render(dt, t, m_window)

    override fun onMouseMove(xpos: Double, ypos: Double) = scene.onMouseMove(xpos, ypos)

    override fun onKey(key: Int, scancode: Int, action: Int, mode: Int) = scene.onKey(key, scancode, action, mode)

}