package mate.academy.jvbookstore.repository.orderitem;

import java.util.List;
import java.util.Optional;
import mate.academy.jvbookstore.model.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @NonNull
    @Query("from OrderItem oi left join fetch oi.order o where o.id = :orderId")
    List<OrderItem> findByOrderId(Long orderId, Pageable pageable);

    @NonNull
    @Query("from OrderItem oi left join fetch oi.order o "
            + "where o.id = :orderId and oi.id = :itemId")
    Optional<OrderItem> findOrderItemByIdForOrderById(Long orderId, Long itemId);
}
