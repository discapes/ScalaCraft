package dev.miikat.scalacraft.game

import scala.collection.mutable.ArrayBuffer
import dev.miikat.scalacraft.engine.Mesh

object Sphere:
  // from https://www.songho.ca/opengl/gl_sphere.html#example_sphere
  def create(sectorCount: Int, stackCount: Int, radius: Float) =
    val vertices = ArrayBuffer[Float]()
    val indices = ArrayBuffer[Int]()
    val lengthInv = 1.0f / radius
    val sectorStep = 2 * Math.PI / sectorCount
    val stackStep = Math.PI / stackCount

    for i <- 0 to stackCount do 
      val stackAngle = Math.PI / 2 - i * stackStep        // starting from pi/2 to -pi/2
      val xy = radius * Math.cos(stackAngle)             // r * cos(u)
      val z = radius * Math.sin(stackAngle)              // r * sin(u)

      // add (sectorCount+1) vertices per stack
      // first and last vertices have same position and normal, but different tex coords

      for j <- 0 to stackCount do
        val sectorAngle = j * sectorStep           // starting from 0 to 2pi

        // vertex position (x, y, z)
        val x = xy * Math.cos(sectorAngle)             // r * cos(u) * cos(v)
        val y = xy * Math.sin(sectorAngle)             // r * cos(u) * sin(v)
        vertices.append(x.toFloat)
        vertices.append(y.toFloat)
        vertices.append(z.toFloat)

        // normalized vertex normal (nx, ny, nz)
        val nx = x * lengthInv
        val ny = y * lengthInv
        val nz = z * lengthInv
        vertices.append(nx.toFloat)
        vertices.append(ny.toFloat)
        vertices.append(nz.toFloat)

        // vertex tex coord (s, t) range between [0, 1]
        val s = j.toFloat / sectorCount
        val t = i.toFloat / stackCount
        vertices.append(s.toFloat)
        vertices.append(t.toFloat)
      end for
    end for

    for i <- 0 until stackCount do
      var k1 = i * (sectorCount + 1);     // beginning of current stack
      var k2 = k1 + sectorCount + 1;      // beginning of next stack

      for j <- 0 until sectorCount do
        // 2 triangles per sector excluding first and last stacks
        // k1 => k2 => k1+1
        if i != 0 then
            indices.append(k1);
            indices.append(k2);
            indices.append(k1 + 1);

        // k1+1 => k2 => k2+1
        if i != (stackCount-1) then
            indices.append(k1 + 1);
            indices.append(k2);
            indices.append(k2 + 1);

        k1 += 1
        k2 += 1
      end for
    end for

    Mesh(vertices.toArray, indices.toArray)