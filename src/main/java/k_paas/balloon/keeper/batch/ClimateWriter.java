package k_paas.balloon.keeper.batch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
public class ClimateWriter implements ItemWriter<List<UpdateClimateServiceSpec>>, StepExecutionListener {

    private String csvFilePath;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.csvFilePath = (String) stepExecution.getJobExecution().getExecutionContext().get("csvFileName");
    }

    @Override
    public void write(Chunk<? extends List<UpdateClimateServiceSpec>> climates) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, true))) {
            for (List<UpdateClimateServiceSpec> climateData : climates) {
                for (UpdateClimateServiceSpec spec : climateData) {
                    String csvLine = convertToCSV(spec);
                    writer.write(csvLine);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            log.error("Error writing to CSV file", e);
        }
    }

    private String convertToCSV(UpdateClimateServiceSpec spec) {
        return String.format("%d,%d,%d,%s,%s",
                spec.y(),
                spec.x(),
                spec.pressure(),
                spec.uVector(),
                spec.vVector());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }
}
