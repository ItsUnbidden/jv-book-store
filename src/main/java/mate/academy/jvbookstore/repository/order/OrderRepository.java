package mate.academy.jvbookstore.repository.order;

import java.util.List;
import java.util.Optional;
import mate.academy.jvbookstore.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("from Order o left join fetch o.user u "
            + "left join fetch o.orderItems oi where u.id = :userId")
    List<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderItems"})
    Optional<Order> findById(Long id);
}
