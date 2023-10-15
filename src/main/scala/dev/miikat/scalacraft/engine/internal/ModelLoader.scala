package dev.miikat.scalacraft.engine.internal

import org.lwjgl.assimp.Assimp.*
import org.lwjgl.assimp.*
import org.lwjgl.system.MemoryUtil
import dev.miikat.scalacraft.engine.Util
import scala.collection.mutable.ArrayBuffer
import org.joml.Vector3f
import dev.miikat.scalacraft.engine.Mesh

object ModelLoader:
  private val aiIoSys = AIFileIO.create()
    .OpenProc((pFileIO, pFilename, openMode) => 
      val filename = MemoryUtil.memUTF8(pFilename)
      // AssImp adds a slash already
      val data = Util.resourceToByteBuffer(filename, insterSlash =  false)
      AIFile.create()
        .ReadProc((pFile, pBuffer, size, count) => 
          val max = Math.min(data.remaining() / size, count)
          MemoryUtil.memCopy(MemoryUtil.memAddress(data), pBuffer, max * size)
          data.position(data.position() + (max * size).toInt)
          max
        )
        .SeekProc((pFile, offset, origin) => 
          if origin == Assimp.aiOrigin_CUR then
            data.position(data.position() + offset.toInt)
          else if origin == Assimp.aiOrigin_SET then
            data.position(offset.toInt)
          else if origin == Assimp.aiOrigin_END then
            data.position(data.limit() + offset.toInt)
          0
        )
        .FileSizeProc(pFile => data.limit())
        .address()
    )
    .CloseProc((pFileIO, pFile) => {
        val aiFile = AIFile.create(pFile)
        aiFile.ReadProc().free()
        aiFile.SeekProc().free()
        aiFile.FileSizeProc().free()
    })

  // assimp lwjgl bindings use the C flat interface
  def fromFile(path: String) = 
    val scene = aiImportFileEx(
      path,
      aiProcess_Triangulate | aiProcess_GenSmoothNormals | 
      aiProcess_JoinIdenticalVertices | aiProcess_OptimizeGraph | 
      aiProcess_OptimizeMeshes | aiProcess_GenUVCoords,
      aiIoSys
    )
    if scene == null then throw Exception(aiGetErrorString())
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
    aiReleaseImport(scene)

    meshes
