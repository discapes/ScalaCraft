package dev.miikat.scalacraft.engine

import org.joml.*

trait Game:
  def updateState(glfwWindow: Long, camera: Camera, delta: Double, mouseDelta: Vector2f): Unit
  def scene: Scene
  def init(): Unit
