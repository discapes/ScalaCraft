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

import org.lwjgl.system.MemoryStack

import scala.util.Using
import org.lwjgl.assimp.AIFileIO
import org.lwjgl.assimp.AIFile
import org.lwjgl.system.MemoryUtil
import org.lwjgl.assimp.Assimp
import org.lwjgl.assimp.Assimp.*
import org.lwjgl.assimp.*
import scala.collection.mutable.ArrayBuffer
import org.joml.Vector3f
import org.lwjgl.system.MemoryUtil
import dev.miikat.scalacraft.engine.internal.Sphere
import dev.miikat.scalacraft.engine.internal.Cube
import dev.miikat.scalacraft.engine.internal.ModelLoader

val SIZEOF_FLOAT = 4
val SIZEOF_INT = 4

class Mesh private (VAO: Int, indicesCount: Int, indicesOffset: Int):

  def draw() =
    glBindVertexArray(VAO)
    glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, indicesOffset)

object Mesh:
  
  val cube = Mesh(Cube.vertices, Cube.indices)

  val sphere = Sphere.create(30, 30, 1)

  def fromFile(path: String) = ModelLoader.fromFile(path)

  def apply(data: Array[Float], indices: Array[Int]) =
    val posAttrIndex = 0
    val normAttrIndex = 1
    val uvAttrIndex = 2
    val mainBufferIndex = 0
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
    glEnableVertexArrayAttrib(VAO, normAttrIndex)
    glEnableVertexArrayAttrib(VAO, uvAttrIndex)
    glVertexArrayAttribFormat(VAO, posAttrIndex, 3, GL_FLOAT, false, /*offset*/ 0)
    glVertexArrayAttribFormat(VAO, normAttrIndex, 3, GL_FLOAT, false, /*offset*/ 3 * SIZEOF_FLOAT)
    glVertexArrayAttribFormat(VAO, uvAttrIndex, 2, GL_FLOAT, false, /*offset*/ (3 + 3) * SIZEOF_FLOAT)
    glVertexArrayAttribBinding(VAO, posAttrIndex, mainBufferIndex)
    glVertexArrayAttribBinding(VAO, normAttrIndex, mainBufferIndex)
    glVertexArrayAttribBinding(VAO, uvAttrIndex, mainBufferIndex)
    glVertexArrayVertexBuffer(VAO, mainBufferIndex, VBO, /*offset*/ 0, /*stride*/ (3 + 2 + 3) * SIZEOF_FLOAT)
    glVertexArrayElementBuffer(VAO, VBO)

    new Mesh(VAO, indicesCount, indicesOffset)