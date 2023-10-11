package dev.miikat.scalacraft.game

import org.lwjgl.opengl.GL11C.*
import org.apache.commons.configuration2.builder.fluent.Configurations
import org.lwjgl.glfw.GLFW.*

import scala.util.Using
import dev.miikat.scalacraft.engine.*
import org.joml.*
import scala.collection.mutable.ArrayBuffer

val configs = Configurations()
// remember the slash
val config = configs.properties(this.getClass.getResource("/config.properties"))

class ScalaCraft extends Game:
  var entities: ArrayBuffer[Entity] = null
  val cameraControls = CameraControls()

  def init() =
    val cubeMesh = Mesh(Cube.vertices, Cube.indices)
    val cubeTex = Texture("/grass_diffuse.png")
    val cubeTexSpec = Texture("/grass_specular.png")
    val cubeEnt = Entity(cubeTex, cubeTexSpec, cubeMesh)
    cubeEnt.pos.set(5, 1, 5)
    val otherCubes = 
      for 
        i <- 0 to 9
        j <- 0 to 9
      yield
        val ent = Entity(cubeTex, cubeTexSpec, cubeMesh)
        ent.pos.set(i, 0, j)
        ent
    entities = ArrayBuffer.from(otherCubes)
    entities.append(cubeEnt)

  override def updateState(glfwWindow: Long, camera: Camera, delta: Double, mouseDelta: Vector2f) = 
    cameraControls.processInput(glfwWindow, camera, delta, mouseDelta)
    

  override def scene: Scene =
    val light = Light.Point(Vector3f(0,0.4,1), Vector3f(0,3,2), 1f, 1f, 1f)
    val light3 = Light.Point(Vector3f(1,0.2,0.2), Vector3f(7,5,5), 1f, 1f, 1f)
    val light2 = Light.Directional(Vector3f(1,0.7,0), Vector3f(4,3,2))
    val lights = Array[Light](light2, light, light3)
    Scene(entities.toArray, lights)


@main
def main() =
  println(config.getString("database.host"))
  System.setProperty("joml.format", "false")
  val game = ScalaCraft()

  // remember to use Using.resource() and not Using(), since using wraps the exception in a Try()
  Using.resource(Engine(game)): engine =>
    engine.run()
