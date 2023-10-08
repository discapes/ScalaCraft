package dev.miikat.scalatest.game

import dev.miikat.scalatest.engine.Camera
import org.lwjgl.glfw.GLFW.*
import scala.util.Using
import org.lwjgl.system.MemoryStack
import org.joml.*

import math.Fractional.Implicits.infixFractionalOps
import math.Integral.Implicits.infixIntegralOps
import math.Numeric.Implicits.infixNumericOps

class CameraControls:
  var prevCursorPos: Vector2f = null

  def processInput(win: Long, camera: Camera, delta: Double): Unit =
    processMouse(win, camera)
    processKeyboard(win, camera, delta)

  def processMouse(win: Long, camera: Camera) =
    val cursorPos = getCursorPos(win)
    if prevCursorPos == null then prevCursorPos = cursorPos
    val move = Vector2f(cursorPos).sub(prevCursorPos).mul(0.01f)
    prevCursorPos = cursorPos

    camera.rotation.add(move)

  def processKeyboard(win: Long, camera: Camera, delta: Double) =
    val mult = 4f
    if glfwGetKey(win, GLFW_KEY_SPACE) == GLFW_PRESS then
      camera.pos.y += mult * delta.toFloat
    if glfwGetKey(win, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS then
      camera.pos.y -= mult * delta.toFloat
    if glfwGetKey(win, GLFW_KEY_W) == GLFW_PRESS then
      camera.pos.x += mult * delta.toFloat
    if glfwGetKey(win, GLFW_KEY_S) == GLFW_PRESS then
      camera.pos.x -= mult * delta.toFloat
    if glfwGetKey(win, GLFW_KEY_A) == GLFW_PRESS then
      camera.pos.z += mult * delta.toFloat
    if glfwGetKey(win, GLFW_KEY_D) == GLFW_PRESS then
      camera.pos.z -= mult * delta.toFloat


  def getCursorPos(win: Long) =
    Using.resource(MemoryStack.stackPush()): stack =>
      val (x, y) = (stack.mallocDouble(1), stack.mallocDouble(1))
      glfwGetCursorPos(win, x, y)
      Vector2f(x.get.toFloat, y.get.toFloat)