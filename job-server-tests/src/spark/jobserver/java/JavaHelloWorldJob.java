package spark.jobserver.java;

import com.typesafe.config.Config;
import org.apache.spark.api.java.JavaSparkContext;


public class JavaHelloWorldJob implements JavaSparkJobBase<JavaSparkContext> {

  @Override
  public Object runJob(JavaSparkContext jsc, Config jobConfig) {
    return("Hello World!");
  }

  @Override
  public JavaSparkJobValidation validate(JavaSparkContext jsc, Config jobConfig) {
    return JavaSparkJobValidation.SPARK_JOB_VALID;
  }
}
