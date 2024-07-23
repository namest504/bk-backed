package k_paas.balloon.keeper.domain.climate.repository;

import k_paas.balloon.keeper.domain.climate.entity.Climate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClimateRepository extends MongoRepository<Climate, String> {

}
