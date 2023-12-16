package mate.academy.jvbookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.jvbookstore.dto.cartitem.CartItemDto;
import mate.academy.jvbookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.model.CartItem;
import mate.academy.jvbookstore.model.ShoppingCart;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.repository.book.BookRepository;
import mate.academy.jvbookstore.repository.cartitem.CartItemRepository;
import mate.academy.jvbookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.jvbookstore.repository.user.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    private static MockMvc mockMvc;

    private static Book bookFromDb;
    
    private static List<CartItem> cartItems;
    
    private static ShoppingCart ownersShoppingCart;

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

        User owner = userRepository.findAll().get(0);

        ownersShoppingCart = shoppingCartRepository.findAll().get(0);
        ownersShoppingCart.setCartItems(new HashSet<>());
        ownersShoppingCart.setUser(owner);

        cartItems = new ArrayList<>();
        CartItem cartItem1 = new CartItem();
        cartItem1.setBook(bookFromDb);
        cartItem1.setQuantity(1);
        cartItem1.setShoppingCart(ownersShoppingCart);
        cartItems.add(cartItem1);
        CartItem cartItem2 = new CartItem();
        cartItem2.setBook(bookFromDb);
        cartItem2.setQuantity(2);
        cartItem2.setShoppingCart(ownersShoppingCart);   
        cartItems.add(cartItem2);
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    void getShoppingCart_NoParams_UsersShoppingCart(
            @Autowired ShoppingCartRepository shoppingCartRepository) throws Exception {
        ownersShoppingCart.getCartItems().add(cartItems.get(0));
        ownersShoppingCart.getCartItems().add(cartItems.get(1));
        shoppingCartRepository.save(ownersShoppingCart);
        
        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();
        
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);
       
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(ownersShoppingCart.getId(), actual.getId());
        Assertions.assertEquals(ownersShoppingCart.getUser().getId(), actual.getUserId());
        Assertions.assertEquals(2, actual.getCartItems().size());
    }
    
    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    void addBookToShoppingCart_WithCorrectBook_UpdatedShoppingCart() throws Exception {
        CartItemDto requestDto = new CartItemDto();
        requestDto.setBookId(bookFromDb.getId());
        requestDto.setQuantity(1);
        
        MvcResult result = mockMvc.perform(post("/cart")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);
    
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(ownersShoppingCart.getId(), actual.getId());
        Assertions.assertEquals(ownersShoppingCart.getUser().getId(), actual.getUserId());

        Assertions.assertNotNull(actual.getCartItems());
        List<CartItemDto> cartItemDtos = new ArrayList<>(actual.getCartItems());

        Assertions.assertEquals(1, cartItemDtos.size());
        Assertions.assertEquals(requestDto.getBookId(), cartItemDtos.get(0).getBookId());
        Assertions.assertEquals(bookFromDb.getTitle(), cartItemDtos.get(0).getBookTitle());
        Assertions.assertEquals(requestDto.getQuantity(), cartItemDtos.get(0).getQuantity());
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    void updateBookQuantity_WithValidCartItemIdAndQuantity_UpdatedCartItem(
            @Autowired ShoppingCartRepository shoppingCartRepository,
            @Autowired CartItemRepository cartItemRepository) throws Exception {
        CartItemDto requestDto = new CartItemDto();
        requestDto.setQuantity(5);

        ownersShoppingCart.getCartItems().add(cartItems.get(0));
        shoppingCartRepository.save(ownersShoppingCart);

        Long cartItemId = cartItemRepository.findAll().get(0).getId();

        MvcResult result = mockMvc.perform(put("/cart/cart-items/" + cartItemId)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CartItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CartItemDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(cartItemId, actual.getId());
        Assertions.assertEquals(cartItems.get(0).getBook().getId(), actual.getBookId());
        Assertions.assertEquals(cartItems.get(0).getBook().getTitle(), actual.getBookTitle());
        Assertions.assertEquals(requestDto.getQuantity(), actual.getQuantity());
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    void deleteBookFromShoppingCart_WithCorrectId_Success(
            @Autowired ShoppingCartRepository shoppingCartRepository,
            @Autowired CartItemRepository cartItemRepository) throws Exception {
        ownersShoppingCart.getCartItems().add(cartItems.get(0));
        shoppingCartRepository.save(ownersShoppingCart);

        Long cartItemId = cartItemRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/cart/cart-items/" + cartItemId))
                .andExpect(status().isNoContent())
                .andReturn();

        boolean actual = cartItemRepository.findAll().isEmpty();

        Assertions.assertTrue(actual);
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    void clearShoppingCart_NoParams_Success(
            @Autowired ShoppingCartRepository shoppingCartRepository,
            @Autowired CartItemRepository cartItemRepository) throws Exception {

        ownersShoppingCart.getCartItems().add(cartItems.get(0));
        ownersShoppingCart.getCartItems().add(cartItems.get(1));
        shoppingCartRepository.save(ownersShoppingCart);

        mockMvc.perform(delete("/cart/cart-items"))
                .andExpect(status().isNoContent())
                .andReturn();

        boolean actual = cartItemRepository.findAll().isEmpty();

        Assertions.assertTrue(actual);
    }

    @AfterEach
    void clearShoppingCart(@Autowired DataSource dataSource) throws SQLException {
        ownersShoppingCart.setCartItems(new HashSet<>());
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/shoppingcart/clear-shopping-cart.sql"));
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
                    new ClassPathResource("db/shoppingcart/clear-shopping-cart.sql"));
        }
    }
}
