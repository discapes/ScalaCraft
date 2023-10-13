package dev.miikat.scalacraft.engine

import org.joml.*
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
import org.lwjgl.system.MemoryUtil



case class Scene(
  entities: Array[Entity],
  lights: Array[Light],
  ambientLight: Vector3f = Vector3f(0.1f, 0.1f, 0.1f)
):

  def bindLightsUniform(): Unit =
    val pointLights = this.lights.filter(_.isInstanceOf[Light.Point]).map(_.asInstanceOf[Light.Point])
    val dirLights = this.lights.filter(_.isInstanceOf[Light.Directional]).map(_.asInstanceOf[Light.Directional])
    val spotLights = this.lights.filter(_.isInstanceOf[Light.Spot]).map(_.asInstanceOf[Light.Spot])
    
    val buf = MemoryUtil.memCalloc(4096)
    def insertVec3(v: Vector3f) =
      v.get(buf)
      buf.position(buf.position + 4 * 3)
    
    buf.putInt(pointLights.length)
    buf.putInt(dirLights.length)
    buf.putInt(spotLights.length)
    buf.putFloat(0f)

    pointLights.foreach: pl =>
      insertVec3(pl.color)
      buf.putFloat(pl.linear)
      insertVec3(pl.pos)
      buf.putFloat(pl.quadratic)
    val blankPointLights = 10 - pointLights.length
    buf.position(buf.position + blankPointLights * Light.Point.alignedSize)
    
    
    dirLights.foreach: dl =>
      insertVec3(dl.color)
      buf.putFloat(0f)
      insertVec3(dl.dir)
      buf.putFloat(0f)
    val blankDirLights = 10 - pointLights.length
    buf.position(buf.position + blankDirLights * Light.Directional.alignedSize)

    spotLights.foreach: sl =>
      insertVec3(sl.color)
      buf.putFloat(0f)

      insertVec3(sl.pos)
      buf.putFloat(0f)
      
      insertVec3(sl.dir)
      buf.putFloat(sl.linear)
      
      buf.putFloat(sl.quadratic)
      buf.putFloat(sl.innerCutoff)
      buf.putFloat(sl.outerCutoff)
      buf.putFloat(0f)
    val blankSpotLights = 10 - pointLights.length
    buf.position(buf.position + blankSpotLights * Light.Spot.alignedSize)

    val bytesWritten = buf.position
    buf.position(0)

    val UBO = glCreateBuffers()
    glNamedBufferStorage(UBO, bytesWritten, GL_DYNAMIC_STORAGE_BIT)
    // we use the native so we can use a bigger buf as src,
    nglNamedBufferSubData(UBO, /*offset*/ 0, bytesWritten, MemoryUtil.memAddress(buf))
    glBindBufferBase(GL_UNIFORM_BUFFER, /*index*/0, UBO)
