package dev.miikat.scalacraft.engine

import org.joml.*
import scala.Option


enum Light:
  case Point(color: Vector3f, pos: Vector3f, const: Float, linear: Float, quadratic: Float)
  case Directional(color: Vector3f, dir: Vector3f)
  case Spot(color: Vector3f, pos: Vector3f, dir: Vector3f,  linear: Float, quadratic: Float, innerCutoff: Float, outerCutoff: Float)

object Light:
  object Point:
    // read from the fragment shader based on std140
    val alignedSize = 2 * 16
  object Directional:
    val alignedSize = 2 * 16
  object Spot:
    val alignedSize = 4 * 16
