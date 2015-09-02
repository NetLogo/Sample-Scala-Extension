scalaVersion := "2.11.7"

enablePlugins(org.nlogo.build.NetLogoExtension)

scalaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "us-ascii")

javacOptions ++= Seq("-Xlint:deprecation")

netLogoExtName      := "sample-scala"

netLogoClassManager := "SampleScalaExtension"

netLogoZipSources   := false

// The remainder of this file is for options specific to bundled netlogo extensions
// if copying this extension to build your own, you need nothing past line 14 to build
// sample-scala.zip
val netLogoJarOrDependency =
  Option(System.getProperty("netlogo.jar.url"))
    .orElse(Some("http://ccl.northwestern.edu/netlogo/5.3.0/NetLogo.jar"))
    .map { url =>
      import java.io.File
      import java.net.URI
      if (url.startsWith("file:"))
        (Seq(new File(new URI(url))), Seq())
      else
        (Seq(), Seq("org.nlogo" % "NetLogo" % "5.3.0" from url))
    }.get

unmanagedJars in Compile ++= netLogoJarOrDependency._1

libraryDependencies      ++= netLogoJarOrDependency._2

packageBin in Compile := {
  val jar = (packageBin in Compile).value
  val sampleScalaZip = baseDirectory.value / "sample-scala.zip"
  if (sampleScalaZip.exists) {
    IO.unzip(sampleScalaZip, baseDirectory.value)
    for (jar <- (baseDirectory.value / "sample-scala" ** "*.jar").get)
      IO.copyFile(jar, baseDirectory.value / jar.getName)
    IO.delete(baseDirectory.value / "sample-scala")
  }
  jar
}
