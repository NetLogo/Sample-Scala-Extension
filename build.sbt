scalaVersion := "2.10.1"

scalaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-Xfatal-warnings",
                      "-encoding", "us-ascii", "-target:jvm-1.7")

libraryDependencies +=
  "org.nlogo" % "NetLogoHeadless" % "5.x-de4980d4" from
    "http://ccl.northwestern.edu/devel/NetLogoHeadless-de4980d4.jar"

artifactName := { (_, _, _) => "sample-scala.jar" }

packageOptions := Seq(
  Package.ManifestAttributes(
    ("Extension-Name", "sample-scala"),
    ("Class-Manager", "SampleScalaExtension"),
    ("NetLogo-Extension-API-Version", "5.0")))

packageBin in Compile <<= (packageBin in Compile, baseDirectory, streams) map {
  (jar, base, s) =>
    IO.copyFile(jar, base / "sample-scala.jar")
    if(Process("git diff --quiet --exit-code HEAD").! == 0) {
      Process("git archive -o sample-scala.zip --prefix=sample-scala/ HEAD").!!
      IO.createDirectory(base / "sample-scala")
      IO.copyFile(base / "sample-scala.jar", base / "sample-scala" / "sample-scala.jar")
      Process("zip sample-scala.zip sample-scala/sample-scala.jar").!!
      IO.delete(base / "sample-scala")
    }
    else {
      s.log.warn("working tree not clean; no zip archive made")
      IO.delete(base / "sample-scala.zip")
    }
    jar
  }

cleanFiles <++= baseDirectory { base =>
  Seq(base / "sample-scala.jar",
      base / "sample-scala.zip") }

