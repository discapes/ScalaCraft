package dev.miikat.scalacraft.engine

import org.joml.*

val worldUp = Vector3f(0.0f, 1.0f, 0.0f)

class Camera(winDim: (Int, Int)):
  val FOV = Math.toRadians(60.0f);
  val projMatrix = Matrix4f().setPerspective(FOV, winDim._1.toFloat / winDim._2.toFloat, 0.01f, 1000.0f);
  var pos = Vector3f()
  var pitch = 0f
  var yaw = 0f

  def forward =
    val forward = Vector3f()
    forward.x = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
    forward.y = Math.sin(Math.toRadians(pitch));
    forward.z = Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
    forward.normalize()
    forward
  def right = Vector3f(forward).cross(worldUp).normalize()
  def up = Vector3f(right).cross(forward).normalize()
  def viewMatrix =  Matrix4f().lookAt(pos, Vector3f(pos).add(forward), up)


