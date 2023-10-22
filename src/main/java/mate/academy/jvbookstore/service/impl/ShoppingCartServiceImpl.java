package mate.academy.jvbookstore.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.cartitem.CartItemDto;
import mate.academy.jvbookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.jvbookstore.exception.EntityNotFoundException;
import mate.academy.jvbookstore.mapper.BookMapper;
import mate.academy.jvbookstore.mapper.ShoppingCartMapper;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.model.CartItem;
import mate.academy.jvbookstore.model.ShoppingCart;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.repository.cartitem.CartItemRepository;
import mate.academy.jvbookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.jvbookstore.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;

    private final ShoppingCartMapper shoppingCartMapper;

    private final BookMapper bookMapper;

    private final CartItemRepository cartItemRepository;

    @Override
    public ShoppingCartDto getShoppingCartByUser(User user) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(user.getId());
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(User user, CartItemDto requestDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(user.getId());
        Book book = bookMapper.bookFromId(requestDto.getBookId());
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(requestDto.getQuantity());
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.getCartItems().add(cartItemRepository.save(cartItem));
        return shoppingCartMapper.toShoppingCartDto(shoppingCartRepository.save(shoppingCart)); 
    }

    @Override
    public CartItemDto updateBookQuantity(Long cartItemId, CartItemDto requestDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> 
                new EntityNotFoundException("Unable to find cart item by id " + cartItemId));
        cartItem.setQuantity(requestDto.getQuantity());
        return shoppingCartMapper.toCartItemDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteBookFromShoppingCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);;
    }

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(
                userId).orElseThrow(() -> new EntityNotFoundException(
                "Unable to find shopping cart by user id " + userId));
    } 
}
