package mate.academy.jvbookstore.repository.book.specification;

import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.BookSearchParametersDto;
import mate.academy.jvbookstore.dto.SearchParametersDto;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.repository.SpecificationBuilder;
import mate.academy.jvbookstore.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specProviderManager;

    @Override
    public Specification<Book> build(SearchParametersDto searchParameters) {
        Specification<Book> spec = Specification.where(null);
        BookSearchParametersDto bookSearchParameters = (BookSearchParametersDto) searchParameters;

        if (bookSearchParameters.getAuthors() != null 
                && bookSearchParameters.getAuthors().length != 0) {
            spec = spec.and(specProviderManager.getSpecificationProvider("author")
                    .getSpecification(bookSearchParameters.getAuthors()));
        }

        if (bookSearchParameters.getPrice() != null 
                && bookSearchParameters.getPrice().length != 0) {
            spec = spec.and(specProviderManager.getSpecificationProvider("price")
                    .getSpecification(bookSearchParameters.getPrice()));
        }
        return spec;
    }
}
