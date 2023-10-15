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
import org.lwjgl.system.MemoryUtil
import org.joml.Vector3f

class Lighting:
  private val UBO = glCreateBuffers()
  private val N_LIGHTS = 10
  private val UBO_SIZE = 4 * 4 + N_LIGHTS * (Light.Point.alignedSize + Light.Directional.alignedSize + Light.Spot.alignedSize)
  glNamedBufferStorage(UBO, UBO_SIZE, GL_DYNAMIC_STORAGE_BIT)

  def updateLights(lights: Iterable[Light]): Unit =
    val pointLights = lights.filter(_.isInstanceOf[Light.Point]).map(_.asInstanceOf[Light.Point])
    val dirLights = lights.filter(_.isInstanceOf[Light.Directional]).map(_.asInstanceOf[Light.Directional])
    val spotLights = lights.filter(_.isInstanceOf[Light.Spot]).map(_.asInstanceOf[Light.Spot])
    if pointLights.size > N_LIGHTS then throw Exception("Too many point lights!")
    if dirLights.size > N_LIGHTS then throw Exception("Too many dir lights!")
    if spotLights.size > N_LIGHTS then throw Exception("Too many spot lights!")

    val buf = MemoryUtil.memCalloc(UBO_SIZE)
    def insertVec3(v: Vector3f) =
      v.get(buf)
      buf.position(buf.position + 4 * 3)
    
    buf.putInt(pointLights.size)
    buf.putInt(dirLights.size)
    buf.putInt(spotLights.size)
    buf.putFloat(0f)

    pointLights.foreach: pl =>
      insertVec3(pl.color)
      buf.putFloat(pl.linear)
      insertVec3(pl.pos)
      buf.putFloat(pl.quadratic)
    val blankPointLights = N_LIGHTS - pointLights.size
    buf.position(buf.position + blankPointLights * Light.Point.alignedSize)
    
    
    dirLights.foreach: dl =>
      insertVec3(dl.color)
      buf.putFloat(0f)
      insertVec3(dl.dir)
      buf.putFloat(0f)
    val blankDirLights = N_LIGHTS - dirLights.size
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

    buf.position(0)
    glNamedBufferSubData(UBO, /*offset*/ 0, buf)
    
  def bind(index: Int) = glBindBufferBase(GL_UNIFORM_BUFFER, index, UBO)