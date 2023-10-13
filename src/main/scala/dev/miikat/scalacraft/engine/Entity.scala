package dev.miikat.scalacraft.engine

import org.joml.*

class Entity(val texture: Texture, val spec: Texture, val mesh: Mesh, val ambientLight: Option[Vector3f] = None):

  var pos = Vector3f()
  var rotation = Quaternionf()
  var scale = 1.0f

  def modelMatrix = Matrix4f().translationRotateScale(pos, rotation, scale)
