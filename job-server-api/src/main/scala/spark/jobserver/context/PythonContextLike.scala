package spark.jobserver.context

import spark.jobserver.ContextLike

/**
  * Created by scarman on 11/1/16.
  */
trait PythonContextLike extends ContextLike {

  /**
    * The Python Subprocess needs to know what sort of context to build from the JVM context.
    * It can't interrogate the JVM type system, so this method is used as an explicit indicator.
    *
    * @return the full canonical class name of the context type
    */
  def contextType: String

  /**
    *
    * @return The entries with which to populate the PYTHONPATH environment variable when
    *         launching the python subprocess.
    */
  def pythonPath: Seq[String]

  /**
    * Which process to call to execute the Python interpreter, e.g `python`, `python3`
    *
    * @return the executable to call
    */
  def pythonExecutable: String

  /**
    * Any mutable actions which need to be taken before the context is used.
    */
  def setupTasks(): Unit
}
