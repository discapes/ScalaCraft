package dev.miikat.scalatest.engine

trait Game:
  def update(glfwWindow: Long, delta: Double): Vector[Entity]
  def init(): Unit
