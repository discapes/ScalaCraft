package dev.miikat.scalatest.engine

import org.joml.*

class Camera:
  val FOV = Math.toRadians(60.0f);
  val projMatrix = Matrix4f().setPerspective(FOV, 1.0f, 0.01f, 1000.0f);
  def viewMatrix = Matrix4f().rotateX(rotation.x).rotateY(rotation.y).translate(Vector3f(pos).negate())

  var pos = Vector3f(0f, 0f, 2f)
  var rotation = Vector2f()
