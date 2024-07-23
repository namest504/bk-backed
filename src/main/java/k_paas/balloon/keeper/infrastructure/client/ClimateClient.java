package k_paas.balloon.keeper.infrastructure.client;

public interface ClimateClient {
    String[][] sendRequestClimateData(String varn, String level, String predictHour);
}
