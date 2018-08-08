enablePlugins(org.nlogo.build.NetLogoExtension)

netLogoExtName      := "sample-scala"

netLogoClassManager := "SampleScalaExtension"

netLogoZipSources   := false

version := "1.1.0"

scalaVersion           := "2.12.0"

scalaSource in Compile := baseDirectory.value / "src"

scalacOptions          ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "us-ascii")

// The remainder of this file is for options specific to bundled netlogo extensions
// if copying this extension to build your own, you need nothing past line 14 to build
// sample-scala.zip
netLogoTarget :=
  org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)

netLogoVersion := "6.0.2-M1"
