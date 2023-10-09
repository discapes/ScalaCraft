#!Nsis Installer Command Script
#
# This is an NSIS Installer Command Script generated automatically
# by the Fedora nsiswrapper program.  For more information see:
#
#   http://fedoraproject.org/wiki/MinGW
#
# To build an installer from the script you would normally do:
#
#   makensis this_script
#
# which will generate the output file 'installer.exe' which is a Windows
# installer containing your program.

Name "ScalaCraft"
OutFile "installer.exe"
InstallDir "$ProgramFiles64\ScalaCraft"

ShowInstDetails nevershow
ShowUninstDetails hide

Page directory
Page instfiles

DirText "Please select the installation folder."

Section "ScalaCraft"
  SectionIn RO

  SetOutPath "$INSTDIR\."
  File "ScalaCraft-windows.jar"
  File /r "jre"
SectionEnd

Section "Start Menu Shortcuts"
  CreateDirectory "$SMPROGRAMS\ScalaCraft"
  CreateShortCut "$SMPROGRAMS\ScalaCraft\Uninstall ScalaCraft.lnk" "$INSTDIR\Uninstall ScalaCraft.exe" "" "$INSTDIR\Uninstall ScalaCraft.exe" 0
  CreateShortCut "$SMPROGRAMS\ScalaCraft\ScalaCraft.lnk" "$INSTDIR\jre\bin\java.exe" '-jar "$INSTDIR/ScalaCraft-windows.jar"' "$INSTDIR\Uninstall ScalaCraft.exe" 0
SectionEnd

Section "Desktop Icons"
SectionEnd

Section "Uninstall"
  Delete /rebootok "$SMPROGRAMS\ScalaCraft\Uninstall ScalaCraft.lnk"
  RMDir "$SMPROGRAMS\ScalaCraft"

  RMDir /r "$INSTDIR\.\jre"

  Delete /rebootok "$INSTDIR\.\ScalaCraft-windows.jar"
  RMDir "$INSTDIR\."
  RMDir "$INSTDIR"
SectionEnd

Section -post
  WriteUninstaller "$INSTDIR\Uninstall ScalaCraft.exe"
SectionEnd
