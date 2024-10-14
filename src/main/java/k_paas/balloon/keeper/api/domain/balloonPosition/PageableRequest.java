package k_paas.balloon.keeper.api.domain.balloonPosition;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageableRequest(
        @Min(1)
        @DefaultValue("1")
        Integer page,

        @Min(1)
        @DefaultValue("10")
        Integer size,

        @DefaultValue("id")
        String sortBy,

        @DefaultValue("DESC")
        String sortDirection
) {
    public Pageable toPageable() {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return PageRequest.of(page - 1, size, sort);
    }

    public Pageable toPageable(String sortBy) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return PageRequest.of(page - 1, size, sort);
    }
}
