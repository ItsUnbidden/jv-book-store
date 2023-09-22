package mate.academy.jvbookstore.repository.book.specification;

import jakarta.persistence.criteria.Predicate;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {

    @Override
    public Specification<Book> getSpecification(String[] params) {
        final Double priceFrom;
        final Double priceTo;

        try {
            priceFrom = (params.length > 0 && params[0] != null 
                    && params[0].length() != 0) ? Double.valueOf(params[0]) : 0.0;
            priceTo = (params.length > 1 && params[1] != null 
                    && params[1].length() != 0) ? Double.valueOf(params[1]) : Double.MAX_VALUE;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameters for price: " 
                    + params + ". Parameters must be parsable to double.", e);
        }
        
        return (root, query, criteriaBuilder) -> {
            Predicate gt = criteriaBuilder.gt(root.get(getKey()), priceFrom);
            Predicate lt = criteriaBuilder.lt(root.get(getKey()), priceTo);
            return criteriaBuilder.and(gt, lt);
        };
    }

    @Override
    public String getKey() {
        return "price";
    }
}
