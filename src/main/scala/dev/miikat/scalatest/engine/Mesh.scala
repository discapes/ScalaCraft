package dev.miikat.scalatest.engine
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

import org.lwjgl.system.MemoryStack

import scala.util.Using

val SIZEOF_FLOAT = 4
val SIZEOF_INT = 4

class Mesh(data: Array[Float], indices: Array[Int]):
  private val posAttrIndex = 0
  private val uvAttrIndex = 1
  private val mainBufferIndex = 0
  val VAO = glCreateVertexArrays()
  val VBO = glCreateBuffers()
  val indicesCount = indices.length
  val indicesOffset = data.length * SIZEOF_FLOAT

  // fill the buffer
  glNamedBufferStorage(VBO, data.length * SIZEOF_FLOAT + indices.length * SIZEOF_INT, GL_DYNAMIC_STORAGE_BIT)
  glNamedBufferSubData(VBO, /*offset*/ 0, data)
  glNamedBufferSubData(VBO, indicesOffset, indices)

  // initialize the VAO
  glEnableVertexArrayAttrib(VAO, posAttrIndex)
  glEnableVertexArrayAttrib(VAO, uvAttrIndex)
  glVertexArrayAttribFormat(VAO, posAttrIndex, 3, GL_FLOAT, false, /*offset*/ 0);
  glVertexArrayAttribFormat(VAO, uvAttrIndex, 2, GL_FLOAT, false, /*offset*/ 3 * SIZEOF_FLOAT);
  glVertexArrayAttribBinding(VAO, posAttrIndex, mainBufferIndex)
  glVertexArrayAttribBinding(VAO, uvAttrIndex, mainBufferIndex)
  glVertexArrayVertexBuffer(VAO, mainBufferIndex, VBO, /*offset*/ 0, /*stride*/ 3 * SIZEOF_FLOAT + 2 * SIZEOF_FLOAT);
  glVertexArrayElementBuffer(VAO, VBO)

  def draw() =
    glBindVertexArray(VAO)
    glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, indicesOffset);
