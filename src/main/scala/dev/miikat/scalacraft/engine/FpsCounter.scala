package dev.miikat.scalacraft.engine

import java.time.Duration
import java.time.Instant

class FpsCounter:
  var fps = 0
  var frames = 0
  var lastFpsUpdate = Instant.now()

  def count() =
    val now = Instant.now()
    if Duration.between(lastFpsUpdate, now).toSeconds >= 1 then
      fps = frames
      frames = 0
      lastFpsUpdate = now
    frames += 1