package mate.academy.jvbookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.cartitem.CartItemDto;
import mate.academy.jvbookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User's Shopping Cart Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    @Operation(
            summary = "Get user's shopping cart",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ShoppingCartDto.class)),
                    responseCode = "200",
                    description = "User's shopping cart"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCartByUser((User)authentication.getPrincipal());
    }

    @PostMapping
    @Operation(
            summary = "Add a book to user's shopping cart",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ShoppingCartDto.class)),
                    responseCode = "200",
                    description = "User's shopping cart"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public ShoppingCartDto addBookToShoppingCart(
            Authentication authentication,
            @RequestBody @Valid @NonNull CartItemDto requestDto) {
        return shoppingCartService.addBookToShoppingCart(
                (User)authentication.getPrincipal(), requestDto);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @Operation(
            summary = "Change quantity of books in user's shopping cart",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CartItemDto.class)),
                    responseCode = "200",
                    description = "Cart item with updated quantity of books"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public CartItemDto updateBookQuantity(
            Authentication authentication,
            @Parameter(
                description = "Id of the required cart item"
            )
            @PathVariable @NonNull Long cartItemId, 
            @RequestBody @Valid @NonNull CartItemDto requestDto) {
        return shoppingCartService.updateBookQuantity(cartItemId, requestDto,
                (User)authentication.getPrincipal());
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete book from shopping cart by id",             
            responses = {               
                @ApiResponse(
                    responseCode = "204"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public void deleteBookFromShoppingCart(
            Authentication authentication,
            @Parameter(
                description = "Id of the required cart item"
            )
            @PathVariable @NonNull Long cartItemId) {
        shoppingCartService.deleteBookFromShoppingCart(cartItemId,
                (User)authentication.getPrincipal());
    }

    @DeleteMapping("/cart-items")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete all books from shopping cart",             
            responses = {               
                @ApiResponse(
                    responseCode = "204"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public void clearShoppingCart(Authentication authentication) {
        shoppingCartService.clearUserShoppingCart((User)authentication.getPrincipal());
    }
}
