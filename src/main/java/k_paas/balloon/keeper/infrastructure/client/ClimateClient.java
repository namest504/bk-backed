package k_paas.balloon.keeper.infrastructure.client;

public interface ClimateClient {
    String[][] climateApiResponse(String varn, String level, String predictHour);
}
