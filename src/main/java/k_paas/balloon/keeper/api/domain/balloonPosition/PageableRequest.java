package k_paas.balloon.keeper.api.domain.balloonPosition;

import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageableRequest(
        @Min(0)
        Integer page,
        @Min(1)
        Integer size,
        String sortBy,
        String sortDirection
) {
    public Pageable toPageable() {
        String effectiveSortBy = sortBy != null && !sortBy.isEmpty() ? sortBy : "createdAt";
        Sort.Direction direction = Sort.Direction.fromString(sortDirection != null ? sortDirection : "DESC");
        Sort sort = Sort.by(direction, effectiveSortBy);
        return PageRequest.of(page != null ? page : 0, size != null ? size : 10, sort);
    }
}
