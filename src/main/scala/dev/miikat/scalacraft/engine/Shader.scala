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
import scala.io.Source
import org.joml.*
import scala.util.Using
import org.lwjgl.system.MemoryStack


class Shader(vertFilename: String, fragFilename: String):
  val programId = init()

  private def init() = 
    val programId = glCreateProgram()
    val vertShaderId = glCreateShader(GL_VERTEX_SHADER)
    val fragShaderId = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(vertShaderId, Source.fromResource(vertFilename).mkString)
    glShaderSource(fragShaderId, Source.fromResource(fragFilename ).mkString)
    glCompileShader(vertShaderId)
    glCompileShader(fragShaderId)
    glAttachShader(programId, vertShaderId)
    glAttachShader(programId, fragShaderId)

    glLinkProgram(programId)

    glDetachShader(programId, vertShaderId)
    glDetachShader(programId, fragShaderId)
    glDeleteShader(vertShaderId)
    glDeleteShader(fragShaderId)

    programId

  def use() = glUseProgram(programId)

  def setVector3f(name: String, vec: Vector3f) =
    val loc = glGetUniformLocation(programId, name)
    glProgramUniform3f(programId, loc, vec.x, vec.y, vec.z)

  def setMatrix4f(name: String, matrix: Matrix4f) =
    Using.resource(MemoryStack.stackPush()): stack =>
      val loc = glGetUniformLocation(programId, name)
      glProgramUniformMatrix4fv(programId, loc, false, matrix.get(stack.mallocFloat(16)))