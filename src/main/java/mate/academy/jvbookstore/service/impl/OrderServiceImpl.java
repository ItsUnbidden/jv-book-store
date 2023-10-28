package mate.academy.jvbookstore.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.order.OrderDto;
import mate.academy.jvbookstore.dto.order.PlaceOrderRequestDto;
import mate.academy.jvbookstore.dto.order.UpdateStatusRequestDto;
import mate.academy.jvbookstore.dto.orderitem.OrderItemDto;
import mate.academy.jvbookstore.exception.EntityNotFoundException;
import mate.academy.jvbookstore.mapper.OrderMapper;
import mate.academy.jvbookstore.model.CartItem;
import mate.academy.jvbookstore.model.Order;
import mate.academy.jvbookstore.model.Order.Status;
import mate.academy.jvbookstore.model.OrderItem;
import mate.academy.jvbookstore.model.ShoppingCart;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.repository.order.OrderRepository;
import mate.academy.jvbookstore.repository.orderitem.OrderItemRepository;
import mate.academy.jvbookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.jvbookstore.service.OrderService;
import mate.academy.jvbookstore.service.ShoppingCartService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final OrderMapper orderMapper;

    private final ShoppingCartRepository shoppingCartRepository;

    private final ShoppingCartService shoppingCartService;

    @Override
    public OrderDto createOrder(User user, PlaceOrderRequestDto requestDto) {
        ShoppingCart shoppingCart = getUserShoppingCart(user);
        Order order = new Order();
        order.setOrderItems(initializeOrderItems(order, shoppingCart.getCartItems()));
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setStatus(Status.CREATED);
        order.setTotal(calculateTotalPrice(order.getOrderItems()));
        order.setUser(user);
        shoppingCartService.clearUserShoppingCart(user);
        return orderMapper.orderToDto(orderRepository.save(order));
    }

    @Override
    public List<OrderDto> findAllForUser(User user, Pageable pageable) {
        return orderRepository.findByUserId(user.getId(), pageable).stream()
                .map(orderMapper::orderToDto)
                .toList();
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, UpdateStatusRequestDto requestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> 
                new EntityNotFoundException("There is no order with id " + orderId));
        order.setStatus(requestDto.getStatus());
        return orderMapper.orderToDto(orderRepository.save(order));
    }

    @Override
    public List<OrderItemDto> findAllOrderItemsForOrderById(Long orderId, Pageable pageable) {
        return orderItemRepository.findByOrderId(orderId, pageable).stream()
                .map(orderMapper::orderItemToDto)
                .toList();
    }

    @Override
    public OrderItemDto findOrderItemByIdForOrderById(Long orderId, Long itemId) {
        return orderMapper.orderItemToDto(orderItemRepository
                .findOrderItemByIdForOrderById(orderId, itemId).orElseThrow(() -> 
                new EntityNotFoundException("There is no item with id " 
                + itemId + " within order with id " + orderId)));
    }

    private ShoppingCart getUserShoppingCart(User user) {
        return shoppingCartRepository.findByUserId(user.getId()).get();
    }

    private Set<OrderItem> initializeOrderItems(Order order, Set<CartItem> cartItems) {
        Set<OrderItem> orderItems = new HashSet<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setOrder(order);
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private BigDecimal calculateTotalPrice(Set<OrderItem> orderItems) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItems) {
            total = total.add(orderItem.getPrice()
                    .multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }
        return total;
    }
}
