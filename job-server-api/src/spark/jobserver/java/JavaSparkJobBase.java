package spark.jobserver.java;

import com.typesafe.config.Config;
import org.apache.spark.api.java.JavaSparkContext;

public interface JavaSparkJobBase<C extends JavaSparkContext> {
    Object runJob(C jsc, Config jobConfig);
    JavaSparkJobValidation validate(C jsc, Config jobConfig);
}
