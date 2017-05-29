name := "spark-nlp"
version := "0.1"
scalaVersion := "2.11.1"
libraryDependencies ++=
  Seq("org.apache.spark" % "spark-core_2.11" % "2.1.1" % "provided",
      "org.apache.spark" % "spark-mllib_2.11" % "2.1.1" % "provided",
      "org.apache.lucene" % "lucene-analyzers-kuromoji" % "6.5.1")
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
