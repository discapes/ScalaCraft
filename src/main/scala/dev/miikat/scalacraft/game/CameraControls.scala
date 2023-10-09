package dev.miikat.scalacraft.game

import dev.miikat.scalacraft.engine.Camera
import org.lwjgl.glfw.GLFW.*
import scala.util.Using
import org.lwjgl.system.MemoryStack
import org.joml.*

import math.Fractional.Implicits.infixFractionalOps
import math.Integral.Implicits.infixIntegralOps
import math.Numeric.Implicits.infixNumericOps

class CameraControls:
  var prevCursorPos: Vector2f = null

  def processInput(win: Long, camera: Camera, delta: Double, mouseDelta: Vector2f): Unit =
    processMouse(camera, mouseDelta)
    processKeyboard(win, camera, delta)

  def processMouse(camera: Camera, mouseDelta: Vector2f) =
    // glfw screen space has top left as origin, so we subtract y instead of adding
    camera.yaw += mouseDelta.x
    camera.pitch = Math.clamp(-89.9f, 89.9f, camera.pitch - mouseDelta.y)
     
  def processKeyboard(win: Long, camera: Camera, delta: Double) =
    val mult = 4f
    val forward = camera.forward
    val right = camera.right
    val forwardFlat = Vector3f(forward.x, 0, forward.z).normalize()
    val rightFlat = Vector3f(right.x, 0, right.z).normalize()
    val moveDir = Vector3f()

    if glfwGetKey(win, GLFW_KEY_SPACE) == GLFW_PRESS then
      moveDir.y += 1
    if glfwGetKey(win, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS then
      moveDir.y -= 1

    if glfwGetKey(win, GLFW_KEY_W) == GLFW_PRESS then
      moveDir.add(forwardFlat)
    if glfwGetKey(win, GLFW_KEY_S) == GLFW_PRESS then
      moveDir.sub(forwardFlat)
      
    if glfwGetKey(win, GLFW_KEY_D) == GLFW_PRESS then
      moveDir.add(rightFlat)
    if glfwGetKey(win, GLFW_KEY_A) == GLFW_PRESS then
      moveDir.sub(rightFlat)

    // if the length is 0, normalize returns NaN
    if (moveDir.length > 0)
      camera.pos.add(moveDir.normalize().mul(mult * delta.toFloat))

