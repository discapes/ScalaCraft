package dev.miikat.scalacraft.engine

import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
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
import org.lwjgl.glfw.GLFW.*

import java.time.{Duration, Instant}
import scala.io.Source
import scala.util.Using
import org.lwjgl.system.MemoryStack

import org.joml.*
import org.lwjgl.opengl.GLUtil
import imgui.flag.ImGuiConfigFlags
import scala.collection.mutable.ArrayBuffer
import java.nio.ByteBuffer
import org.lwjgl.system.MemoryUtil
import scala.math.pow
import imgui.`type`.ImBoolean

class Engine(gameFn: (window: Window) => Game, name: String) extends AutoCloseable:
  val window = Window(name)
  val gui = GUI(window.handle)
  val renderer = Renderer(window)
  val game = gameFn(window)
  
  var paused = false
  val vSync = ImBoolean(true)
  initCallbacks()

  def run(): Unit =
    window.show()
    window.pollEvents()

    var lastInstant = Instant.now()
    var lastFpsUpdate = Instant.now()
    var lastCursorPos = window.cursorPos

    while !window.shouldClose do
      window.pollEvents()
      val now = Instant.now()
      val cursorPos = window.cursorPos
      val delta = Duration.between(lastInstant, now).toNanos
      val cursorDelta = Vector2f(cursorPos).sub(lastCursorPos)
      
      if !paused then
        game.processInput(delta, cursorDelta)
        game.updateState(delta)

      renderer.render(game.scene)
      gui.beginFrame()
      game.drawGui()
      gui.endFrame()
      window.swapBuffers()

      lastInstant = now
      lastCursorPos = cursorPos

  private def initCallbacks() =
    glfwSetKeyCallback(window.handle, (_, key, _, action, _) => 
      if action == GLFW_PRESS then
        key match
          case GLFW_KEY_ESCAPE =>  glfwSetWindowShouldClose(window.handle, true)
          case GLFW_KEY_P =>
            paused = !paused
            gui.setCaptureMouse(paused)
            glfwSetInputMode(window.handle, GLFW_CURSOR, if paused then GLFW_CURSOR_NORMAL else GLFW_CURSOR_DISABLED)
          case _ => ()
    )

  def close(): Unit = window.exit()