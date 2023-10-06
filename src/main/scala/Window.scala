import org.lwjgl.*
import org.lwjgl.glfw.*
import org.lwjgl.opengl.*
import org.lwjgl.system.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.*
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import imgui.ImGui
import imgui.flag.ImGuiConfigFlags

case class Window():

  GLFWErrorCallback.createPrint(System.err).set();
  glfwInit()
  val win = glfwCreateWindow(300, 300, "Hi", NULL, NULL)
  glfwMakeContextCurrent(win)
  glfwShowWindow(win)
  GL.createCapabilities();

  private val imGuiRenderer = ImGuiImplGl3()
  private val imGuiPlatform = ImGuiImplGlfw()
  ImGui.createContext()
  val io = ImGui.getIO
  io.setIniFilename(null)
  imGuiPlatform.init(win, true)
  imGuiRenderer.init()

  def draw() =
    glColor3f(0.0, 1.0, 1.0)
    glBegin(GL_QUADS)
    glVertex2f(-0.5, 0.5)
    glVertex2f(0.5, 0.5)
    glVertex2f(0.5, -0.5)
    glVertex2f(-0.5, -0.5)
    glEnd()

  def drawGUI() =
    imGuiPlatform.newFrame()
    ImGui.newFrame()
    ImGui.begin("Cool Window");
    ImGui.end()
    ImGui.render()
    imGuiRenderer.renderDrawData(ImGui.getDrawData())
