import org.nlogo.build.NetLogoExtension

enablePlugins(NetLogoExtension)

version    := "1.1.1"
isSnapshot := true

scalaVersion          := "2.12.12"
Compile / scalaSource := baseDirectory.value / "src" / "main"
Test / scalaSource    := baseDirectory.value / "src" / "test"
scalacOptions        ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "us-ascii", "-release", "11")

netLogoExtName      := "sample-scala"
netLogoClassManager := "org.nlogo.extensions.samplescala.SampleScalaExtension"
netLogoVersion      := "6.3.0"
netLogoZipExtras   ++= Seq(baseDirectory.value / "README.md", baseDirectory.value / "example-models")
