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
import java.util.Set;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
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
                    new ClassPathResource("db/book/create-book-for-cart-items.sql"));
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
    @Transactional
    @Rollback
    void getShoppingCart_NoParams_UsersShoppingCart(
            @Autowired ShoppingCartRepository shoppingCartRepository) throws Exception {
        ShoppingCartDto expected = addCartItemsToDb(shoppingCartRepository);
        MvcResult result = mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andReturn();
        
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);
       
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }
    
    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    @Transactional
    @Rollback
    void addBookToShoppingCart_WithCorrectBook_UpdatedShoppingCart(
            @Autowired CartItemRepository cartItemRepository) throws Exception {
        CartItemDto requestDto = new CartItemDto();
        requestDto.setBookId(bookFromDb.getId());
        requestDto.setQuantity(1);
        
        MvcResult result = mockMvc.perform(post("/cart")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        final ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);

        List<CartItem> cartItemsFromDb = cartItemRepository.findAll();
        Assertions.assertEquals(1, cartItemsFromDb.size());
        CartItem cartItemFromDb = cartItemsFromDb.get(0);
        CartItemDto expectedCartItem = new CartItemDto();
        expectedCartItem.setId(cartItemFromDb.getId());
        expectedCartItem.setBookId(cartItemFromDb.getBook().getId());
        expectedCartItem.setBookTitle(cartItemFromDb.getBook().getTitle());
        expectedCartItem.setQuantity(cartItemFromDb.getQuantity());
        Set<CartItemDto> cartItemDtosSet = new HashSet<>();
        cartItemDtosSet.add(expectedCartItem);

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(ownersShoppingCart.getId());
        expected.setUserId(ownersShoppingCart.getUser().getId());
        expected.setCartItems(cartItemDtosSet);
    
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    @Transactional
    @Rollback
    void updateBookQuantity_WithValidCartItemIdAndQuantity_UpdatedCartItem(
            @Autowired ShoppingCartRepository shoppingCartRepository,
            @Autowired CartItemRepository cartItemRepository) throws Exception {
        CartItemDto requestDto = new CartItemDto();
        requestDto.setQuantity(5);

        CartItemDto expected = addCartItemsToDb(shoppingCartRepository).getCartItems().stream()
                .limit(1)
                .toList()
                .get(0);
        expected.setQuantity(5);

        Long cartItemId = cartItemRepository.findAll().get(0).getId();

        MvcResult result = mockMvc.perform(put("/cart/cart-items/" + cartItemId)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CartItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CartItemDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithUserDetails(value = "owner@bookstore.com")
    @Transactional
    @Rollback
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
    @Transactional
    @Rollback
    void clearShoppingCart_NoParams_Success(
            @Autowired ShoppingCartRepository shoppingCartRepository,
            @Autowired CartItemRepository cartItemRepository) throws Exception {
        addCartItemsToDb(shoppingCartRepository);

        mockMvc.perform(delete("/cart/cart-items"))
                .andExpect(status().isNoContent())
                .andReturn();

        boolean actual = cartItemRepository.findAll().isEmpty();

        Assertions.assertTrue(actual);
    }

    @AfterEach
    void clearShoppingCart() {
        ownersShoppingCart.setCartItems(new HashSet<>());
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
        }
    }

    private ShoppingCartDto addCartItemsToDb(ShoppingCartRepository shoppingCartRepository) {
        ownersShoppingCart.getCartItems().add(cartItems.get(0));
        ownersShoppingCart.getCartItems().add(cartItems.get(1));
        ShoppingCart shoppingCart = shoppingCartRepository.save(ownersShoppingCart);
        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setId(shoppingCart.getId());
        dto.setUserId(shoppingCart.getUser().getId());
        dto.setCartItems(new HashSet<>(shoppingCart.getCartItems().stream()
                .map(ci -> {
                    CartItemDto cartItemDto = new CartItemDto();
                    cartItemDto.setId(ci.getId());
                    cartItemDto.setBookId(ci.getBook().getId());
                    cartItemDto.setBookTitle(ci.getBook().getTitle());
                    cartItemDto.setQuantity(ci.getQuantity());
                    return cartItemDto;
                })
                .toList()));
        return dto;
    }
}
