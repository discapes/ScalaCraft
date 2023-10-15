package dev.miikat.scalacraft.engine

import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.io.FileNotFoundException
import scala.util.Using
import org.lwjgl.system.MemoryStack

import org.lwjgl.stb.STBImage.*
import java.io.IOException

object Util:
  def resourceToByteBuffer(name: String, insterSlash: Boolean = true): ByteBuffer =
    val fixedName = "/" + name
    val stream = getClass.getResourceAsStream(fixedName)
    if stream == null then 
      val err = s"ERROR: Failed to open resource $fixedName!"
      // we print it because this might be called from native code
      // so the exception won't go through
      System.err.println(err)
      throw FileNotFoundException(err)
      
    val bytes = stream.readAllBytes()
    // https://stackoverflow.com/questions/69478753/failed-to-load-image-using-stbi-load-lwjgl-used
    //  ByteBuffer.wrap(bytes) won't work
    val buf = MemoryUtil.memAlloc(bytes.length).put(bytes).position(0)
    buf
    
  def loadImage(name: String, flip: Boolean = false) =
    Using.resource(MemoryStack.stackPush()): stack =>
      val w = stack.mallocInt(1)
      val h = stack.mallocInt(1)
      val channels = stack.mallocInt(1)
      val pngBuf = Util.resourceToByteBuffer(name)
      stbi_set_flip_vertically_on_load(flip)
      val buf = stbi_load_from_memory(pngBuf, w, h, channels, 4)
      MemoryUtil.memFree(pngBuf)
      if buf == null then throw IOException(stbi_failure_reason())
      (w.get(), h.get(), buf)
