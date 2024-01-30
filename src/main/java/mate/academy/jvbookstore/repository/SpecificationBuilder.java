package mate.academy.jvbookstore.repository;

import mate.academy.jvbookstore.dto.SearchParametersDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public interface SpecificationBuilder<T> {
    @NonNull
    Specification<T> build(SearchParametersDto searchParameters);
}
