package mate.academy.jvbookstore.dto.shoppingcart;

import java.util.Set;
import lombok.Data;
import mate.academy.jvbookstore.dto.cartitem.CartItemDto;

@Data
public class ShoppingCartDto {
    private Long id;

    private Long userId;

    private Set<CartItemDto> cartItems;
}
