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
import org.joml.Matrix4f
import org.joml.Matrix3f

val skyboxVertices = Array(
  // positions          
  -1.0f,  1.0f, -1.0f,
  -1.0f, -1.0f, -1.0f,
  1.0f, -1.0f, -1.0f,
  1.0f, -1.0f, -1.0f,
  1.0f,  1.0f, -1.0f,
  -1.0f,  1.0f, -1.0f,

  -1.0f, -1.0f,  1.0f,
  -1.0f, -1.0f, -1.0f,
  -1.0f,  1.0f, -1.0f,
  -1.0f,  1.0f, -1.0f,
  -1.0f,  1.0f,  1.0f,
  -1.0f, -1.0f,  1.0f,

  1.0f, -1.0f, -1.0f,
  1.0f, -1.0f,  1.0f,
  1.0f,  1.0f,  1.0f,
  1.0f,  1.0f,  1.0f,
  1.0f,  1.0f, -1.0f,
  1.0f, -1.0f, -1.0f,

  -1.0f, -1.0f,  1.0f,
  -1.0f,  1.0f,  1.0f,
  1.0f,  1.0f,  1.0f,
  1.0f,  1.0f,  1.0f,
  1.0f, -1.0f,  1.0f,
  -1.0f, -1.0f,  1.0f,

  -1.0f,  1.0f, -1.0f,
  1.0f,  1.0f, -1.0f,
  1.0f,  1.0f,  1.0f,
  1.0f,  1.0f,  1.0f,
  -1.0f,  1.0f,  1.0f,
  -1.0f,  1.0f, -1.0f,

  -1.0f, -1.0f, -1.0f,
  -1.0f, -1.0f,  1.0f,
  1.0f, -1.0f, -1.0f,
  1.0f, -1.0f, -1.0f,
  -1.0f, -1.0f,  1.0f,
  1.0f, -1.0f,  1.0f
)

class Skybox:
  private val shader = Shader("shader_skybox.vert", "shader_skybox.frag")
  private val filePaths = List("skybox/right.jpg", "skybox/left.jpg",
    "skybox/top.jpg", "skybox/bottom.jpg", "skybox/front.jpg", "skybox/back.jpg")
  private val cubemap = Cubemap(filePaths)
  val VAO = glCreateVertexArrays()
  val VBO = glCreateBuffers()

  glNamedBufferStorage(VBO, skyboxVertices.length * SIZEOF_FLOAT, GL_DYNAMIC_STORAGE_BIT)
  glNamedBufferSubData(VBO, /*offset*/ 0, skyboxVertices)
  glEnableVertexArrayAttrib(VAO, 0)
  glVertexArrayAttribFormat(VAO, 0, 3, GL_FLOAT, false, /*offset*/ 0);
  glVertexArrayAttribBinding(VAO, 0, 0)
  glVertexArrayVertexBuffer(VAO, 0, VBO, /*offset*/ 0, /*stride*/ 3 * SIZEOF_FLOAT);

  def draw(viewMatrix: Matrix4f, projMatrix: Matrix4f) = 
    glDepthFunc(GL_LEQUAL)
    shader.use()
    val view2 = Matrix4f(Matrix3f(viewMatrix))
    shader.setMatrix4f("view", view2);
    shader.setMatrix4f("projection", projMatrix);
    cubemap.bind(0)
    glBindVertexArray(VAO)
    glDrawArrays(GL_TRIANGLES, 0, 36);
    glDepthFunc(GL_LESS)