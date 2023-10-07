package dev.miikat.scalatest

import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

import java.time.{Duration, Instant}

class Engine extends AutoCloseable:
  private val (win, imGuiPlatform, imGuiRenderer) = initWindow()
  private val game = Game()

  def run(): Unit =
    var lastInstant = Instant.now()

    while !glfwWindowShouldClose(win) do
      val now = Instant.now()
      val delta = Duration.between(lastInstant, now).toMillis / 1000.0
      glfwPollEvents()
      game.updateState(delta)

      // draw game
      // draw gui
      game.draw()
      this.drawGui()


      glfwSwapBuffers(win)
      lastInstant = now

  private def initWindow() =
    println("Hello LWJGL " + org.lwjgl.Version.getVersion + "!")
    GLFWErrorCallback.createPrint(System.err).set()
    glfwInit()
    val win = glfwCreateWindow(300, 300, "Hi", NULL, NULL)
    glfwMakeContextCurrent(win)
    glfwShowWindow(win)

    GL.createCapabilities()
    println(s"GL Version: ${glGetString(GL_VERSION)}")

    val imGuiRenderer = ImGuiImplGl3()
    val imGuiPlatform = ImGuiImplGlfw()
    ImGui.createContext()
    val io = ImGui.getIO
    io.setIniFilename(null)
    imGuiPlatform.init(win, true)
    imGuiRenderer.init()

    (win, imGuiPlatform, imGuiRenderer)

  private def drawGui(): Unit =
    imGuiPlatform.newFrame()
    ImGui.newFrame()
    ImGui.begin("Cool Window")
    ImGui.end()
    ImGui.render()
    imGuiRenderer.renderDrawData(ImGui.getDrawData)

  def close(): Unit =
    glfwTerminate()

