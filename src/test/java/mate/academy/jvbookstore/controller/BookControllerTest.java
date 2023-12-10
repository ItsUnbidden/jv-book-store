package mate.academy.jvbookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.jvbookstore.dto.book.BookDto;
import mate.academy.jvbookstore.dto.book.BookSearchParametersDto;
import mate.academy.jvbookstore.dto.book.CreateBookRequestDto;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.repository.book.BookRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;

    private static List<BookDto> dtos;
    
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void init(@Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource,
            @Autowired BookRepository bookRepository) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/book/create-default-books.sql"));
        }
        
        List<Book> booksFromDb = bookRepository.findAll();

        BookDto dto1 = new BookDto();
        dto1.setId(booksFromDb.get(0).getId());
        dto1.setTitle(booksFromDb.get(0).getTitle());
        dto1.setAuthor(booksFromDb.get(0).getAuthor());
        dto1.setIsbn(booksFromDb.get(0).getIsbn());
        dto1.setPrice(booksFromDb.get(0).getPrice());
        dto1.setCategoryIds(new HashSet<>());
        dto1.setDescription(booksFromDb.get(0).getDescription());
        dto1.setCoverImage(booksFromDb.get(0).getCoverImage());
        
        BookDto dto2 = new BookDto();
        dto2.setId(booksFromDb.get(1).getId());
        dto2.setTitle(booksFromDb.get(1).getTitle());
        dto2.setAuthor(booksFromDb.get(1).getAuthor());
        dto2.setIsbn(booksFromDb.get(1).getIsbn());
        dto2.setPrice(booksFromDb.get(1).getPrice());
        dto2.setCategoryIds(new HashSet<>());
        dto2.setDescription(booksFromDb.get(1).getDescription());
        dto2.setCoverImage(booksFromDb.get(1).getCoverImage());

        BookDto dto3 = new BookDto();
        dto3.setId(booksFromDb.get(2).getId());
        dto3.setTitle(booksFromDb.get(2).getTitle());
        dto3.setAuthor(booksFromDb.get(2).getAuthor());
        dto3.setIsbn(booksFromDb.get(2).getIsbn());
        dto3.setPrice(booksFromDb.get(2).getPrice());
        dto3.setCategoryIds(new HashSet<>());
        dto3.setDescription(booksFromDb.get(2).getDescription());
        dto3.setCoverImage(booksFromDb.get(2).getCoverImage());
        dtos = List.of(dto1, dto2, dto3);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:db/book/delete-new-book-after-save.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_SavedBookDto() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setAuthor("author4");
        requestDto.setTitle("title4");
        requestDto.setIsbn("3242324900");
        requestDto.setPrice(BigDecimal.valueOf(8.99));
        requestDto.setCategoryIds(new HashSet<>());
        requestDto.setDescription("description4");
        requestDto.setCoverImage("coverImageURL4");

        BookDto bookDto = new BookDto();
        bookDto.setAuthor(requestDto.getAuthor());
        bookDto.setTitle(requestDto.getTitle());
        bookDto.setIsbn(requestDto.getIsbn());
        bookDto.setPrice(requestDto.getPrice());
        bookDto.setCategoryIds(requestDto.getCategoryIds());
        bookDto.setDescription(requestDto.getDescription());
        bookDto.setCoverImage(requestDto.getCoverImage());

        MvcResult result = mockMvc.perform(post("/books")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(bookDto.getTitle(), actual.getTitle());
        Assertions.assertEquals(bookDto.getAuthor(), actual.getAuthor());
        Assertions.assertEquals(bookDto.getIsbn(), actual.getIsbn());
        Assertions.assertEquals(bookDto.getCategoryIds(), actual.getCategoryIds());
        Assertions.assertEquals(bookDto.getPrice(), actual.getPrice());
        Assertions.assertEquals(bookDto.getDescription(), actual.getDescription());
        Assertions.assertEquals(bookDto.getCoverImage(), actual.getCoverImage());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAll_WithPagable_ListOf3Books() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        
        MvcResult result = mockMvc.perform(get("/books")
                .content(objectMapper.writeValueAsString(pageable))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDto[].class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(3, actual.length);
        for (int i = 0; i < actual.length; i++) {
            Assertions.assertNotNull(actual[i].getId());
            Assertions.assertEquals(dtos.get(i).getTitle(), actual[i].getTitle());
            Assertions.assertEquals(dtos.get(i).getAuthor(), actual[i].getAuthor());
            Assertions.assertEquals(dtos.get(i).getIsbn(), actual[i].getIsbn());
            Assertions.assertEquals(dtos.get(i).getCategoryIds(), actual[i].getCategoryIds());
            Assertions.assertEquals(dtos.get(i).getPrice(), actual[i].getPrice());
            Assertions.assertEquals(dtos.get(i).getDescription(), actual[i].getDescription());
            Assertions.assertEquals(dtos.get(i).getCoverImage(), actual[i].getCoverImage());
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getBookById_WithValidId_CorrectBook() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/" + dtos.get(0).getId()))
                .andExpect(status().isOk())
                .andReturn();
        
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        Assertions.assertEquals(dtos.get(0).getId(), actual.getId());
        Assertions.assertEquals(dtos.get(0).getTitle(), actual.getTitle());
        Assertions.assertEquals(dtos.get(0).getAuthor(), actual.getAuthor());
        Assertions.assertEquals(dtos.get(0).getIsbn(), actual.getIsbn());
        Assertions.assertEquals(dtos.get(0).getCategoryIds(), actual.getCategoryIds());
        Assertions.assertEquals(dtos.get(0).getPrice(), actual.getPrice());
        Assertions.assertEquals(dtos.get(0).getDescription(), actual.getDescription());
        Assertions.assertEquals(dtos.get(0).getCoverImage(), actual.getCoverImage());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:db/book/delete-new-book-after-delete.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBoook_WithValidId_Success(@Autowired BookRepository bookRepository)
            throws Exception {
        Book bookForDelete = new Book();
        bookForDelete.setTitle("bookForDelete");
        bookForDelete.setAuthor("author1");
        bookForDelete.setIsbn("234923493");
        bookForDelete.setPrice(BigDecimal.valueOf(6.99));
        bookForDelete.setCategories(new HashSet<>());

        bookRepository.save(bookForDelete);

        mockMvc.perform(delete("/books/" + bookForDelete.getId()))
                .andExpect(status().isNoContent())
                .andReturn();

        List<Book> booksFromDb = bookRepository.findAll();
        Assertions.assertNotNull(booksFromDb);
        Assertions.assertEquals(3, booksFromDb.size());
        Assertions.assertFalse(bookRepository.findById(
                bookForDelete.getId()).isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:db/book/delete-new-book-after-update.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_WithCorrectIdAndBook_UpdatedBook(
            @Autowired BookRepository bookRepository) throws Exception {
        Book bookForUpdate = new Book();
        bookForUpdate.setTitle("bookForUpdate");
        bookForUpdate.setAuthor("author1");
        bookForUpdate.setIsbn("923467293");
        bookForUpdate.setPrice(BigDecimal.valueOf(8.99));
        bookForUpdate.setCategories(new HashSet<>());
        bookRepository.save(bookForUpdate);

        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(bookForUpdate.getTitle());
        requestDto.setAuthor("updatedAuthor");
        requestDto.setIsbn(bookForUpdate.getIsbn());
        requestDto.setPrice(bookForUpdate.getPrice());
        requestDto.setCategoryIds(new HashSet<>());

        BookDto expected = new BookDto();
        expected.setId(bookForUpdate.getId());
        expected.setTitle(bookForUpdate.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(bookForUpdate.getIsbn());
        expected.setPrice(bookForUpdate.getPrice());
        expected.setCategoryIds(requestDto.getCategoryIds());

        MvcResult result = mockMvc.perform(put("/books/" + bookForUpdate.getId())
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void searchBooks_PriceBetween10And15Author1_ListWithExpectedBook() throws Exception {
        BookSearchParametersDto searchParametersDto = new BookSearchParametersDto();
        searchParametersDto.setAuthors(new String[]{"author1"});
        searchParametersDto.setPrice(new String[]{"10", "15"});

        MvcResult result = mockMvc.perform(get("/books/search")
                .param("authors", searchParametersDto.getAuthors())
                .param("price", searchParametersDto.getPrice()))
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto[].class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.length);
        Assertions.assertEquals(dtos.get(2), actual[0]);
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
    
}
