package dev.miikat.scalacraft.engine

import org.joml.*

enum Light:
  case Directional(color: Vector3f, dir: Vector3f)
  case Point(color: Vector3f, pos: Vector3f, const: Float, linear: Float, quadratic: Float)
  case Spot(color: Vector3f, pos: Vector3f, dir: Vector3f,  linear: Float, quadratic: Float, innerCutoff: Float, outerCutoff: Float)