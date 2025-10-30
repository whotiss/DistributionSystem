ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "introtosclafx",
    libraryDependencies ++= {
      // Determine OS version of JavaFX binaries
      val osName = System.getProperty("os.name") match {
        case n if n.startsWith("Linux")   => "linux"
        case n if n.startsWith("Mac")     => "mac"
        case n if n.startsWith("Windows") => "win"
        case _                            => throw new Exception("Unknown platform!")
      }
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "21.0.4" classifier osName)
    },
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "21.0.0-R32"
      // Uncomment if/when you add JSON persistence:
      // , "io.circe" %% "circe-core" % "0.14.10"
      // , "io.circe" %% "circe-generic" % "0.14.10"
      // , "io.circe" %% "circe-parser" % "0.14.10"
    ),

    //  minimal runtime flags so the window actually shows
    fork := true,
    javaOptions ++= Seq(
      "--add-modules=javafx.controls,javafx.graphics,javafx.fxml"
    )
  )
