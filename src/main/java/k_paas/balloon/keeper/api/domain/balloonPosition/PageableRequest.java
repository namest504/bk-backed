package k_paas.balloon.keeper.api.domain.balloonPosition;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public record PageableRequest(
        @Min(1)
        @DefaultValue("1")
        Integer page,

        @Min(1)
        @DefaultValue("10")
        Integer size,

        @DefaultValue("id")
        String sortBy,

        @DefaultValue("desc")
        String sortDirection
) {
    public Pageable toPageable(String sortBy) {
        Sort sort = createSort(sortBy);
        return PageRequest.of(page - 1, size, sort);
    }

    private Sort createSort(String properties) {
        Sort initialSort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return initialSort.and(Sort.by(Sort.Direction.DESC, properties));
    }

    private Sort createSort(String properties, String dir) {
        Sort initialSort = Sort.by(createSortDirection(dir), sortBy);
        return initialSort.and(Sort.by(Sort.Direction.DESC, properties));
    }

    private Direction createSortDirection(String dir) {
        return Direction.valueOf(dir);
    }
}
