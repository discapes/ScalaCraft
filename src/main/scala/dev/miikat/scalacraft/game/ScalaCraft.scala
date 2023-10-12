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
  var entities = ArrayBuffer[Entity]()
  var lights = ArrayBuffer[Light]()
  val cameraControls = CameraControls()

  def init(engine: Engine) =
    engine.camera.yaw = -69f
    engine.camera.pitch = -26f
    engine.camera.pos.set(1.3, 4.7, 11.7)

    val grassDiffTex = Texture("/grass_diffuse.png")
    val grassSpecTex = Texture("/grass_specular.png")
    val earthDiffTex = Texture("/earth2048.bmp")
    val moonDiffTex = Texture("/moon1024.bmp")
    val suzanneDiffTex = Texture("/suzanne.png")
    val ratDiffTex = Texture("/rat_diff.jpg")
    val ratNormTex = Texture("/rat_norm.jpg")
    
    val sphereMesh = Sphere.create(32, 32, 1)

    val grassBlock = Entity(grassDiffTex, grassSpecTex, Cube.mesh)
    grassBlock.pos.set(5, 1, 5)

    val earth = Entity(earthDiffTex, moonDiffTex, sphereMesh)
    earth.pos.set(3, 3, 3)

    val blueLight: Light.Point = Light.Point(Vector3f(0,0.4,1), Vector3f(0,3,2), 0.09f, 0.032f)
    val redLight: Light.Point = Light.Point(Vector3f(1,0.2,0.2), Vector3f(7,5,5), 0.09f, 0.032f)
    val brightLight: Light.Point = Light.Point(Vector3f(1,1,0.9), Vector3f(2,2,7), 0.045f, 0.0075f)
    val blueSource = Entity(moonDiffTex, moonDiffTex, sphereMesh, Some(blueLight.color))
    val redSource = Entity(moonDiffTex, moonDiffTex, sphereMesh, Some(redLight.color))
    val brightSource = Entity(moonDiffTex, moonDiffTex, sphereMesh, Some(brightLight.color))
    blueSource.pos.set(blueLight.pos)
    redSource.pos.set(redLight.pos)
    brightSource.pos.set(brightLight.pos)
    blueSource.scale = 0.4f
    redSource.scale = 0.4f
    brightSource.scale = 0.3f

    val groundBlocks = 
      for 
        i <- 0 to 9
        j <- 0 to 9
      yield
        val ent = Entity(grassDiffTex, grassSpecTex, Cube.mesh)
        ent.pos.set(i, 0, j)
        ent
      
    val suzanneMesh = Model.load("/suzanne.obj")(0)
    val suzanne = Entity(suzanneDiffTex, suzanneDiffTex, suzanneMesh, Some(Vector3f(0.2,0.1,0.2)))
    suzanne.pos.set(1, 2, 5)
    // val sunlight = Light.Directional(Vector3f(1.0, 1.0, 0.9), Vector3f(-1,-0.5,-0.2))

    val ratMesh = Model.load("/rat.gltf")(0)
    val rat = Entity(ratDiffTex, ratNormTex, ratMesh)
    rat.pos.set(3, 0.50, 6)
    rat.scale = 10f

    entities.append(grassBlock, earth, redSource, blueSource, suzanne, brightSource, rat)
    entities.addAll(groundBlocks)
    lights.append(redLight, blueLight, brightLight)

  override def updateState(glfwWindow: Long, camera: Camera, delta: Long, mouseDelta: Vector2f) = 
    cameraControls.processInput(glfwWindow, camera, delta, mouseDelta)
    

  override def scene: Scene =
    Scene(entities.toArray, lights.toArray)


@main
def main() =
  println(config.getString("database.host"))
  System.setProperty("joml.format", "false")
  val game = ScalaCraft()

  // remember to use Using.resource() and not Using(), since using wraps the exception in a Try()
  Using.resource(Engine(game)): engine =>
    engine.run()
