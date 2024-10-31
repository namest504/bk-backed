package k_paas.balloon.keeper.batch.climate.job;

import k_paas.balloon.keeper.batch.climate.dto.ClimateDataDto;
import k_paas.balloon.keeper.batch.util.BatchContextUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@Getter
public class ClimateWriter implements ItemWriter<List<ClimateDataDto>>, StepExecutionListener {

    private String csvFilePath;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.csvFilePath = BatchContextUtil.getCurrentBatchContext(stepExecution, "csvFileName");
    }

    @Override
    public void write(Chunk<? extends List<ClimateDataDto>> climates) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, true))) {
            for (List<ClimateDataDto> climateData : climates) {
                for (ClimateDataDto spec : climateData) {
                    String csvLine = convertToCSV(spec);
                    writer.write(csvLine);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            log.error("Error writing to CSV file", e);
        }
    }

    private String convertToCSV(ClimateDataDto spec) {
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
