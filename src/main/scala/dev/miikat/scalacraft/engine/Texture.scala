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

class Texture(name: String):
  val id = glCreateTextures(GL_TEXTURE_2D)
  val (w, h, data) = loadImage(name)

  glTextureParameteri(id, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
  glTextureParameteri(id, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
  glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

  glTextureStorage2D(id, 1, GL_RGBA8, w, h)
  glTextureSubImage2D(id, 0, 0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, data)
  glGenerateTextureMipmap(id)

  def bind() = glBindTextureUnit(0, id)

  private def resourceToByteBuffer(name: String) =
    val bytes = getClass.getResourceAsStream(name).readAllBytes()
    // https://stackoverflow.com/questions/69478753/failed-to-load-image-using-stbi-load-lwjgl-used
    //  ByteBuffer.wrap(bytes) won't work
    MemoryUtil.memAlloc(bytes.length).put(bytes).position(0)

  private def loadImage(name: String) =
    Using.resource(MemoryStack.stackPush()): stack =>
      val w = stack.mallocInt(1);
      val h = stack.mallocInt(1);
      val channels = stack.mallocInt(1);
      val pngBuf = resourceToByteBuffer(name)
      val buf = stbi_load_from_memory(pngBuf, w, h, channels, 4)
      MemoryUtil.memFree(pngBuf)
      if buf == null then throw IOException(stbi_failure_reason())
      (w.get(), h.get(), buf)
