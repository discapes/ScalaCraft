package dev.miikat.scalacraft.game

import dev.miikat.scalacraft.engine.Camera
import org.lwjgl.glfw.GLFW.*
import scala.util.Using
import org.lwjgl.system.MemoryStack
import org.joml.*

import math.Fractional.Implicits.infixFractionalOps
import math.Integral.Implicits.infixIntegralOps
import math.Numeric.Implicits.infixNumericOps
import dev.miikat.scalacraft.engine.Window

class CameraControls(win: Window, camera: Camera):
  var prevCursorPos: Vector2f = null

  def processInput(delta: Long, mouseDelta: Vector2f): Unit =
    processMouse(mouseDelta)
    processKeyboard(delta)

  def processMouse(mouseDelta: Vector2f) =
    // glfw screen space has top left as origin, so we subtract y instead of adding
    camera.yaw += mouseDelta.x
    camera.pitch = Math.clamp(-89.9f, 89.9f, camera.pitch - mouseDelta.y)
     
  def processKeyboard(delta: Long) =
    val forward = camera.forward
    val right = camera.right
    val forwardFlat = Vector3f(forward.x, 0, forward.z).normalize()
    val rightFlat = Vector3f(right.x, 0, right.z).normalize()
    val moveDir = Vector3f()

    if win.isKeyDown(GLFW_KEY_SPACE) then
      moveDir.y += 1
    if win.isKeyDown(GLFW_KEY_LEFT_SHIFT) then
      moveDir.y -= 1

    if win.isKeyDown(GLFW_KEY_W) then
      moveDir.add(forwardFlat)
    if win.isKeyDown(GLFW_KEY_S) then
      moveDir.sub(forwardFlat)
      
    if win.isKeyDown(GLFW_KEY_D) then
      moveDir.add(rightFlat)
    if win.isKeyDown(GLFW_KEY_A) then
      moveDir.sub(rightFlat)

    // if the length is 0, normalize returns NaN
    if (moveDir.length > 0)
      camera.pos.add(moveDir.normalize().mul((math.pow(10.0, -9.0) * delta * 4).toFloat))

