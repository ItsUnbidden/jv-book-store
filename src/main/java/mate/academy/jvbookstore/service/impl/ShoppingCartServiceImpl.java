package mate.academy.jvbookstore.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.cartitem.CartItemDto;
import mate.academy.jvbookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.jvbookstore.exception.EntityNotFoundException;
import mate.academy.jvbookstore.mapper.BookMapper;
import mate.academy.jvbookstore.mapper.ShoppingCartMapper;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.model.CartItem;
import mate.academy.jvbookstore.model.Role;
import mate.academy.jvbookstore.model.Role.RoleName;
import mate.academy.jvbookstore.model.ShoppingCart;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.repository.cartitem.CartItemRepository;
import mate.academy.jvbookstore.repository.role.RoleRepository;
import mate.academy.jvbookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.jvbookstore.service.ShoppingCartService;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;

    private final ShoppingCartMapper shoppingCartMapper;

    private final BookMapper bookMapper;

    private final CartItemRepository cartItemRepository;

    private final RoleRepository roleRepository;

    @Override
    public ShoppingCartDto getShoppingCartByUser(User user) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(user.getId());
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addBookToShoppingCart(User user,
            @NonNull CartItemDto requestDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(user.getId());
        Book book = bookMapper.bookFromId(requestDto.getBookId());
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(requestDto.getQuantity());
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.getCartItems().add(cartItem);
        return shoppingCartMapper.toShoppingCartDto(shoppingCartRepository.save(shoppingCart)); 
    }

    @Override
    public CartItemDto updateBookQuantity(@NonNull Long cartItemId,
            @NonNull CartItemDto requestDto,
            User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> 
                new EntityNotFoundException("Unable to find cart item by id " + cartItemId));

        isUserAllowedToChangeItem(cartItem, user);

        cartItem.setQuantity(requestDto.getQuantity());
        return shoppingCartMapper.toCartItemDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void deleteBookFromShoppingCart(@NonNull Long cartItemId,
            User user) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> 
                new EntityNotFoundException("Unable to find cart item by id " + cartItemId));

        isUserAllowedToChangeItem(cartItem, user);

        cartItemRepository.deleteById(cartItemId);
    }

    @Override
    public void clearUserShoppingCart(User user) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(user.getId());
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            deleteBookFromShoppingCart(cartItem.getId(), user);
        }
    } 

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(
                userId).orElseThrow(() -> new EntityNotFoundException(
                "Unable to find shopping cart by user id " + userId));
    }

    private void isUserAllowedToChangeItem(CartItem cartItem, User user) {
        final Role adminRole = roleRepository.findByName(RoleName.ADMIN).get();

        if (!user.getRoles().contains(adminRole)
                && cartItem.getShoppingCart().getUser().getId() != user.getId()) {
            throw new AccessDeniedException("User is not an admin and therefore is not allowed"
                    + " to change a cart item that belongs to another user");
        }
    }
}
