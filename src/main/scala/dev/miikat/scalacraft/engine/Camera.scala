package dev.miikat.scalacraft.engine

import org.joml.*

val worldUp = Vector3f(0.0f, 1.0f, 0.0f)

class Camera():

  private var _pos = Vector3f()
  private var _pitch = 0f
  private var _yaw = 0f
  private var (_forward, _right, _viewMatrix) = createViewMatrix()

  def pos = _pos
  def pitch = _pitch
  def yaw = _yaw
  def viewMatrix = _viewMatrix
  def right = _right
  def forward = _forward

  def pos_=(arg: Vector3f) =
    _pos = arg
    updateViewMatrix()
  def pitch_=(arg: Float) =
    _pitch = arg
    updateViewMatrix()
  def yaw_=(arg: Float) =
    _yaw = arg
    updateViewMatrix()

  private def updateViewMatrix() =
    val (_forward, _right, _viewMatrix) = createViewMatrix()
    this._forward = _forward
    this._right = _right
    this._viewMatrix = _viewMatrix


  private def createViewMatrix() =
    val forward = Vector3f()
    forward.x = Math.cos(Math.toRadians(_yaw)) * Math.cos(Math.toRadians(_pitch))
    forward.y = Math.sin(Math.toRadians(_pitch))
    forward.z = Math.sin(Math.toRadians(_yaw)) * Math.cos(Math.toRadians(_pitch))
    forward.normalize()
    val right = Vector3f(forward).cross(worldUp).normalize()
    val up = Vector3f(right).cross(forward).normalize()
    val vm = Matrix4f().lookAt(_pos, Vector3f(_pos).add(forward), up)
    (forward, right, vm)

