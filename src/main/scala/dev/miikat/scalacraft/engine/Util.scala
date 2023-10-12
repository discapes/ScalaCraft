package dev.miikat.scalacraft.engine

import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.io.FileNotFoundException

object Util:
  def resourceToByteBuffer(name: String): ByteBuffer =
    val stream = getClass.getResourceAsStream(name)
    if stream == null then 
      val err = s"ERROR: Failed to open resource $name!"
      // we print it because this might be called from native code
      // so the exception won't go through
      System.err.println(err)
      throw FileNotFoundException(err)
      
    val bytes = stream.readAllBytes()
    // https://stackoverflow.com/questions/69478753/failed-to-load-image-using-stbi-load-lwjgl-used
    //  ByteBuffer.wrap(bytes) won't work
    val buf = MemoryUtil.memAlloc(bytes.length).put(bytes).position(0)
    buf