import org.nlogo.build.NetLogoExtension

enablePlugins(NetLogoExtension)

version    := "1.1.1"
isSnapshot := true

scalaVersion           := "2.12.12"
scalaSource in Compile := baseDirectory.value / "src" / "main"
scalaSource in Test    := baseDirectory.value / "src" / "test"
scalacOptions          ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "us-ascii", "-release", "11")

netLogoExtName      := "sample-scala"
netLogoClassManager := "org.nlogo.extensions.samplescala.SampleScalaExtension"
netLogoVersion      := "6.2.2"
netLogoZipExtras   ++= Seq(baseDirectory.value / "README.md", baseDirectory.value / "example-models")
