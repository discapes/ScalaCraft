package dev.miikat.scalacraft.engine

import org.joml.*
import scala.Option


enum Light:
  case Point(var color: Vector3f, var pos: Vector3f, var linear: Float, var quadratic: Float)
  case Directional(var color: Vector3f, var dir: Vector3f)
  case Spot(var color: Vector3f, var pos: Vector3f, var dir: Vector3f,  var linear: Float, var quadratic: Float, var innerCutoff: Float, var outerCutoff: Float)

object Light:
  object Point:
    // read from the fragment shader based on std140
    val alignedSize = 2 * 16
  object Directional:
    val alignedSize = 2 * 16
  object Spot:
    val alignedSize = 4 * 16
