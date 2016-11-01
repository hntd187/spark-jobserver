package spark.jobserver

/**
  * Created by scarman on 11/1/16.
  */
//For python jobs, there is no class loading or constructor required.
case class PythonJobInfo(eggPath: String) extends BinaryJobInfo with SparkJobInfo
