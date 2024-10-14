package k_paas.balloon.keeper.api.domain.climateData;

import static org.springframework.http.HttpStatus.OK;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/climate")
@Slf4j
@RequiredArgsConstructor
public class ClimateDataController {

    private final ClimateDataService climateDataService;

    @GetMapping("/data-path")
    public ResponseEntity<String> getResentBatchedCsvPath() {

        final String recentCsvFilePath = climateDataService.getRecentCsvFilePath();

        return ResponseEntity.status(OK)
                .body(recentCsvFilePath);
    }
}
