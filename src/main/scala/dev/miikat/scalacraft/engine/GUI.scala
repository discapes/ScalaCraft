package dev.miikat.scalacraft.engine

import imgui.ImGui
import imgui.glfw.ImGuiImplGlfw
import imgui.gl3.ImGuiImplGl3
import imgui.flag.ImGuiConfigFlags

class GUI(glfwHandle: Long):
  var (imGuiIo, imGuiPlatform, imGuiRenderer) = init()

  private def init() = 
    val imGuiRenderer = ImGuiImplGl3()
    val imGuiPlatform = ImGuiImplGlfw()
    ImGui.createContext()
    val io = ImGui.getIO
    io.addConfigFlags(ImGuiConfigFlags.NoMouse)
    io.setIniFilename(null)
    imGuiPlatform.init(glfwHandle, true)
    imGuiRenderer.init()
    (io, imGuiPlatform, imGuiRenderer)

  def setCaptureMouse(capture: Boolean) =
    if capture then
      imGuiIo.removeConfigFlags(ImGuiConfigFlags.NoMouse)
    else
      imGuiIo.addConfigFlags(ImGuiConfigFlags.NoMouse)
      
  def beginFrame() =
    imGuiPlatform.newFrame()
    ImGui.newFrame()

  def endFrame() =
    ImGui.render()
    imGuiRenderer.renderDrawData(ImGui.getDrawData)