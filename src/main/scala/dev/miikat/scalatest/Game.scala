package dev.miikat.scalatest

import org.lwjgl.opengl.GL11.*

class Game():
  private val G = -9.8
  private var birdHeight = -0.5
  private var birdVelocity = 0.0

  def updateState(delta: Double): Unit =
    birdHeight += birdVelocity * delta + 1 / 2 * G * delta * delta
    birdVelocity += G * delta

  def draw(): Unit =
    glClearColor(1.0, 0.0, 0.0, 0.0)
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glColor3f(0.0, 1.0, 1.0)
    glBegin(GL_QUADS)
    glVertex2f(-0.5, 0.5)
    glVertex2f(0.5, 0.5)
    glVertex2f(0.5, -0.5)
    glVertex2f(-0.5, -0.5)
    glEnd()