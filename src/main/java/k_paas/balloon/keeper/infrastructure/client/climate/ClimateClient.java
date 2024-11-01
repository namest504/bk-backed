package k_paas.balloon.keeper.infrastructure.client.climate;

public interface ClimateClient {
    String[][] fetchGetClimateData(String varn, String level, String predictHour, String timeStamp);
}
