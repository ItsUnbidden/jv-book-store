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
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.jvbookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.jvbookstore.dto.category.CategoryDto;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.model.Category;
import mate.academy.jvbookstore.repository.book.BookRepository;
import mate.academy.jvbookstore.repository.category.CategoryRepository;
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
public class CategoryControllerTest {
    protected static MockMvc mockMvc;

    private static List<CategoryDto> dtos;

    private static List<Category> categoriesFromDb;
    
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void init(@Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource,
            @Autowired CategoryRepository categoryRepository) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/category/create-default-categories.sql"));
        }
        categoriesFromDb = categoryRepository.findAll();

        CategoryDto dto1 = new CategoryDto();
        dto1.setId(categoriesFromDb.get(0).getId());
        dto1.setName(categoriesFromDb.get(0).getName());
        dto1.setDescription(categoriesFromDb.get(0).getDescription());
        CategoryDto dto2 = new CategoryDto();
        dto2.setId(categoriesFromDb.get(1).getId());
        dto2.setName(categoriesFromDb.get(1).getName());
        dto2.setDescription(categoriesFromDb.get(1).getDescription());
        dtos = List.of(dto1, dto2);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:db/category/delete-new-category-after-save.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_SavedCategoryDto() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("categoryForSave");
        categoryDto.setDescription("description3");

        MvcResult result = mockMvc.perform(post("/categories")
                .content(objectMapper.writeValueAsString(categoryDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(categoryDto.getName(), actual.getName());
        Assertions.assertEquals(categoryDto.getDescription(), actual.getDescription());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getAll_WithPagable_ListOf2Categories() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        
        MvcResult result = mockMvc.perform(get("/categories")
                .content(objectMapper.writeValueAsString(pageable))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto[].class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.length);
        for (int i = 0; i < actual.length; i++) {
            Assertions.assertNotNull(actual[i].getId());
            Assertions.assertEquals(dtos.get(i).getName(), actual[i].getName());
            Assertions.assertEquals(dtos.get(i).getDescription(), actual[i].getDescription());
        }
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getById_WithValidId_CorrectCategory() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/" + dtos.get(0).getId()))
                .andExpect(status().isOk())
                .andReturn();
        
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        Assertions.assertEquals(dtos.get(0).getId(), actual.getId());
        Assertions.assertEquals(dtos.get(0).getName(), actual.getName());
        Assertions.assertEquals(dtos.get(0).getDescription(), actual.getDescription());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:db/category/delete-new-category-after-delete.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCategory_WithValidId_Success(@Autowired CategoryRepository categoryRepository) 
            throws Exception {
        Category categoryForDelete = new Category();
        categoryForDelete.setName("categoryForDelete");
        categoryRepository.save(categoryForDelete);

        mockMvc.perform(delete("/categories/" + categoryForDelete.getId()))
                .andExpect(status().isNoContent())
                .andReturn();

        List<Category> categoriesFromDb = categoryRepository.findAll();
        Assertions.assertNotNull(categoriesFromDb);
        Assertions.assertEquals(2, categoriesFromDb.size());
        Assertions.assertFalse(categoryRepository.findById(
                categoryForDelete.getId()).isPresent());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:db/category/delete-new-category-after-update.sql",
            executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_WithCorrectIdAndCategory_UpdatedCategory(
            @Autowired CategoryRepository categoryRepository) throws Exception {
        Category categoryForUpdate = new Category();
        categoryForUpdate.setName("categoryForUpdate");
        categoryForUpdate.setDescription("initialDescription");
        categoryRepository.save(categoryForUpdate);

        CategoryDto expected = new CategoryDto();
        expected.setId(categoryForUpdate.getId());
        expected.setName(categoryForUpdate.getName());
        expected.setDescription("updatedDescription");

        MvcResult result = mockMvc.perform(put("/categories/" + expected.getId())
                .content(objectMapper.writeValueAsString(expected))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getBooksByCategoryId_WithValidId_OneCorrectBook(@Autowired BookRepository bookRepository)
            throws Exception {
        final Pageable pageable = PageRequest.of(0, 10);
        
        Book book1 = new Book();
        book1.setTitle("book1WithCategory1");
        book1.setAuthor("author1");
        book1.setIsbn("0234923690");
        book1.setPrice(BigDecimal.valueOf(8.99).setScale(6));
        book1.setCategories(Set.of(categoriesFromDb.get(0), categoriesFromDb.get(1)));
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("book2WithCategory2");
        book2.setAuthor("author2");
        book2.setIsbn("2340234093");
        book2.setPrice(BigDecimal.valueOf(11.99).setScale(6));
        book2.setCategories(Set.of(categoriesFromDb.get(1)));
        bookRepository.save(book2);

        MvcResult result = mockMvc.perform(get("/categories/"
                + categoriesFromDb.get(0).getId() + "/books")
                .content(objectMapper.writeValueAsString(pageable))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();      
        
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.length);
        Assertions.assertEquals(book1.getId(), actual[0].getId());
        Assertions.assertEquals(book1.getTitle(), actual[0].getTitle());
        Assertions.assertEquals(book1.getAuthor(), actual[0].getAuthor());
        Assertions.assertEquals(book1.getIsbn(), actual[0].getIsbn());
        Assertions.assertEquals(book1.getPrice(), actual[0].getPrice());
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
                    new ClassPathResource(
                    "db/category/delete-everything-from-books-categories.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/category/delete-every-category.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/book/delete-every-book.sql"));
        }
    }
}
