package dev.miikat.scalacraft.engine

import org.joml.*
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL12C.*
import org.lwjgl.opengl.GL13C.*
import org.lwjgl.opengl.GL14C.*
import org.lwjgl.opengl.GL15C.*
import org.lwjgl.opengl.GL20C.*
import org.lwjgl.opengl.GL21C.*
import org.lwjgl.opengl.GL30C.*
import org.lwjgl.opengl.GL31C.*
import org.lwjgl.opengl.GL32C.*
import org.lwjgl.opengl.GL33C.*
import org.lwjgl.opengl.GL40C.*
import org.lwjgl.opengl.GL41C.*
import org.lwjgl.opengl.GL42C.*
import org.lwjgl.opengl.GL43C.*
import org.lwjgl.opengl.GL44C.*
import org.lwjgl.opengl.GL45C.*
import org.lwjgl.system.MemoryUtil

class Renderer(window: Window):
  private val shader = Shader("shader.vert", "shader.frag")
  private val skyboxShader = Shader("shader_skybox.vert", "shader_skybox.frag")

  def render(scene: Scene) = 
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    shader.use()
    if scene.lighting.isDefined then
      scene.lighting.get.bind(0)

    scene.entities.foreach: ent =>
      shader.setMatrix4f("MVP", Matrix4f(window.projMatrix).mul(scene.camera.viewMatrix).mul(ent.modelMatrix))
      shader.setMatrix4f("M", ent.modelMatrix)
      shader.setVector3f("camPos", scene.camera.pos)
      shader.setVector3f("ambientLight", ent.ambientLight.getOrElse(scene.ambientLight))
      
      ent.texture.bind(0)
      ent.spec.bind(1)
      ent.mesh.draw()
    
    if scene.skybox.isDefined then
      glDepthFunc(GL_LEQUAL)
      skyboxShader.use()
      val view2 = Matrix4f(Matrix3f(scene.camera.viewMatrix))
      skyboxShader.setMatrix4f("view", view2)
      skyboxShader.setMatrix4f("projection", window.projMatrix)
      scene.skybox.get.cubemap.bind(0)
      scene.skybox.get.draw()
      glDepthFunc(GL_LESS)

