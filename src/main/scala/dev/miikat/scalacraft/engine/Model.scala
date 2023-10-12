package dev.miikat.scalacraft.engine

import org.lwjgl.assimp.Assimp.*
import org.lwjgl.assimp.*
import scala.collection.mutable.ArrayBuffer
import org.joml.Vector3f

object Model:
  // assimp lwjgl bindings use the C flat interface
  def load(path: String) = 
    val fileBuf = Util.resourceToByteBuffer(path)
    val scene = aiImportFileFromMemory(
      fileBuf,
      aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_GenSmoothNormals | 
      aiProcess_JoinIdenticalVertices | aiProcess_OptimizeGraph | 
      aiProcess_OptimizeMeshes | aiProcess_GenUVCoords,
      ""
    )
    val nMeshes = scene.mNumMeshes
    println(s"assimp: $nMeshes meshes")
    val aiMeshes = for i <- 0 until nMeshes yield AIMesh.create(scene.mMeshes.get)
    val meshes = aiMeshes.map: aiMesh =>

      println(s"assimp: ${aiMesh.mNumVertices} vertices ")
      println(s"assimp: ${aiMesh.mNumFaces} faces ")
      val dataBuf = ArrayBuffer[Float]()
      val indexBuf = ArrayBuffer[Int]()

      // because mVertices creates a new buf each time
      val aiPos = aiMesh.mVertices
      val aiNorms = aiMesh.mNormals
      val aiUvs = Option(aiMesh.mTextureCoords(0))
      for i <- 0 until aiMesh.mNumVertices do
        val pos = aiPos.get
        val norm =  aiNorms.get
        val uv = aiUvs
          .map(_.get)
          .map(v => Vector3f(v.x, v.y, v.z))
          .getOrElse(Vector3f())
        dataBuf.append(pos.x, pos.y, pos.z, norm.x, norm.y, norm.z, uv.x, uv.y)

      val aiFaces = aiMesh.mFaces
      for i <- 0 until aiMesh.mNumFaces do
        val indices = aiFaces.get.mIndices
        indexBuf.append(indices.get, indices.get, indices.get)
      Mesh(dataBuf.toArray, indexBuf.toArray)

    meshes
