package dev.miikat.scalacraft.game

import org.lwjgl.opengl.GL11C.*
import org.apache.commons.configuration2.builder.fluent.Configurations
import org.lwjgl.glfw.GLFW.*

import scala.util.Using
import dev.miikat.scalacraft.engine.*
import org.joml.*
import scala.collection.mutable.ArrayBuffer
import dev.miikat.scalacraft.engine.Texture
import imgui.ImGui
import imgui.`type`.ImBoolean

val configs = Configurations()
// remember the slash
val config = configs.properties(this.getClass.getResource("/config.properties"))

class ScalaCraft(window: Window) extends Game:
  var entities = ArrayBuffer[Entity]()
  var lights = ArrayBuffer[Light]()
  val camera = Camera()
  val cameraControls = CameraControls(window, camera)
  val skybox = Skybox(List("skybox/right.jpg", "skybox/left.jpg",
    "skybox/top.jpg", "skybox/bottom.jpg", "skybox/front.jpg", "skybox/back.jpg"))
  val lighting = Lighting()
  var imGuiVSync = ImBoolean(window.vSync)
  init()

  private def init() =
    camera.yaw = -69f
    camera.pitch = -26f
    camera.pos.set(1.3, 4.7, 11.7)

    val grassDiffTex = Texture("grass_diffuse.png")
    val grassSpecTex = Texture("grass_specular.png")
    val earthDiffTex = Texture("earth2048.bmp")
    val moonDiffTex = Texture("moon1024.bmp")
    val suzanneDiffTex = Texture("suzanne.png")
    val ratDiffTex = Texture("rat_diff.jpg")
    val ratNormTex = Texture("rat_norm.jpg")
    
    val grassBlock = Entity(grassDiffTex, grassSpecTex, Mesh.cube)
    grassBlock.pos.set(5, 1, 5)

    val earth = Entity(earthDiffTex, moonDiffTex, Mesh.sphere)
    earth.pos.set(3, 3, 3)

    val blueLight: Light.Point = Light.Point(Vector3f(0,0.4,1), Vector3f(0,3,2), 0.09f, 0.032f)
    val redLight: Light.Point = Light.Point(Vector3f(1,0.2,0.2), Vector3f(7,5,5), 0.09f, 0.032f)
    val brightLight: Light.Point = Light.Point(Vector3f(1,1,0.9), Vector3f(-2,9,7), 0.045f, 0.0075f)
    val blueSource = Entity(moonDiffTex, moonDiffTex, Mesh.sphere, Some(blueLight.color))
    val redSource = Entity(moonDiffTex, moonDiffTex, Mesh.sphere, Some(redLight.color))
    val brightSource = Entity(moonDiffTex, moonDiffTex, Mesh.sphere, Some(brightLight.color))
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
        val ent = Entity(grassDiffTex, grassSpecTex, Mesh.cube)
        ent.pos.set(i, 0, j)
        ent
      
    val suzanneMesh = Mesh.fromFile("suzanne.obj")(0)
    val suzanne = Entity(suzanneDiffTex, suzanneDiffTex, suzanneMesh, Some(Vector3f(0.2,0.1,0.2)))
    suzanne.pos.set(1, 2, 5)
    val sunlight = Light.Directional(Vector3f(.8, .8, 1), Vector3f(2.2, -2.4, 4))

    val ratMesh = Mesh.fromFile("rat.gltf")(0)
    val rat = Entity(ratDiffTex, ratNormTex, ratMesh)
    rat.pos.set(3, 0.50, 6)
    rat.scale = 10f

    entities.append(grassBlock, earth, redSource, blueSource, suzanne, brightSource, rat)
    entities.addAll(groundBlocks)
    lights.append(redLight, blueLight, brightLight, sunlight)
    lighting.updateLights(lights)

  override def processInput(delta: Long, mouseDelta: Vector2f) = 
    cameraControls.processInput(delta, mouseDelta)
    
  def updateState(delta: Long): Unit = 
    ()
    
  override def scene: Scene =
    val scene = Scene(camera, entities)
    scene.lighting = Some(lighting)
    scene.skybox = Some(skybox)
    scene

  override def drawGui() =
    ImGui.begin("Options and info")
    ImGui.text(f"FPS: ${window.fpsCounter.fps}%.0f")
    ImGui.text(s"Pitch: ${camera.pitch}")
    ImGui.text(s"Yaw: ${camera.yaw}")
    ImGui.text(f"X: ${camera.pos.x}%.2f")
    ImGui.text(f"Y: ${camera.pos.y}%.2f")
    ImGui.text(f"Z: ${camera.pos.z}%.2f")
    if ImGui.checkbox("VSync", imGuiVSync) then
      window.setVSync(imGuiVSync.get)
    ImGui.end()

@main
def main() =
  println(config.getString("database.host"))
  System.setProperty("joml.format", "false")

  // remember to use Using.resource() and not Using(), since using wraps the exception in a Try()
  Using.resource(Engine((win: Window) => ScalaCraft(win), "ScalaCraft")): engine =>
    engine.run()
