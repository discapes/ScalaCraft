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
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.io.IOError
import java.io.IOException

class Texture(name: String):
  val id = glCreateTextures(GL_TEXTURE_2D)
  val (w, h, data) = Util.loadImage(name, flip = true)

  glTextureParameteri(id, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
  glTextureParameteri(id, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
  glPixelStorei(GL_UNPACK_ALIGNMENT, 1)

  glTextureStorage2D(id, 1, GL_RGBA8, w, h)
  glTextureSubImage2D(id, 0, 0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, data)
  glGenerateTextureMipmap(id)
  stbi_image_free(data)

  def bind(unit: Int) = glBindTextureUnit(unit, id)
