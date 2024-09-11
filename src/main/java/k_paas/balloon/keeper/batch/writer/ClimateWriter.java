package k_paas.balloon.keeper.batch.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import k_paas.balloon.keeper.batch.dto.UpdateClimateServiceSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClimateWriter implements ItemWriter<List<UpdateClimateServiceSpec>> {

    private static final String CSV_FILE_PATH = "climate_data.csv";

    @Override
    public void write(Chunk<? extends List<UpdateClimateServiceSpec>> climates) {
        log.info("climates.size() : {}", climates.size());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH, true))) {
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
}
