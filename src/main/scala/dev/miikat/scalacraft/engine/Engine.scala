package dev.miikat.scalacraft.engine

import imgui.ImGui
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL12C.*
import org.lwjgl.opengl.GL13C.*
import org.lwjgl.opengl.GL14C.*
import org.lwjgl.opengl.GL15C.*
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL21C.*
import org.lwjgl.opengl.GL30C.*
import org.lwjgl.opengl.GL31C.*
import org.lwjgl.opengl.GL32C.*
import org.lwjgl.opengl.GL33C.*
import org.lwjgl.opengl.GL40C.*
import org.lwjgl.opengl.GL41C.*
import org.lwjgl.opengl.GL42C.*
import org.lwjgl.opengl.GL43C.*
import org.lwjgl.opengl.GL44C.*
import org.lwjgl.opengl.GL45C.*
import org.lwjgl.system.MemoryUtil.NULL

import java.time.{Duration, Instant}
import scala.io.Source
import scala.util.Using
import org.lwjgl.system.MemoryStack

import org.joml.*
import org.lwjgl.opengl.GLUtil
import imgui.flag.ImGuiConfigFlags
import scala.collection.mutable.ArrayBuffer
import java.nio.ByteBuffer
import org.lwjgl.system.MemoryUtil

class Engine(game: Game) extends AutoCloseable:
  private val windowResult: (Long, (Int, Int)) = initWindow()
  private val window = windowResult._1
  private val winDim = windowResult._2
  private val (imGuiIo, imGuiPlatform, imGuiRenderer) = initImGui(window)
  private val shaderProgramId = initShaders()
  private val camera = Camera(winDim)
  private var paused = false
  game.init()

  def run(): Unit =
    var lastInstant = Instant.now()
    glfwPollEvents()
    var lastCursorPos = getCursorPos(window)

    while !glfwWindowShouldClose(window) do
      glfwPollEvents()
      val now = Instant.now()
      val cursorPos = getCursorPos(window)
      val delta = Duration.between(lastInstant, now).toMillis / 1000.0
      val cursorDelta = Vector2f(cursorPos).sub(lastCursorPos)

      if !paused then
        game.updateState(window, camera, delta, cursorDelta)

      this.drawScene(game.scene)
      this.drawGui()

      glfwSwapBuffers(window)
      lastInstant = now
      lastCursorPos = cursorPos

  private def initWindow() =
    println("Hello LWJGL " + org.lwjgl.Version.getVersion + "!")
    GLFWErrorCallback.createPrint(System.err).set()
    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE)
    glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, 1)
    val monitor = glfwGetPrimaryMonitor();
    val vidMode = glfwGetVideoMode(monitor);
    val win = glfwCreateWindow(vidMode.width, vidMode.height, "Hi", monitor, NULL)
    glfwMakeContextCurrent(win)
    glfwSetKeyCallback(win, (_, key, _, action, _) => 
      if action == GLFW_PRESS then
        key match
          case GLFW_KEY_ESCAPE =>  glfwSetWindowShouldClose(win, true)
          case GLFW_KEY_P =>
            paused = !paused
            if paused then
              imGuiIo.removeConfigFlags(ImGuiConfigFlags.NoMouse)
            else 
              imGuiIo.addConfigFlags(ImGuiConfigFlags.NoMouse)
            glfwSetInputMode(win, GLFW_CURSOR, if paused then GLFW_CURSOR_NORMAL else GLFW_CURSOR_DISABLED)
          case _ => ()
    )
    glfwSetInputMode(win, GLFW_STICKY_KEYS, GL_TRUE)
    glfwSetInputMode(win, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
    glfwShowWindow(win)

    GL.createCapabilities()
    GLUtil.setupDebugMessageCallback(System.err)
    glEnable(GL_DEPTH_TEST)


    println(s"GL Version: ${glGetString(GL_VERSION)}")
    glClearColor(0.0, 0.0, 0.0, 0.0)
    (win, (vidMode.width, vidMode.height))

  private def initImGui(win: Long) =
    val imGuiRenderer = ImGuiImplGl3()
    val imGuiPlatform = ImGuiImplGlfw()
    ImGui.createContext()
    val io = ImGui.getIO
    io.addConfigFlags(ImGuiConfigFlags.NoMouse)
    io.setIniFilename(null)
    imGuiPlatform.init(win, true)
    imGuiRenderer.init()
    (io, imGuiPlatform, imGuiRenderer)

  private def drawGui(): Unit =
    imGuiPlatform.newFrame()
    ImGui.newFrame()
    ImGui.begin("Cool Window")
    ImGui.text(s"Pitch: ${camera.pitch}")
    ImGui.text(s"Yaw: ${camera.yaw}")
    ImGui.text(s"X: ${camera.pos.x}")
    ImGui.text(s"Y: ${camera.pos.y}")
    ImGui.text(s"Z: ${camera.pos.z}")
    ImGui.end()
    ImGui.render()
    imGuiRenderer.renderDrawData(ImGui.getDrawData)

  def close(): Unit =
    glfwTerminate()

  private def initShaders() =
    val programId = glCreateProgram()
    val vertShaderId = glCreateShader(GL_VERTEX_SHADER)
    val fragShaderId = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(vertShaderId, Source.fromResource("shader.vert").mkString)
    glShaderSource(fragShaderId, Source.fromResource("shader.frag").mkString)
    glCompileShader(vertShaderId)
    glCompileShader(fragShaderId)
    glAttachShader(programId, vertShaderId)
    glAttachShader(programId, fragShaderId)

    glLinkProgram(programId)

    glDetachShader(programId, vertShaderId)
    glDetachShader(programId, fragShaderId)
    glDeleteShader(vertShaderId)
    glDeleteShader(fragShaderId)

    programId

  private def bindLightsUniform(scene: Scene): Unit =
    val pointLights = scene.lights.filter(_.isInstanceOf[Light.Point]).map(_.asInstanceOf[Light.Point])
    val dirLights = scene.lights.filter(_.isInstanceOf[Light.Directional]).map(_.asInstanceOf[Light.Directional])
    val spotLights = scene.lights.filter(_.isInstanceOf[Light.Spot]).map(_.asInstanceOf[Light.Spot])
    
    val buf = MemoryUtil.memCalloc(4096)
    def insertVec3(v: Vector3f) =
      v.get(buf)
      buf.position(buf.position + 4 * 3)
    
    insertVec3(scene.ambientLight)
    buf.putInt(pointLights.length)
    buf.putInt(dirLights.length)
    buf.putInt(spotLights.length)
    buf.putFloat(0f)
    buf.putFloat(0f)

    pointLights.foreach: pl =>
      insertVec3(pl.color)
      buf.putFloat(pl.linear)
      insertVec3(pl.pos)
      buf.putFloat(pl.quadratic)
    val blankPointLights = 10 - pointLights.length
    buf.position(buf.position + blankPointLights * Light.Point.alignedSize)
    
    
    dirLights.foreach: dl =>
      insertVec3(dl.color)
      buf.putFloat(0f)
      insertVec3(dl.dir)
      buf.putFloat(0f)
    val blankDirLights = 10 - pointLights.length
    buf.position(buf.position + blankDirLights * Light.Directional.alignedSize)

    spotLights.foreach: sl =>
      insertVec3(sl.color)
      buf.putFloat(0f)

      insertVec3(sl.pos)
      buf.putFloat(0f)
      
      insertVec3(sl.dir)
      buf.putFloat(sl.linear)
      
      buf.putFloat(sl.quadratic)
      buf.putFloat(sl.innerCutoff)
      buf.putFloat(sl.outerCutoff)
      buf.putFloat(0f)
    val blankSpotLights = 10 - pointLights.length
    buf.position(buf.position + blankSpotLights * Light.Spot.alignedSize)

    val bytesWritten = buf.position
    buf.position(0)

    val UBO = glCreateBuffers()
    glNamedBufferStorage(UBO, bytesWritten, GL_DYNAMIC_STORAGE_BIT)
    // we use the native so we can use a bigger buf as src,
    nglNamedBufferSubData(UBO, /*offset*/ 0, bytesWritten, MemoryUtil.memAddress(buf))
    glBindBufferBase(GL_UNIFORM_BUFFER, /*index*/0, UBO)

  private def drawScene(scene: Scene) =
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glUseProgram(shaderProgramId)
    bindLightsUniform(scene)

    scene.entities.foreach: ent =>
      setShaderMatrix(Matrix4f(camera.projMatrix).mul(camera.viewMatrix).mul(ent.modelMatrix))
      ent.texture.bind()
      ent.mesh.draw()

  private def setShaderMatrix(mat: Matrix4f) =
    Using.resource(MemoryStack.stackPush()): stack =>
      val uniLoc = glGetUniformLocation(shaderProgramId, "MVP")
      glUniformMatrix4fv(uniLoc, false, mat.get(stack.mallocFloat(16)))

  private def getCursorPos(win: Long) =
    Using.resource(MemoryStack.stackPush()): stack =>
      val (x, y) = (stack.mallocDouble(1), stack.mallocDouble(1))
      glfwGetCursorPos(win, x, y)
      Vector2f(x.get.toFloat, y.get.toFloat)