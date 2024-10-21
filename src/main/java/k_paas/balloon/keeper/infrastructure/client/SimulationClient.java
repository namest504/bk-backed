package k_paas.balloon.keeper.infrastructure.client;

public interface SimulationClient {

    void sendObjectPath(String path);

    void initiateLearningProcess();

    void fetchImage(SimulationImageDto simulationImageDto);
}
