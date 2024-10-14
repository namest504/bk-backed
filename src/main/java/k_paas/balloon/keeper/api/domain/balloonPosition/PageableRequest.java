package k_paas.balloon.keeper.api.domain.balloonPosition;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageableRequest(
        @Min(1) @DefaultValue("1") int page,
        @Min(1) @Max(100) @DefaultValue("10") int size,
        @DefaultValue("id") String sortBy,
        @DefaultValue("desc") String sortDirection
) {
    public Pageable toPageable() {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return PageRequest.of(page - 1, size, sort);
    }
}
