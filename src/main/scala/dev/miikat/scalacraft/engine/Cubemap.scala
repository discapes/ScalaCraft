package dev.miikat.scalacraft.engine

import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL12C.*
import org.lwjgl.opengl.GL13C.*
import org.lwjgl.opengl.GL14C.*
import org.lwjgl.opengl.GL15C.*
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL21C.*
import org.lwjgl.opengl.GL30C.*
import org.lwjgl.opengl.GL31C.*
import org.lwjgl.opengl.GL32C.*
import org.lwjgl.opengl.GL33C.*
import org.lwjgl.opengl.GL40C.*
import org.lwjgl.opengl.GL41C.*
import org.lwjgl.opengl.GL42C.*
import org.lwjgl.opengl.GL43C.*
import org.lwjgl.opengl.GL44C.*
import org.lwjgl.opengl.GL45C.*

import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import scala.util.Using
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer
import java.io.IOError
import java.io.IOException

class Cubemap(facePaths: Iterable[String]):
  val id = glCreateTextures(GL_TEXTURE_CUBE_MAP)

  for (facePath, index) <- facePaths.zipWithIndex do
    val (w, h, data) = Util.loadImage(facePath)
    if index == 0 then glTextureStorage2D(id, 1, GL_RGBA8, w, h)
    // A cubemap under DSA is treated as though it were a 2D array texture 33 with exactly 6 layers.
    // You could likewise consider it to be a cubemap array 26 with only one layer (and therefore 6 
    // layer-faces). It’s the same either way.
    glTextureSubImage3D(id, 0, 0, 0, index, w, h, 1, GL_RGBA, GL_UNSIGNED_BYTE, data)
    stbi_image_free(data)

  glTextureParameteri(id, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
  glTextureParameteri(id, GL_TEXTURE_MAG_FILTER, GL_NEAREST)  
  glTextureParameteri(id, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
  glTextureParameteri(id, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
  glTextureParameteri(id, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)
  glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
  glGenerateTextureMipmap(id)

  def bind(unit: Int) = glBindTextureUnit(unit, id)

