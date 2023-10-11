package dev.miikat.scalacraft.game

import dev.miikat.scalacraft.engine.Mesh

object Cube:
  // converted from CppCraft with sed 's/[{}]//g;s/\.f/.0/g;s/ //g'
  val vertices = Array(
    0.5,-0.5,0.5,1,0,0,0,1.0/3,0.5,-0.5,0.5,0,-1,0,1,1.0/3,
    0.5,-0.5,0.5,0,0,1,1,1.0/3,0.5,0.5,0.5,1,0,0,0,2.0/3,
    0.5,0.5,0.5,0,1,0,1,2.0/3,0.5,0.5,0.5,0,0,1,1,2.0/3,
    -0.5,0.5,0.5,-1,0,0,1,2.0/3,-0.5,0.5,0.5,0,1,0,0,2.0/3,
    -0.5,0.5,0.5,0,0,1,0,2.0/3,-0.5,-0.5,0.5,-1,0,0,1,1.0/3,
    -0.5,-0.5,0.5,0,-1,0,0,1.0/3,-0.5,-0.5,0.5,0,0,1,0,1.0/3,
    0.5,-0.5,-0.5,1,0,0,1,1.0/3,0.5,-0.5,-0.5,0,-1,0,1,0,
    0.5,-0.5,-0.5,0,0,-1,0,1.0/3,0.5,0.5,-0.5,1,0,0,1,2.0/3,
    0.5,0.5,-0.5,0,1,0,1,1,	0.5,0.5,-0.5,0,0,-1,0,2.0/3,
    -0.5,0.5,-0.5,-1,0,0,0,2.0/3,-0.5,0.5,-0.5,0,1,0,0,1,
    -0.5,0.5,-0.5,0,0,-1,1,2.0/3,-0.5,-0.5,-0.5,-1,0,0,0,1.0/3,
    -0.5,-0.5,-0.5,0,-1,0,0,0,-0.5,-0.5,-0.5,0,0,-1,1,1.0/3,
  ).map(_.toFloat)

  val indices = Array(
    0*3+2,1*3+2,2*3+2,2*3+2,3*3+2,0*3+2,3*3+0,2*3+0,6*3+0,6*3+0,
    7*3+0,3*3+0,1*3+1,5*3+1,6*3+1,6*3+1,2*3+1,1*3+1,4*3+2,7*3+2,
    6*3+2,6*3+2,5*3+2,4*3+2,0*3+0,4*3+0,5*3+0,5*3+0,1*3+0,0*3+0,
    0*3+1,3*3+1,7*3+1,7*3+1,4*3+1,0*3+1
  )

  val mesh = Mesh(vertices, indices)