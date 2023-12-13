package mate.academy.jvbookstore.mapper;

import java.util.HashSet;
import mate.academy.jvbookstore.config.MapperConfig;
import mate.academy.jvbookstore.dto.cartitem.CartItemDto;
import mate.academy.jvbookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.jvbookstore.model.CartItem;
import mate.academy.jvbookstore.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart);

    CartItemDto toCartItemDto(CartItem cartItem);

    CartItem toCartItem(CartItemDto dto);

    @AfterMapping
    default void setBookIdAndTitle(@MappingTarget CartItemDto dto, CartItem cartItem) {
        dto.setBookTitle(cartItem.getBook().getTitle());
        dto.setBookId(cartItem.getBook().getId());
    }
    
    @AfterMapping
    default void setCartItemDtosAndUserId(@MappingTarget ShoppingCartDto dto, 
            ShoppingCart shoppingCart) {
        dto.setUserId(shoppingCart.getId());
        dto.setCartItems(new HashSet<>(shoppingCart.getCartItems().stream()
                .map(this::toCartItemDto)
                .toList()));
    }
}
