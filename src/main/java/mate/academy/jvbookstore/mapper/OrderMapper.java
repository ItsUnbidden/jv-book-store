package mate.academy.jvbookstore.mapper;

import java.util.HashSet;
import mate.academy.jvbookstore.config.MapperConfig;
import mate.academy.jvbookstore.dto.order.OrderDto;
import mate.academy.jvbookstore.dto.orderitem.OrderItemDto;
import mate.academy.jvbookstore.model.Order;
import mate.academy.jvbookstore.model.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    OrderItemDto orderItemToDto(OrderItem orderItem);

    OrderDto orderToDto(Order order);

    @AfterMapping
    default void setBookId(@MappingTarget OrderItemDto dto, OrderItem orderItem) {
        dto.setBookId(orderItem.getBook().getId());
    }

    @AfterMapping
    default void setOrderItemsAndUserId(@MappingTarget OrderDto dto, Order order) {
        dto.setUserId(order.getUser().getId());
        dto.setOrderItems(new HashSet<>(order.getOrderItems().stream()
                .map(this::orderItemToDto)
                .toList()));
    }
}
