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



@main
def main() =
  println("Hello LWJGL " + Version.getVersion() + "!");
  val window = Window()

  println(s"GL Version: ${glGetString(GL_VERSION)}")
  glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

  while !glfwWindowShouldClose(window.win) do
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    window.draw()
    window.drawGUI()

    glfwSwapBuffers(window.win)
    glfwPollEvents()

  glfwTerminate()