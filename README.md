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

### Kubectl Secret 생성 간소화 Shell Script
- NCP K8s내에서 사용할 환경변수 설정 작업 간소화

---
## 트러블 슈팅

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
