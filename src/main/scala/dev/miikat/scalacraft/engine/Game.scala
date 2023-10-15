package dev.miikat.scalacraft.engine

import org.joml.*

trait Game:
  def updateState(delta: Long): Unit
  def processInput(delta: Long, mouseDelta: Vector2f): Unit
  def scene: Scene
  def drawGui(): Unit
