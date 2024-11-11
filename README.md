# Balloon Seeker

<div align="center">

<img width="400" alt="KakaoTalk_Photo_2024-11-11-22-50-30" src="https://github.com/user-attachments/assets/91d6f159-0442-40ac-a8e0-d020d182b7b1">

[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fnamest504%2Fbk-backed&count_bg=%23718790&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)

</div>

# Balloon Seeker
> **K-pass 공모전 출품작** <br/> **개발기간: 2024.07 ~ 2024.10**

## 배포 주소

> **프론트 서버** : [https://app.tfinder.store/](https://app.tfinder.store/)<br>
> **백엔드 서버** : [https://spring.tfinder.store/](https://spring.tfinder.store/)<br>

## 백엔드 개발팀 소개

| 임승택 | 정승주 |                                                                                                              
| :-: | :-: |
| [@lim1t](https://github.com/namest504) | [@Icecoff22](https://github.com/Icecoff22) |

## 프로젝트 소개

오물풍선 낙하 위험도 AI 기반 예측 프로젝트로 K-pass 공모전 출품작입니다.

---
<!--  -->
## Stacks

### Environment
![Naver Cloud Platform](https://img.shields.io/badge/Naver%20Cloud%20Platform-03C75A?style=for-the-badge&logo=naver&logoColor=white)
![NCP K8s](https://img.shields.io/badge/NCP%20K8s-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

### Development
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white)
![MY SQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### Communication
![discord](https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white)

---
## 주요 기능

### 학습용 기상 데이터 가공
- 백그라운드 환경에서 매 6시간 마다 Spring Batch를 통한 기상 데이터 API 조회 후 CSV 파일로 가공

### AI 학습 결과에 따른 위험도 결과 조회 기능
- AI 학습 결과에 따른 특정 수준 이상의 위험도 풍선 데이터 조회 가능

### 오물 풍선 낙하 이미지 신고 기능
- 오물 풍선 낙하 식별시 이미지를 통한 직접 신고 가능

### 백그라운드 작업 수동 실행을 위한 API 제공
- 보안을 위한 헤더기반 API-KEY 검증

### Kubectl Secret 생성 간소화 Shell Script
- NCP K8s내에서 사용할 환경변수 설정 작업 간소화

---
## 트러블 슈팅

### Batch 수행시 데이터 요청시 Heap Memory OOM 발생

초기 요구사항에 따라 배치 작업 한 번에 약 1700번의 API 요청을 수행해야 했습니다.
이는 Spring Batch의 기본 싱글 스레드 처리 방식으로는 심각한 병목 현상을 일으킬 수 있는 상황이었습니다.
또한 U벡터와 V벡터 데이터를 각각 받아 하나의 데이터 셋으로 가공하는 과정에서 시간이 소요되었습니다.
더불어 API 호출 및 데이터 가공에 걸리는 시간으로 인해 Chunk 크기만큼의 데이터가 쌓이는 데 시간이 걸렸고, 이로 인해 Write 작업으로 넘어가는 데 지연이 발생했습니다.
그 결과로 API 응답 데이터와 가공된 데이터가 Write 작업으로 넘어가지 못하고 힙 메모리에 계속 누적되어 OOM이 발생하게 되었습니다.

해결 방법

ClimateReader 의 성능을 개선 할 필요가 있었습니다.
API 개별의 결과로 가공하는 것이 아닌 U,V 벡터를 조합하여 데이터셋을 만드는 과정이였으므로 
```Java
// ClimateReader
private List<ClimateDataDto> processClimateData(int altitude, String predictHour, String timeStamp) {
        CompletableFuture<String[][]> completableUVectors = sendClimateRequest(2002, altitude, predictHour, timeStamp);
        CompletableFuture<String[][]> completableVVectors = sendClimateRequest(2003, altitude, predictHour, timeStamp);

        List<ClimateDataDto> result = CompletableFuture.allOf(completableUVectors, completableVVectors)
                .thenApply(r -> {
                    String[][] uVectorArray = completableUVectors.join();
                    String[][] vVectorArray = completableVVectors.join();
                    List<ClimateDataDto> climateDataDtos = saveClimateData(altitude, uVectorArray, vVectorArray);
                    return climateDataDtos;
                }).join();
        return result;
    }
```
CompletableFuture.allOf()를 사용하여 두 API 요청이 모두 완료될 때까지 기다린 후, 결과를 합쳐 데이터 셋으로 가공하도록 하였습니다.

```Java
List<ClimateDataDto> chunk = new ArrayList<>();
while (chunk.size() < CHUNK_SIZE && !isCompleted) {
    if (buffer.isEmpty()) {
        if (currentAltitudeIndex >= ISOBARIC_ALTITUDE.length) {
            isCompleted = true;
            break;
        }
        buffer = processClimateData(currentAltitudeIndex, predictHour, timestamp);

        currentAltitudeIndex++;
    }

    int itemsToAdd = Math.min(CHUNK_SIZE - chunk.size(), buffer.size());
    chunk.addAll(buffer.subList(0, itemsToAdd));
    buffer = new ArrayList<>(buffer.subList(itemsToAdd, buffer.size()));
}

if (chunk.isEmpty()) {
    isCompleted = true;
    return null;
}
```
한 차례의 API 결과로 Chunk 사이즈에 도달할 경우 바로 Reader 작업이 끝나버리는 현상을 방지하기 위해 임의이 버퍼를 두어 점진적으로 데이터를 처리하도록 하였습니다.

### Batch 트리거 API 응답 지연으로 인한 Client 재요청 이슈

기상청 API가 실시간으로 업데이트가 되지 않아 배치가 실패하는 경우가 발생하였습니다.
이에 대한 대책으로 단순 REST API 요청을 통해 배치 수행을 수동으로 실행할 수 있도록 하였습니다.

하지만 일반적인 API로 배치 작업을 수행하도록 할 경우 배치가 완료되기 전까지는 응답을 처리해주지 못하는 상황이 발생하였습니다.

```Java
// ClimateDataController
@ValidAPIKey
@GetMapping("/batch/init")
public ResponseEntity<Void> initBatchJob(
        @ModelAttribute @Valid ClimateRunnerRequest climateRunnerRequest
) {
    climateBatchRunner.run(climateRunnerRequest);

    return ResponseEntity.status(ACCEPTED)
            .build();
}
```
이로인해 요청을 보내는 Client 측에서 요청을 재전송 하는 문제가 발생하였고 N번의 배치 작업이 동시에 수행되는 문제가 발생했습니다.

해결 방법
```Java
// ClimateBatchAsyncWrapper
@Async("threadPoolTaskExecutor")
public void runBatch(ClimateRunnerRequest request) {
    CompletableFuture.runAsync(() -> {
        climateBatchRunner.run(request);
    });
}
```
배치 작업을 CompletableFuture으로 결과를 기다리지 않고 비동기로 수행하도록 하였습니다.

---
## 아키텍쳐

### 디렉토리 구조
```bash
├── README.md
├── Dockerfile
├── build.gradle
├── db-deployment.yml
├── db-infra.yml
├── db-pv-pvc-manifest.yml
├── patchSecret.sh
├── settings.gradle
├── spring-manifest.yml
└── src/
    ├── main/
    │   ├── generated/
    │   ├── java/
    │   │   └── k_paas/
    │   │       └── balloon/
    │   │           └── keeper/
    │   │               ├── Application.java
    │   │               ├── application/
    │   │               │   ├── advice/
    │   │               │   │   ├── ErrorResponse.java
    │   │               │   │   └── RestControllerAdvice.java
    │   │               │   └── domain/
    │   │               │       ├── balloon/
    │   │               │       │   ├── BalloonController.java
    │   │               │       │   ├── BalloonService.java
    │   │               │       │   ├── BalloonServiceImpl.java
    │   │               │       │   ├── PageableRequest.java
    │   │               │       │   ├── comment/
    │   │               │       │   │   ├── dto/
    │   │               │       │   │   │   ├── BalloonCommentRequest.java
    │   │               │       │   │   │   └── BalloonCommentResponse.java
    │   │               │       │   │   └── service/
    │   │               │       │   │       └── BalloonCommentService.java
    │   │               │       │   ├── position/
    │   │               │       │   │   ├── dto/
    │   │               │       │   │   │   └── BalloonPositionResponse.java
    │   │               │       │   │   └── service/
    │   │               │       │   │       └── BalloonPositionService.java
    │   │               │       │   └── report/
    │   │               │       │       ├── dto/
    │   │               │       │       │   ├── BalloonReportDto.java
    │   │               │       │       │   ├── BalloonReportRequest.java
    │   │               │       │       │   ├── BalloonReportWithCount.java
    │   │               │       │       │   └── ReportBalloonImageCodeResponse.java
    │   │               │       │       └── service/
    │   │               │       │           ├── BalloonReportService.java
    │   │               │       │           └── ReportImageTypeValidator.java
    │   │               │       └── climateData/
    │   │               │           ├── ClimateDataController.java
    │   │               │           ├── ClimateDataPathResponse.java
    │   │               │           ├── ClimateDataService.java
    │   │               │           └── ClimateDataServiceImpl.java
    │   │               ├── batch/
    │   │               │   ├── climate/
    │   │               │   │   ├── config/
    │   │               │   │   │   └── ClimateJobConfig.java
    │   │               │   │   ├── dto/
    │   │               │   │   │   └── ClimateDataDto.java
    │   │               │   │   ├── job/
    │   │               │   │   │   ├── ClimateReader.java
    │   │               │   │   │   └── ClimateWriter.java
    │   │               │   │   ├── runner/
    │   │               │   │   │   ├── ClimateBatchRunner.java **배치 실행 클래스**
    │   │               │   │   │   └── ClimateRunnerRequest.java **배치 실행에 필요한 요청 DTO 클래스**
    │   │               │   │   └── schedule/
    │   │               │   │       └── ClimateSchedule.java
    │   │               │   └── util/
    │   │               │       ├── BatchContextUtil.java
    │   │               │       └── ClimateContants.java
    │   │               ├── global/
    │   │               │   ├── annotation/
    │   │               │   │   └── ValidAPIKey.java **API Key 검증 공통 관심사 어노테이션으로 적용**
    │   │               │   ├── async/
    │   │               │   │   ├── ClimateBatchAsyncWrapper.java **배치 트리거 실행시 비동기로 수행하기 위한 Wrapper 서비스**
    │   │               │   │   └── ClimateRequestAsyncWrapper.java **API 요청시 발생하는 I/O 병목현상 해결을 위한 위한 Wrapper 서비스**
    │   │               │   ├── config/
    │   │               │   │   ├── AsyncThreadConfig.java **스레드 풀 설정**
    │   │               │   │   ├── RestTemplateConfig.java
    │   │               │   │   ├── SwaggerConfig.java
    │   │               │   │   └── WebConfig.java
    │   │               │   ├── exception/
    │   │               │   │   ├── BkException.java **RuntimeException을 상속하는 CustomException**
    │   │               │   │   ├── InternalServiceConnectionException.java
    │   │               │   │   ├── InvalidAPIKeyException.java
    │   │               │   │   ├── NotFoundException.java
    │   │               │   │   └── UnsupportedImageTypeException.java
    │   │               │   ├── interceptor/
    │   │               │   │   └── ApiKeyInterceptor.java **API Key 검증 공통 관심사 인터셉터로 분리**
    │   │               │   ├── property/
    │   │               │   │   ├── ClimateApiKeyProperty.java
    │   │               │   │   ├── NcpProperty.java
    │   │               │   │   └── ProjectApiKeyProperty.java
    │   │               │   └── util/
    │   │               │       ├── CodeGenerateUtil.java
    │   │               │       └── TimeUtil.java
    │   │               └── infrastructure/
    │   │                   ├── client/
    │   │                   │   ├── climate/
    │   │                   │   │   ├── ClimateClient.java
    │   │                   │   │   └── ClimateClientImpl.java
    │   │                   │   └── simulation/
    │   │                   │       ├── SimulationClient.java
    │   │                   │       ├── SimulationClientImpl.java
    │   │                   │       └── SimulationImageDto.java
    │   │                   └── persistence/
    │   │                       ├── memory/
    │   │                       │   ├── ClimateDataInMemoryStore.java **최근 수집된 기상 학습 데이터 저장을 위한 구현 클래스**
    │   │                       │   └── InMemoryStore.java **InMemory DB 목적으로 사용하기 위한 추상 클래스**
    │   │                       ├── objectStorage/
    │   │                       │   └── ncp/
    │   │                       │       ├── NcpObjectStorageConfig.java
    │   │                       │       └── NcpObjectStorageService.java
    │   │                       └── repository/
    │   │                           ├── BalloonComment.java
    │   │                           ├── BalloonCommentRepository.java
    │   │                           ├── BalloonPosition.java
    │   │                           ├── BalloonPositionRepository.java
    │   │                           ├── BalloonReport.java
    │   │                           └── BalloonReportRepository.java
    │   └── resources/
    │       └── application.yml
    └── test/
        ├── java/
        │   └── k_paas/
        │       └── balloon/
        │           └── keeper/
        │               ├── ApplicationTests.java
        │               ├── application/
        │               │   └── domain/
        │               │       ├── balloon/
        │               │       │   ├── BalloonServiceImplTest.java
        │               │       │   ├── comment/
        │               │       │   │   └── service/
        │               │       │   │       └── BalloonCommentServiceTest.java
        │               │       │   ├── position/
        │               │       │   │   └── service/
        │               │       │   │       └── BalloonPositionServiceTest.java
        │               │       │   └── report/
        │               │       │       └── service/
        │               │       │           └── BalloonReportServiceTest.java
        │               │       └── climateData/
        │               └── support/
        │                   └── DomainCreateSupport.java
        └── resources/
            └── applicaiton.yml
```
