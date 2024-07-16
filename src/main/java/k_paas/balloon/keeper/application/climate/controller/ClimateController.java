package k_paas.balloon.keeper.application.climate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/api/v1")
//@RestController
public class ClimateController {

    /**
     * 개발용 API
     *
     * 데이터 가공 작업 실행용 API
     */
//    @GetMapping("/test")
//    public void test() {
//        climateService.updateClimateData();
//    }

    /**
     * 개발용 API
     *
     * 데이터 String 작업 정상 수행 여부 확인용 API
     * @param text 실제 API 응답값
     */
//    @GetMapping("/text")
//    public void text(@RequestBody String text) {
//        climateClient.parseResponseBody(text);
//    }
}
