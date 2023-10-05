import org.lwjgl._
import org.lwjgl.glfw._
import org.lwjgl.opengl._
import org.lwjgl.system._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._
import org.lwjgl.system.MemoryUtil._
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import imgui.ImGui

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
