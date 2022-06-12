package cga.exercise.components.texture

import cga.framework.GLError.checkEx
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.EXTTextureFilterAnisotropic
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer


/**
 * Created by Fabian on 16.09.2017.
 */
class Texture2D(imageData: ByteBuffer, width: Int, height: Int, genMipMaps: Boolean): ITexture{
    private var texID: Int = -1
        private set

    init {
        try {
            processTexture(imageData, width, height, genMipMaps)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }
    companion object {
        //create texture from file
        //don't support compressed textures for now
        //instead stick to pngs
        operator fun invoke(path: String, genMipMaps: Boolean): Texture2D {
            val x = BufferUtils.createIntBuffer(1)
            val y = BufferUtils.createIntBuffer(1)
            val readChannels = BufferUtils.createIntBuffer(1)
            //flip y coordinate to make OpenGL happy
            STBImage.stbi_set_flip_vertically_on_load(true)
            val imageData = STBImage.stbi_load(path, x, y, readChannels, 4)
                    ?: throw Exception("Image file \"" + path + "\" couldn't be read:\n" + STBImage.stbi_failure_reason())
            try {
                return Texture2D(imageData, x.get(), y.get(), genMipMaps)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                throw ex
            } finally {
                STBImage.stbi_image_free(imageData)
            }
        }
    }

    override fun processTexture(imageData: ByteBuffer, width: Int, height: Int, genMipMaps: Boolean) {
        // todo 3.1

    }

    override fun setTexParams(wrapS: Int, wrapT: Int, minFilter: Int, magFilter: Int) {
        // todo 3.1
    }

    override fun bind(textureUnit: Int) {
        // todo 3.1
    }

    override fun unbind() {
        // todo 3.1
    }

    override fun cleanup() {
        unbind()
        if (texID != 0) {
            GL11.glDeleteTextures(texID)
            texID = 0
        }
    }
}