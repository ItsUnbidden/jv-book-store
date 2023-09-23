package mate.academy.jvbookstore.repository.book.specification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.repository.SpecificationProvider;
import mate.academy.jvbookstore.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> specificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(sp -> sp.getKey()
                .equals(key))
                .findFirst()
                .orElseThrow(() -> 
                new RuntimeException("Can't find a specification provider by key " + key));
    }
}
