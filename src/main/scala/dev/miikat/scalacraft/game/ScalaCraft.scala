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
    val cubeMesh = Mesh(Cube.vertices, Cube.indices)
    val cubeTex = Texture("/grass_diffuse.png")
    cubeEnt = Entity(cubeTex, cubeMesh)

  override def updateState(glfwWindow: Long, camera: Camera, delta: Double, mouseDelta: Vector2f) = 
    cameraControls.processInput(glfwWindow, camera, delta, mouseDelta)
    

  override def scene: Scene =
    val entities = Array(cubeEnt)
    val light = Light.Point(Vector3f(1,1,1), Vector3f(4,4,4), 1f, 1f, 1f)
    val lights = Array[Light](light)
    Scene(entities, lights)


@main
def main() =
  println(config.getString("database.host"))
  System.setProperty("joml.format", "false")
  val game = ScalaCraft()

  // remember to use Using.resource() and not Using(), since using wraps the exception in a Try()
  Using.resource(Engine(game)): engine =>
    engine.run()
