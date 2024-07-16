package k_paas.balloon.keeper.batch;

import java.util.ArrayList;
import java.util.List;
import k_paas.balloon.keeper.domain.climate.entity.Climate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClimateWriter implements ItemWriter<List<Climate>> {

    private final MongoOperations mongoOperations;

    public ClimateWriter(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void write(Chunk<? extends List<Climate>> climates) {
        List<Climate> allClimates = new ArrayList<>();
        for (List<Climate> climateData : climates) {
            allClimates.addAll(climateData);
        }

        if (!allClimates.isEmpty()) {
            log.info("writer execute {} things inserted to mongoDB", allClimates.size());
            BulkOperations bulkOps = mongoOperations.bulkOps(BulkOperations.BulkMode.UNORDERED, Climate.class);
            for (Climate climate : allClimates) {
                bulkOps.insert(climate);
            }
            bulkOps.execute();
        }
    }
}
