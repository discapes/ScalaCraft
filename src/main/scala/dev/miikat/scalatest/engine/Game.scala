package dev.miikat.scalatest.engine

import org.joml.*

trait Game:
  def updateState(glfwWindow: Long, camera: Camera, delta: Double, mouseDelta: Vector2f): Unit
  def meshes: Vector[Entity]
  def init(): Unit
