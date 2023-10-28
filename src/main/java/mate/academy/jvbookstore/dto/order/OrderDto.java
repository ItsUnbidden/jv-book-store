package mate.academy.jvbookstore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;
import mate.academy.jvbookstore.dto.orderitem.OrderItemDto;
import mate.academy.jvbookstore.model.Order.Status;

@Data
public class OrderDto {
    private Long id;

    private Long userId;

    private Set<OrderItemDto> orderItems;

    private LocalDateTime orderDate;

    private BigDecimal total;

    private Status status;
}
