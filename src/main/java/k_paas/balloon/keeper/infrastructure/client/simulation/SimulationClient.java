package k_paas.balloon.keeper.infrastructure.client.simulation;

public interface SimulationClient {

    void sendObjectPath(String path);

    void initiateLearningProcess();

    void fetchImage(SimulationImageDto simulationImageDto);
}
