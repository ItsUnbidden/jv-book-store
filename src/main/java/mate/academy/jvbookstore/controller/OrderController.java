package mate.academy.jvbookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.order.OrderDto;
import mate.academy.jvbookstore.dto.order.PlaceOrderRequestDto;
import mate.academy.jvbookstore.dto.order.UpdateStatusRequestDto;
import mate.academy.jvbookstore.dto.orderitem.OrderItemDto;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Create a new order",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = OrderDto.class)),
                    responseCode = "200",
                    description = "New order"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public OrderDto placeOrder(
            Authentication authentication, 
            @RequestBody @Valid @NonNull PlaceOrderRequestDto requestDto) {
        return orderService.createOrder((User)authentication.getPrincipal(), requestDto);
    }

    @GetMapping
    @Operation(
            summary = "Get user's orders",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = OrderDto.class)),
                    responseCode = "200",
                    description = "User's orders"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public List<OrderDto> getOrders(
            Authentication authentication, 
            @Parameter(
                description = "Pagination and sorting")
            @NonNull Pageable pageable) {
        return orderService.findAllForUser((User)authentication.getPrincipal(), pageable);
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update order status",
            description = "Requires ADMIN role to access",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = OrderDto.class)),
                    responseCode = "200",
                    description = "Order with updated status"),
                @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or order not found"),
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "403",
                    description = "Forbidden")  
            }
    )
    public OrderDto updateOrderStatus(
            @Parameter(
                description = "Id of the required order")
            @PathVariable @NonNull Long orderId, 
            @RequestBody @Valid @NonNull UpdateStatusRequestDto requestDto) {
        return orderService.updateOrderStatus(orderId, requestDto);
    }

    @GetMapping("{orderId}/items")
    @Operation(
            summary = "Get all items from a specific order",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = OrderItemDto.class)),
                    responseCode = "200",
                    description = "Order items"), 
                @ApiResponse(
                    responseCode = "400",
                    description = "Order not found"),
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public List<OrderItemDto> getAllItemsFromOrder(
            Authentication authentication,
            @Parameter(
                description = "Id of the required order")
            @PathVariable @NonNull Long orderId, 
            @NonNull Pageable pageable) {
        return orderService.findAllOrderItemsForOrderById(orderId, pageable,
                (User) authentication.getPrincipal());
    }

    @GetMapping("{orderId}/items/{itemId}")
    @Operation(
            summary = "Get an item from a specific order",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = OrderItemDto.class)),
                    responseCode = "200",
                    description = "Order item"), 
                @ApiResponse(
                    responseCode = "400",
                    description = "Order or order item not found"),
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public OrderItemDto getOrderItemByIdFromOrder(
            Authentication authentication,
            @Parameter(
                description = "Id of the required order")
            @PathVariable @NonNull Long orderId,
            @Parameter(
                description = "Id of the required order item")
            @PathVariable @NonNull Long itemId) {
        return orderService.findOrderItemByIdForOrderById(orderId, itemId,
                (User)authentication.getPrincipal());
    }
}
