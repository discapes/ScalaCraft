package dev.miikat.scalatest.engine

trait Game:
  def update(glfwWindow: Long, camera: Camera, delta: Double): Vector[Entity]
  def init(): Unit
