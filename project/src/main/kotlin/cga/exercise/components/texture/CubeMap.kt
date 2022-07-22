package cga.exercise.components.texture

import cga.framework.GLError.checkEx
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.EXTTextureFilterAnisotropic
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer

class CubeMap(faces : Array<String>, genMipMaps: Boolean) {
    private var texID: Int = -1
    var cubemapTexture : Int
    init {
        cubemapTexture = loadCubemap(faces, genMipMaps)
    }

    fun loadCubemap(faces : Array<String>, genMipMaps: Boolean): Int {
        texID = GL30.glGenTextures()

        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP,texID)

        //flip y coordinate to make OpenGL happy
        STBImage.stbi_set_flip_vertically_on_load(genMipMaps)
        for (i in 0..5){
            var x = BufferUtils.createIntBuffer(1)                         //die MÜSSEN hier deklariert werden, sonst gibt es eine Exception
            val y = BufferUtils.createIntBuffer(1)                         //habs auch nur halb verstanden, aber es geht hier darum, dass immer neue pointer generiert werden müssen glaub ich
            val readChannels = BufferUtils.createIntBuffer(1)
            var imageData = STBImage.stbi_load(faces[i],x,y,readChannels,0)//Fuer cubemap steht da 0 desired channels
                ?: throw Exception("Image file \"" + faces[i] + "\" couldn't be read:\n" + STBImage.stbi_failure_reason())
            try {
                GL30.glTexImage2D(GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,0,GL30.GL_RGB,x.get(),y.get(),0,GL30.GL_RGB,GL30.GL_UNSIGNED_BYTE,imageData)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                throw ex
            } finally {
                STBImage.stbi_image_free(imageData)
            }
        }
        println("subba")
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR)
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR)
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE)
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE)
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_WRAP_R, GL30.GL_CLAMP_TO_EDGE)

        return  texID
    }

    fun processTexture(){

    }
}
