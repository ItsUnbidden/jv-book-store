package mate.academy.jvbookstore.dto.orderitem;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;

    private Long bookId;

    private int quantity;
}
