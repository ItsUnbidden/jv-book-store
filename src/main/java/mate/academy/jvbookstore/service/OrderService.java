package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.order.OrderDto;
import mate.academy.jvbookstore.dto.order.PlaceOrderRequestDto;
import mate.academy.jvbookstore.dto.order.UpdateStatusRequestDto;
import mate.academy.jvbookstore.dto.orderitem.OrderItemDto;
import mate.academy.jvbookstore.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

public interface OrderService {
    OrderDto createOrder(User user,
            @NonNull PlaceOrderRequestDto requestDto);

    List<OrderDto> findAllForUser(User user,
            @NonNull Pageable pageable);

    OrderDto updateOrderStatus(@NonNull Long orderId,
            @NonNull UpdateStatusRequestDto requestDto);

    List<OrderItemDto> findAllOrderItemsForOrderById(@NonNull Long orderId,
            @NonNull Pageable pageable,
            User user);

    OrderItemDto findOrderItemByIdForOrderById(@NonNull Long orderId,
            @NonNull Long itemId, 
            User user);
}
