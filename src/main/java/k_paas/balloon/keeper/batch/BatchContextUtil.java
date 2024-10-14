package k_paas.balloon.keeper.batch;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;

public class BatchContextUtil {

    public static String getCurrentCsvFileName(ChunkContext chunkContext) {
        return (String) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("csvFileName");
    }

    public static String getCurrentCsvFileName(StepExecution stepExecution) {
        return (String) stepExecution.getJobExecution().getExecutionContext().get("csvFileName");
    }

    public static void addContextData(ChunkContext chunkContext, String timestamp, String timestamp1) {
        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put(timestamp, timestamp1);
    }
}
