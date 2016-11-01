package spark.jobserver.context

case class PythonJobContainer[C <: PythonContextLike](job: PythonJob[C])
  extends JobContainer[PythonJob[C]] {
  def getSparkJob: PythonJob[C] = job
}
