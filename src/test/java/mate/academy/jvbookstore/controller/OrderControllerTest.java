package mate.academy.jvbookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.jvbookstore.dto.order.OrderDto;
import mate.academy.jvbookstore.dto.order.PlaceOrderRequestDto;
import mate.academy.jvbookstore.dto.order.UpdateStatusRequestDto;
import mate.academy.jvbookstore.dto.orderitem.OrderItemDto;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.model.CartItem;
import mate.academy.jvbookstore.model.Order;
import mate.academy.jvbookstore.model.OrderItem;
import mate.academy.jvbookstore.model.ShoppingCart;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.repository.book.BookRepository;
import mate.academy.jvbookstore.repository.order.OrderRepository;
import mate.academy.jvbookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.jvbookstore.repository.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OrderControllerTest {
    private static MockMvc mockMvc;

    private static Book bookFromDb;

    private static User owner;

    private static List<OrderItem> orderItems;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void init(@Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource,
            @Autowired ShoppingCartRepository shoppingCartRepository,
            @Autowired BookRepository bookRepository,
            @Autowired UserRepository userRepository) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/user/create-book-for-cart-items.sql"));
        }
        bookFromDb = bookRepository.findAll().get(0);

        owner = userRepository.findAll().get(0);
    }

    @BeforeEach
    void recreateOrderItems() {
        orderItems = new ArrayList<>();
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setBook(bookFromDb);
        orderItem1.setPrice(bookFromDb.getPrice());
        orderItem1.setQuantity(1);
        orderItems.add(orderItem1);
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setBook(bookFromDb);
        orderItem2.setPrice(bookFromDb.getPrice());
        orderItem2.setQuantity(2);
        orderItems.add(orderItem2);
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    @Sql(scripts = "classpath:db/shoppingcart/clear-shopping-cart.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void placeOrder_WithTwoCartItems_CreatedOrder(
            @Autowired ShoppingCartRepository shoppingCartRepository) throws Exception {
        ShoppingCart ownersShoppingCart = shoppingCartRepository.findAll().get(0);
        ownersShoppingCart.setUser(owner);

        CartItem cartItem1 = new CartItem();
        cartItem1.setBook(bookFromDb);
        cartItem1.setQuantity(1);
        cartItem1.setShoppingCart(ownersShoppingCart);
        CartItem cartItem2 = new CartItem();
        cartItem2.setBook(bookFromDb);
        cartItem2.setQuantity(2);
        cartItem2.setShoppingCart(ownersShoppingCart);   
        
        ownersShoppingCart.setCartItems(new HashSet<>());
        ownersShoppingCart.getCartItems().add(cartItem1);
        ownersShoppingCart.getCartItems().add(cartItem2);
        shoppingCartRepository.save(ownersShoppingCart);

        PlaceOrderRequestDto requestDto = new PlaceOrderRequestDto();
        requestDto.setShippingAddress("SA1");

        MvcResult result = mockMvc.perform(post("/orders")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(ownersShoppingCart.getUser().getId(), actual.getUserId());
        Assertions.assertTrue(actual.getOrderDate().isBefore(LocalDateTime.now()));
        Assertions.assertEquals(Order.Status.CREATED, actual.getStatus());
        Assertions.assertEquals(2, actual.getOrderItems().size());
        BigDecimal expectedTotalPrice = BigDecimal.ZERO;      
        expectedTotalPrice = expectedTotalPrice.add(cartItem1.getBook()
                .getPrice()
                .multiply(BigDecimal.valueOf(cartItem1.getQuantity())))
                .add(cartItem2.getBook()
                .getPrice()
                .multiply(BigDecimal.valueOf(cartItem2.getQuantity())));      
        Assertions.assertEquals(0, expectedTotalPrice.compareTo(actual.getTotal()));
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    void getOrders_WithPageable_ListOfOneOrder(
            @Autowired OrderRepository orderRepository) throws Exception {
        Order expected = addOrderToDb(orderRepository);

        Pageable pageable = PageRequest.of(0, 10);

        MvcResult result = mockMvc.perform(get("/orders")
                .content(objectMapper.writeValueAsString(pageable))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto[].class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.length);
        Assertions.assertEquals(expected.getId(), actual[0].getId());
        Assertions.assertEquals(expected.getUser().getId(), actual[0].getUserId());
        Assertions.assertEquals(expected.getStatus(), actual[0].getStatus());
        Assertions.assertEquals(expected.getTotal(), actual[0].getTotal());
        Assertions.assertEquals(2, actual[0].getOrderItems().size());
        Assertions.assertEquals(new HashSet<>(getOrderItemDtos(expected)),
                actual[0].getOrderItems());
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    void updateOrderStatus_ChangeToCompleted_UpdatedOrder(
            @Autowired OrderRepository orderRepository) throws Exception {
        Order expected = addOrderToDb(orderRepository);

        UpdateStatusRequestDto requestDto = new UpdateStatusRequestDto();
        requestDto.setStatus(Order.Status.COMPLETED);

        MvcResult result = mockMvc.perform(patch("/orders/" + expected.getId())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(Order.Status.COMPLETED, actual.getStatus());
        Assertions.assertEquals(expected.getTotal(), actual.getTotal());
        Assertions.assertEquals(expected.getUser().getId(), actual.getUserId());
        Assertions.assertEquals(new HashSet<>(getOrderItemDtos(expected)), actual.getOrderItems());
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    void getAllItemsFromOrder_FirstOrderWithPageable_ListOfTwoItems(
            @Autowired OrderRepository orderRepository
    ) throws Exception {
        Order order = addOrderToDb(orderRepository);
        Pageable pageable = PageRequest.of(0, 10);

        MvcResult result = mockMvc.perform(get("/orders/" + order.getId() + "/items")
                .content(objectMapper.writeValueAsString(pageable))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        OrderItemDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderItemDto[].class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.length);
        Assertions.assertEquals(getOrderItemDtos(order), new HashSet<>(Arrays.asList(actual)));
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    void getOrderItemByIdFromOrder_WithCorrectIds_CorrectOrderItem(
            @Autowired OrderRepository orderRepository
    ) throws Exception {
        Order order = addOrderToDb(orderRepository);

        ArrayList<OrderItemDto> dtos = new ArrayList<>(getOrderItemDtos(order));

        MvcResult result = mockMvc.perform(get("/orders/" + order.getId() + "/items/"
                + dtos.get(0).getId()))
                .andExpect(status().isOk())
                .andReturn();

        OrderItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), OrderItemDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(dtos.get(0), actual);
    }

    @AfterEach
    void deleteAllEntities(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, 
                    new ClassPathResource("db/order/delete-all-order-items.sql"));
            ScriptUtils.executeSqlScript(connection, 
                    new ClassPathResource("db/order/delete-all-orders.sql"));
        }
    }

    @AfterAll
    static void clearDb(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/book/delete-every-book.sql"));
            ScriptUtils.executeSqlScript(connection, 
                    new ClassPathResource("db/order/delete-all-order-items.sql"));
            ScriptUtils.executeSqlScript(connection, 
                    new ClassPathResource("db/order/delete-all-orders.sql"));
        }
    }

    private BigDecimal calculateTotalPrice(Set<OrderItem> orderItems) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItems) {
            total = total.add(orderItem.getPrice()
                    .multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }
        return total;
    }

    private Order addOrderToDb(@Autowired OrderRepository orderRepository) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress("SA1");
        order.setStatus(Order.Status.CREATED);
        order.setUser(owner);
        orderItems.get(0).setOrder(order);
        orderItems.get(1).setOrder(order);
        order.setOrderItems(new HashSet<>(orderItems));
        order.setTotal(calculateTotalPrice(order.getOrderItems()));
        return orderRepository.save(order);
    }

    private Set<OrderItemDto> getOrderItemDtos(Order order) {
        List<OrderItemDto> dtos = order.getOrderItems().stream()
                .map(oi -> {
                    OrderItemDto orderItemDto = new OrderItemDto();
                    orderItemDto.setId(oi.getId());
                    orderItemDto.setBookId(oi.getBook().getId());
                    orderItemDto.setQuantity(oi.getQuantity());
                    return orderItemDto;
                })
                .toList();
        return new HashSet<>(dtos);
    }
}
