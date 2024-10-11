package k_paas.balloon.keeper.batch.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import k_paas.balloon.keeper.batch.dto.UpdateClimateServiceSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClimateWriter implements ItemWriter<List<UpdateClimateServiceSpec>> {

    private static final String CSV_FILE_PATH = "climate_data.csv";

    @Override
    public void write(Chunk<? extends List<UpdateClimateServiceSpec>> climates) {
//        log.info("climates.size() : {}", climates.size());

        // 파일이 존재하지 않으면 새로 생성
        File file = new File(CSV_FILE_PATH);
        boolean isNewFile = false;

        if (!file.exists()) {
            try {
                isNewFile = file.createNewFile();
                if (isNewFile) {
                    log.info("New file created: {}", CSV_FILE_PATH);
                }
            } catch (IOException e) {
                log.error("Error creating new file: {}", CSV_FILE_PATH, e);
                return; // 파일 생성에 실패하면 더 이상 진행하지 않음
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH, false))) {
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
