package dev.miikat.scalacraft.engine

import org.joml.*

final case class Scene(entities: Array[Entity], lights: Array[Light], ambientLight: Vector3f = Vector3f(0.2f, 0.2f, 0.2f))
