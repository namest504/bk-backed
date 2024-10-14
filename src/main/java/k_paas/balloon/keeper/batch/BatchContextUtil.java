package k_paas.balloon.keeper.batch;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

public class BatchContextUtil {

    public static String getCurrentBatchContext(ChunkContext chunkContext, String key) {
        return getCurrentBatchContext(getStepExecution(chunkContext), key);
    }

    public static String getCurrentBatchContext(StepExecution stepExecution, String key) {
        return (String) getExecutionContext(stepExecution).get(key);
    }

    public static void addContextData(ChunkContext chunkContext, String key, String value) {
        getExecutionContext(getStepExecution(chunkContext)).put(key, value);
    }

    private static StepExecution getStepExecution(ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution();
    }

    private static ExecutionContext getExecutionContext(StepExecution stepExecution) {
        return stepExecution.getJobExecution().getExecutionContext();
    }
}
