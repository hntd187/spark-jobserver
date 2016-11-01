package spark.jobserver.python

import spark.jobserver.context.JobContainer

case class PythonJobContainer[C <: PythonContextLike](job: PythonJob[C]) extends JobContainer[PythonJob[C]] {
  override def getSparkJob: PythonJob[C] = job
}
