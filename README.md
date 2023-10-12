# ScalaCraft

### Features

- Lighting
  - Types:
    - Diffuse
    - Specular
    - Ambient
  - Sources:
    - Point
    - Spot
    - Directional
- Texture loading (stb_image)
- Model loading (AssImp)
- Sphere generation
- Camera and flying
- ImGui
- Single-buffer IBO and VBO
- Direct State Access
- Todo:
  - Skybox
  - Material loading (AssImp)
  - Meshes with multiple textures (texture arrays)
  - ImGui option to toggle fullscreen and working resize handler
  - ImGui option to toggle VSync
  - FpsCounter class


### Building a Jar

- Install SBT (Scala Build Tool)
- Build a jar file with dependencies
  - For Linux: `sbt assembly`
  - For Windows: `SC_TARGET=windows sbt assembly`

### Packaging into a native executable

#### Linux deb/rpm package with jpackage

```bash
IDIR="$(mktemp -d)"
cp target/scala-3.3.1/ScalaCraft-linux.jar "$IDIR"
# --type rpm can be replaced with --type deb
jpackage --name ScalaCraft --main-jar ScalaCraft-linux.jar --main-class dev.miikat.scalacraft.game.main --type rpm --input "$IDIR" --linux-shortcut
rm "$IDIR" -rf
# the produced RPM installs the app to /opt/scalacraft
```

NOTE: jpackage with --type msi/exe requires the WiX Toolset 3.0, which is depreacated in favor of v4, and isn't available anymore. Wix 3.11.2 gives the following error: `Detected [candle.exe] version  but version 3.0 is required.`

#### Windows NSIS installer

- Download NSIS
- Download the JRE 17 zip file at https://adoptium.net/temurin/releases/?os=windows&arch=x64
- Unzip the JRE, rename the folder to `jre`
- Copy `jre` and `scalacraft.nsi` to `target/scala-3.1.1`
- In `target/scala-3.1.1`, run `makensis scalacraft.nsi`