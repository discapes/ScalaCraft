package dev.miikat.scalacraft.game

import org.lwjgl.opengl.GL11C.*
import org.apache.commons.configuration2.builder.fluent.Configurations
import org.lwjgl.glfw.GLFW.*

import scala.util.Using
import dev.miikat.scalacraft.engine.*
import org.joml.*

val configs = Configurations()
// remember the slash
val config = configs.properties(this.getClass.getResource("/config.properties"))

class ScalaCraft extends Game:
  var cubeEnt: Entity = null
  val cameraControls = CameraControls()

  def init() =
    val cubeMesh = Mesh(Cube.data, Cube.indices)
    val cubeTex = Texture("/cube.png")
    cubeEnt = Entity(cubeTex, cubeMesh)

  override def updateState(glfwWindow: Long, camera: Camera, delta: Double, mouseDelta: Vector2f) = 
    cameraControls.processInput(glfwWindow, camera, delta, mouseDelta)
    

  override def scene: Scene = Scene(Array(cubeEnt), Array())

@main
def main() =
  println(config.getString("database.host"))
  System.setProperty("joml.format", "false")
  val game = ScalaCraft()

  // remember to use Using.resource() and not Using(), since using wraps the exception in a Try()
  Using.resource(Engine(game)): engine =>
    engine.run()
