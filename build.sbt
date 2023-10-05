ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaTest"
  )

libraryDependencies += "io.github.spair" % "imgui-java-lwjgl3" % "1.86.11"
libraryDependencies += "io.github.spair" % "imgui-java-natives-linux-ft" % "1.86.11"

// https://mvnrepository.com/artifact/org.lwjgl/lwjgl/3.3.3
libraryDependencies += "org.lwjgl" % "lwjgl" % "3.3.3"
libraryDependencies += "org.lwjgl" % "lwjgl-opengl" % "3.3.3"
libraryDependencies += "org.lwjgl" % "lwjgl-glfw" % "3.3.3"

libraryDependencies += "org.lwjgl" % "lwjgl" % "3.3.3" classifier "natives-linux"
libraryDependencies += "org.lwjgl" % "lwjgl-opengl" % "3.3.3" classifier "natives-linux"
libraryDependencies += "org.lwjgl" % "lwjgl-glfw" % "3.3.3" classifier "natives-linux"
