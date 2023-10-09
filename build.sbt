ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.1"
val os = sys.env.get("SC_TARGET").getOrElse("linux")
val projectName = "ScalaCraft"

lazy val root = (project in file("."))
  .settings(
    name := projectName,
    // https://github.com/scalafx/scalafx-ensemble/issues/6
    run / fork := true,
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case _                        => MergeStrategy.first
    },
    assembly / assemblyJarName := s"$projectName-$os.jar",
  )

libraryDependencies += "io.github.spair" % "imgui-java-lwjgl3" % "1.86.11"
libraryDependencies += "io.github.spair" % s"imgui-java-natives-$os-ft" % "1.86.11"
libraryDependencies += "commons-beanutils" % "commons-beanutils" % "1.9.4"
libraryDependencies += "org.apache.commons" % "commons-configuration2" % "2.9.0"
libraryDependencies += "org.joml" % "joml" % "1.10.5"

// https://mvnrepository.com/artifact/org.lwjgl/lwjgl/3.3.3
libraryDependencies += "org.lwjgl" % "lwjgl" % "3.3.3"
libraryDependencies += "org.lwjgl" % "lwjgl-opengl" % "3.3.3"
libraryDependencies += "org.lwjgl" % "lwjgl-glfw" % "3.3.3"
libraryDependencies += "org.lwjgl" % "lwjgl-stb" % "3.3.3"

libraryDependencies += "org.lwjgl" % "lwjgl" % "3.3.3" classifier s"natives-$os"
libraryDependencies += "org.lwjgl" % "lwjgl-opengl" % "3.3.3" classifier s"natives-$os"
libraryDependencies += "org.lwjgl" % "lwjgl-glfw" % "3.3.3" classifier s"natives-$os"
libraryDependencies += "org.lwjgl" % "lwjgl-stb" % "3.3.3" classifier s"natives-$os"
