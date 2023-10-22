package mate.academy.jvbookstore.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartItemDto {
    private Long id;

    @Min(1)
    private Long bookId;

    private String bookTitle;

    @Min(1)
    private int quantity;
}
