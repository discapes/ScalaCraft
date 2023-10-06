import org.lwjgl.opengl.GL11._

import java.time.{Duration, Instant}

val G = -9.8

case class Game():
  var birdHeight = -0.5
  var birdVelocity = 0.0
  var lastInstant = Instant.now()

  def updateState =
    val now = Instant.now()
    val deltaTime = Duration.between(lastInstant, now)
    val deltaS = deltaTime.toMillis / 1000.0

    birdHeight += birdVelocity * deltaS + 1 / 2 * G * deltaS * deltaS
    birdVelocity += G * deltaS

  def draw =
    glColor3f(0.0, 1.0, 1.0)
    glBegin(GL_QUADS)
    glVertex2f(-0.5, 0.5)
    glVertex2f(0.5, 0.5)
    glVertex2f(0.5, -0.5)
    glVertex2f(-0.5, -0.5)
    glEnd()