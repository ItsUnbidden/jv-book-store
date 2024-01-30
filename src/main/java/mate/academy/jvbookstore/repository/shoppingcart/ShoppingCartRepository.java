package mate.academy.jvbookstore.repository.shoppingcart;

import java.util.Optional;
import mate.academy.jvbookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @NonNull
    @Query("from ShoppingCart sc left join fetch sc.user u left "
            + "join fetch sc.cartItems ci left join fetch ci.book b where u.id = :userId")
    Optional<ShoppingCart> findByUserId(Long userId);
}
