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

def draw() =
  glColor3f(0.0, 1.0, 1.0)

  glBegin(GL_QUADS)
  glVertex2f(-0.5, 0.5)
  glVertex2f(0.5, 0.5)
  glVertex2f(0.5, -0.5)
  glVertex2f(-0.5, -0.5)
  glEnd()

@main
def main() =
  println("Hello LWJGL " + Version.getVersion() + "!");

  GLFWErrorCallback.createPrint(System.err).set();
  glfwInit()
  val win = glfwCreateWindow(300, 300, "Hi", NULL, NULL)
  glfwMakeContextCurrent(win)
  glfwShowWindow(win)
  GL.createCapabilities();

  val imGuiRenderer = ImGuiImplGl3()
  val imGuiPlatform = ImGuiImplGlfw()
  ImGui.createContext()
  imGuiPlatform.init(win, true)
  imGuiRenderer.init()

  println(s"GL Version: ${glGetString(GL_VERSION)}")
  glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

  while !glfwWindowShouldClose(win) do
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    draw()

    imGuiPlatform.newFrame()
    ImGui.newFrame()

    ImGui.begin("Cool Window");
    ImGui.end()

    ImGui.render()
    imGuiRenderer.renderDrawData(ImGui.getDrawData())

    glfwSwapBuffers(win)
    glfwPollEvents()

  glfwTerminate()