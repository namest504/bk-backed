package k_paas.balloon.keeper.batch.processor;

import java.util.List;
import k_paas.balloon.keeper.batch.dto.UpdateClimateServiceSpec;
import k_paas.balloon.keeper.domain.climate.entity.Climate;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ClimateProcessor implements ItemProcessor<List<UpdateClimateServiceSpec>, List<Climate>> {

    @Override
    public List<Climate> process(List<UpdateClimateServiceSpec> items) {
        return items.stream()
                .map(m -> m.toEntity())
                .toList();
    }
}
