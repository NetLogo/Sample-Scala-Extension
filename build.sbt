enablePlugins(org.nlogo.build.NetLogoExtension)

netLogoExtName      := "sample-scala"

netLogoClassManager := "SampleScalaExtension"

netLogoZipSources   := false

scalaVersion           := "2.11.7"

scalaSource in Compile := baseDirectory.value / "src"

scalacOptions          ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "us-ascii")

// The remainder of this file is for options specific to bundled netlogo extensions
// if copying this extension to build your own, you need nothing past line 14 to build
// sample-scala.zip
netLogoTarget :=
  org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)

val netLogoJarURL =
  Option(System.getProperty("netlogo.jar.url")).getOrElse("http://ccl.northwestern.edu/netlogo/5.3.0/NetLogo.jar")

val netLogoJarOrDependency = {
  import java.io.File
  import java.net.URI
  if (netLogoJarURL.startsWith("file:"))
    Seq(unmanagedJars in Compile += new File(new URI(netLogoJarURL)))
  else
    Seq(libraryDependencies += "org.nlogo" % "NetLogo" % "5.3.0" from netLogoJarURL)
}

netLogoJarOrDependency
