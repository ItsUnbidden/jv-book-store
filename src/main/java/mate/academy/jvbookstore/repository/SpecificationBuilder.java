package mate.academy.jvbookstore.repository;

import mate.academy.jvbookstore.dto.SearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(SearchParametersDto searchParameters);
}
