package dev.miikat.scalatest

import org.apache.commons.configuration2.builder.fluent.Configurations
import org.lwjgl.glfw.GLFW.*

import scala.io.Source
import scala.util.Using

val configs = Configurations()
val config = configs.properties(this.getClass.getResource("config.properties"))

val vert = Source.fromResource("shader.vert").mkString

@main
def main() =
  println(config.getString("database.host"))

  Using(Engine()):
    engine => engine.run()



