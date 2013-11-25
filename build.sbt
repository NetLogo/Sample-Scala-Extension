scalaVersion := "2.10.3"

scalaSource in Compile := baseDirectory.value / "src"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-Xfatal-warnings",
                      "-encoding", "us-ascii", "-target:jvm-1.7")

libraryDependencies +=
  "org.nlogo" % "NetLogoHeadless" % "5.x-e2bba9de" from
    "http://ccl.northwestern.edu/devel/NetLogoHeadless-e2bba9de.jar"

artifactName := { (_, _, _) => "sample-scala.jar" }

packageOptions := Seq(
  Package.ManifestAttributes(
    ("Extension-Name", "sample-scala"),
    ("Class-Manager", "SampleScalaExtension"),
    ("NetLogo-Extension-API-Version", "5.0")))

packageBin in Compile := {
  val jar = (packageBin in Compile).value
  val base = baseDirectory.value
  IO.copyFile(jar, base / "sample-scala.jar")
  if(Process("git diff --quiet --exit-code HEAD").! == 0) {
    Process("git archive -o sample-scala.zip --prefix=sample-scala/ HEAD").!!
    IO.createDirectory(base / "sample-scala")
    IO.copyFile(base / "sample-scala.jar", base / "sample-scala" / "sample-scala.jar")
    Process("zip sample-scala.zip sample-scala/sample-scala.jar").!!
    IO.delete(base / "sample-scala")
  }
  else {
    streams.value.log.warn("working tree not clean; no zip archive made")
    IO.delete(base / "sample-scala.zip")
  }
  jar
}

cleanFiles ++= Seq(
  baseDirectory.value / "sample-scala.jar",
  baseDirectory.value / "sample-scala.zip"
)
