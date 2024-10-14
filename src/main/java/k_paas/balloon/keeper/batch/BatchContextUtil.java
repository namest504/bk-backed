package k_paas.balloon.keeper.batch;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;

public class BatchContextUtil {

    public static String getCurrentBatchContext(ChunkContext chunkContext, String value) {
        return getCurrentBatchContext(chunkContext.getStepContext().getStepExecution(), value);
    }

    public static String getCurrentBatchContext(StepExecution stepExecution, String value) {
        return (String) stepExecution.getJobExecution().getExecutionContext().get(value);
    }

    public static void addContextData(ChunkContext chunkContext, String timestamp, String timestamp1) {
        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put(timestamp, timestamp1);
    }
}
