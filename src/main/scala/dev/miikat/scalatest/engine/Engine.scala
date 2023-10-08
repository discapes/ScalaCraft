package dev.miikat.scalatest.engine

import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
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
import org.lwjgl.system.MemoryUtil.NULL

import java.time.{Duration, Instant}
import scala.io.Source
import scala.util.Using
import org.lwjgl.system.MemoryStack

import org.joml.*
import org.lwjgl.opengl.GLUtil

class Engine(game: Game) extends AutoCloseable:
  private val win = initWindow()
  private val (imGuiPlatform, imGuiRenderer) = initImGui()
  private val shaderProgramId = initShaders()
  private val camera = Camera()
  game.init()

  def run(): Unit =
    var lastInstant = Instant.now()

    while !glfwWindowShouldClose(win) do
      val now = Instant.now()
      val delta = Duration.between(lastInstant, now).toMillis / 1000.0
      glfwPollEvents()

      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      val objects = game.update(win, camera, delta)

      glUseProgram(shaderProgramId)
      objects.foreach(drawEntity)

      this.drawGui()

      glfwSwapBuffers(win)
      lastInstant = now

  private def initWindow() =
    println("Hello LWJGL " + org.lwjgl.Version.getVersion + "!")
    GLFWErrorCallback.createPrint(System.err).set()
    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)
    val win = glfwCreateWindow(800, 800, "Hi", NULL, NULL)
    glfwMakeContextCurrent(win)
    glfwShowWindow(win)

    GL.createCapabilities()
    GLUtil.setupDebugMessageCallback(System.err)
    glEnable(GL_DEPTH_TEST)


    println(s"GL Version: ${glGetString(GL_VERSION)}")
    glClearColor(1.0, 0.0, 0.0, 0.0)
    win

  private def initImGui() =
    val imGuiRenderer = ImGuiImplGl3()
    val imGuiPlatform = ImGuiImplGlfw()
    ImGui.createContext()
    val io = ImGui.getIO
    io.setIniFilename(null)
    imGuiPlatform.init(win, true)
    imGuiRenderer.init()
    (imGuiPlatform, imGuiRenderer)

  private def drawGui(): Unit =
    imGuiPlatform.newFrame()
    ImGui.newFrame()
    ImGui.begin("Cool Window")
    ImGui.end()
    ImGui.render()
    imGuiRenderer.renderDrawData(ImGui.getDrawData)

  def close(): Unit =
    glfwTerminate()

  private def initShaders() =
    val programId = glCreateProgram()
    val vertShaderId = glCreateShader(GL_VERTEX_SHADER)
    val fragShaderId = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(vertShaderId, Source.fromResource("shader.vert").mkString)
    glShaderSource(fragShaderId, Source.fromResource("shader.frag").mkString)
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

  private def drawEntity(ent: Entity) =
    setShaderMatrix(Matrix4f(camera.projMatrix).mul(camera.viewMatrix).mul(ent.modelMatrix))
    ent.texture.bind()
    ent.mesh.draw()

  private def setShaderMatrix(mat: Matrix4f) =
    Using.resource(MemoryStack.stackPush()): stack =>
      val uniLoc = glGetUniformLocation(shaderProgramId, "MVP")
      glUniformMatrix4fv(uniLoc, false, mat.get(stack.mallocFloat(16)))
