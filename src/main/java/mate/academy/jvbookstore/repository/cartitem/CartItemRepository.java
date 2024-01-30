package mate.academy.jvbookstore.repository.cartitem;

import java.util.Optional;
import mate.academy.jvbookstore.model.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @NonNull
    @EntityGraph(attributePaths = {"book", "shoppingCart"})
    Optional<CartItem> findById(@NonNull Long id);
}
