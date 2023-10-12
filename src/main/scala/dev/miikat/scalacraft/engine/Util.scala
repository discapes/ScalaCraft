package dev.miikat.scalacraft.engine

import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

object Util:
  def resourceToByteBuffer(name: String): ByteBuffer =
    val bytes = getClass.getResourceAsStream(name).readAllBytes()
    // https://stackoverflow.com/questions/69478753/failed-to-load-image-using-stbi-load-lwjgl-used
    //  ByteBuffer.wrap(bytes) won't work
    MemoryUtil.memAlloc(bytes.length).put(bytes).position(0)