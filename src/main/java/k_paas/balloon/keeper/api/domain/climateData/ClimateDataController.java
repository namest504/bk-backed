package k_paas.balloon.keeper.api.domain.climateData;

import static org.springframework.http.HttpStatus.OK;

import k_paas.balloon.keeper.global.property.ApiKeyProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/climate")
@Slf4j
@RequiredArgsConstructor
public class ClimateDataController {

    private final ClimateDataService climateDataService;
    private final ApiKeyProperty apiKeyProperty;

    @GetMapping("/data-path")
    public ResponseEntity<String> getResentBatchedCsvPath(@RequestHeader(value = "BK-API-KEY") String apiKey) {

        if (apiKey == null || !apiKey.equals(apiKeyProperty.key())) {
            throw new RuntimeException("Invalid API Key");
        }

        final String recentCsvFilePath = climateDataService.getRecentCsvFilePath();

        return ResponseEntity.status(OK)
                .body(recentCsvFilePath);
    }
}
