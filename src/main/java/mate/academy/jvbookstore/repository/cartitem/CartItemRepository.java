package mate.academy.jvbookstore.repository.cartitem;

import java.util.Optional;
import mate.academy.jvbookstore.model.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @EntityGraph(attributePaths = {"book", "shoppingCart"})
    Optional<CartItem> findById(Long id);
}
