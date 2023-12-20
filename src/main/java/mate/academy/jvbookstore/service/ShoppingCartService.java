package mate.academy.jvbookstore.service;

import mate.academy.jvbookstore.dto.cartitem.CartItemDto;
import mate.academy.jvbookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.jvbookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartByUser(User user);

    ShoppingCartDto addBookToShoppingCart(User user, CartItemDto requestDto);

    CartItemDto updateBookQuantity(Long cartItemId, CartItemDto requestDto, User user);

    void deleteBookFromShoppingCart(Long cartItemId, User user);

    void clearUserShoppingCart(User user);
}
