package mate.academy.jvbookstore.service;

import java.util.List;
import mate.academy.jvbookstore.dto.order.OrderDto;
import mate.academy.jvbookstore.dto.order.PlaceOrderRequestDto;
import mate.academy.jvbookstore.dto.order.UpdateStatusRequestDto;
import mate.academy.jvbookstore.dto.orderitem.OrderItemDto;
import mate.academy.jvbookstore.model.User;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(User user, PlaceOrderRequestDto requestDto);

    List<OrderDto> findAllForUser(User user, Pageable pageable);

    OrderDto updateOrderStatus(Long orderId, UpdateStatusRequestDto requestDto);

    List<OrderItemDto> findAllOrderItemsForOrderById(Long orderId, Pageable pageablem, User user);

    OrderItemDto findOrderItemByIdForOrderById(Long orderId, Long itemId, User user);
}
