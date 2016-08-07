package spark.jobserver.examples;

import com.typesafe.config.Config;
import org.apache.spark.api.java.JavaSparkContext;
import spark.jobserver.api.JobEnvironment;
import spark.jobserver.api.JobValidation;
import spark.jobserver.api.JSparkContextJob;


public class FailRunJob extends JSparkContextJob<Integer> {
    @Override
    public Integer runJob(JavaSparkContext context, JobEnvironment cfg, Config data) {
        throw new RuntimeException("Intentional Fail");
    }

    @Override
    public JobValidation validate(JavaSparkContext context, JobEnvironment jEnv, Config cfg) {
        return new JobValidation.JOB_VALID(cfg);
    }
}
