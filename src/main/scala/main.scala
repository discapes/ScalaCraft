import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._
import org.apache.commons.configuration2.builder.fluent.Configurations
import scala.io.Source

val configs = Configurations()
val config = configs.properties(this.getClass.getResource("config.properties"))

val vert = Source.fromResource("shader.vert").mkString

@main
def main() =
  println(config.getString("database.host"))
  println(vert)
  println("Hello LWJGL " + org.lwjgl.Version.getVersion() + "!");
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