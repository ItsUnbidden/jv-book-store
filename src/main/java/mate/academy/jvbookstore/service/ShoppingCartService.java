package mate.academy.jvbookstore.service;

import mate.academy.jvbookstore.dto.cartitem.CartItemDto;
import mate.academy.jvbookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.jvbookstore.model.User;
import org.springframework.lang.NonNull;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartByUser(User user);

    ShoppingCartDto addBookToShoppingCart(User user,
            @NonNull CartItemDto requestDto);

    CartItemDto updateBookQuantity(@NonNull Long cartItemId,
            @NonNull CartItemDto requestDto,
            User user);

    void deleteBookFromShoppingCart(@NonNull Long cartItemId,
            User user);

    void clearUserShoppingCart(User user);
}
