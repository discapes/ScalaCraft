package dev.miikat.scalacraft.engine

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.system.MemoryUtil.NULL
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
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GLUtil
import scala.util.Using
import org.lwjgl.system.MemoryStack
import org.joml.Vector2f
import org.joml.Matrix4f

class Window private (val handle: Long, var width: Int, var height: Int):
  var projMatrix = Window.createProjMatrix(width, height)
  var vSync = true
  val fpsCounter = FpsCounter()

  def setVSync(vSync: Boolean) =
    this.vSync = vSync
    println("setting vsync " + vSync)
    glfwSwapInterval(if vSync then 1 else 0)
  
  glfwSetFramebufferSizeCallback(handle, (win, w, h) => 
    glViewport(0, 0, w, h)
    this.width = w
    this.height = h
    this.projMatrix = Window.createProjMatrix(w, h)
  )

  def show(): Unit = glfwShowWindow(handle)
  def pollEvents() = glfwPollEvents()
  def cursorPos = 
    Using.resource(MemoryStack.stackPush()): stack =>
      val (x, y) = (stack.mallocDouble(1), stack.mallocDouble(1))
      glfwGetCursorPos(handle, x, y)
      Vector2f(x.get.toFloat, y.get.toFloat)
  def exit() = glfwTerminate()
  def swapBuffers() = 
    fpsCounter.count()
    glfwSwapBuffers(handle)
  def shouldClose = glfwWindowShouldClose(handle)
  def isKeyDown(key: Int) = glfwGetKey(handle, key) == GLFW_PRESS 


object Window:
  private def createProjMatrix(w: Int, h: Int) =
    val FOV = Math.toRadians(60.0f)
    Matrix4f().setPerspective(FOV.toFloat, w.toFloat / h.toFloat, 0.01f, 1000.0f)

  def apply(name: String): Window =
    val (handle, width, height) = initGlfw(name)
    initGL()
    new Window(handle, width, height)

  private def initGlfw(name: String) =
    GLFWErrorCallback.createPrint(System.err).set()
    glfwInit()
    println("glfw initialized")
    val monitor = glfwGetPrimaryMonitor()
    val vidMode = glfwGetVideoMode(monitor)
    windowHints()
    val handle = glfwCreateWindow(vidMode.width, vidMode.height, name, monitor, NULL)
    glfwMakeContextCurrent(handle)
    glfwSwapInterval(1)

    glfwSetInputMode(handle, GLFW_STICKY_KEYS, GLFW_TRUE)
    glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED)

    (handle, vidMode.width, vidMode.height)

  private def windowHints() = 
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)
    glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, 1)
    glfwWindowHint(GLFW_SAMPLES, 4)
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

  private def initGL() =
    GL.createCapabilities()
    GLUtil.setupDebugMessageCallback(System.err)
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_CULL_FACE)
    glEnable(GL_MULTISAMPLE)
    glClearColor(0.0, 0.0, 0.0, 1.0)
