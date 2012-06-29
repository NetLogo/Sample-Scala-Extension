scalaVersion := "2.9.2"

scalaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings",
                      "-encoding", "us-ascii")

libraryDependencies +=
  "org.nlogo" % "NetLogo" % "5.0.1" from
    "http://ccl.northwestern.edu/netlogo/5.0.1/NetLogo.jar"

artifactName := { (_, _, _) => "sample-scala.jar" }

packageOptions := Seq(
  Package.ManifestAttributes(
    ("Extension-Name", "sample-scala"),
    ("Class-Manager", "SampleScalaExtension"),
    ("NetLogo-Extension-API-Version", "5.0")))

packageBin in Compile <<= (packageBin in Compile, baseDirectory, streams) map {
  (jar, base, s) =>
    IO.copyFile(jar, base / "sample-scala.jar")
    Process("pack200 --modification-time=latest --effort=9 --strip-debug " +
            "--no-keep-file-order --unknown-attribute=strip " +
            "sample-scala.jar.pack.gz sample-scala.jar").!!
    if(Process("git diff --quiet --exit-code HEAD").! == 0) {
      Process("git archive -o sample-scala.zip --prefix=sample-scala/ HEAD").!!
      IO.createDirectory(base / "sample-scala")
      IO.copyFile(base / "sample-scala.jar", base / "sample-scala" / "sample-scala.jar")
      IO.copyFile(base / "sample-scala.jar.pack.gz", base / "sample-scala" / "sample-scala.jar.pack.gz")
      Process("zip sample-scala.zip sample-scala/sample-scala.jar sample-scala/sample-scala.jar.pack.gz").!!
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
      base / "sample-scala.jar.pack.gz",
      base / "sample-scala.zip") }

