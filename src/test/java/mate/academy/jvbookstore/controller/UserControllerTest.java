package mate.academy.jvbookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.jvbookstore.dto.user.UserResponseDto;
import mate.academy.jvbookstore.model.Role;
import mate.academy.jvbookstore.model.User;
import mate.academy.jvbookstore.repository.role.RoleRepository;
import mate.academy.jvbookstore.repository.user.UserRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private static MockMvc mockMvc;
    
    private static User userFromDb;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void init(@Autowired WebApplicationContext applicationContext,
            @Autowired DataSource dataSource,
            @Autowired UserRepository userRepository) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                    "db/user/create-default-test-user.sql"));
        }
        List<User> usersFromDb = userRepository.findAll();
        List<User> filtered = usersFromDb.stream()
                .filter(user -> !user.getFirstName().equals("owner"))
                .toList();
        userFromDb = filtered.get(0);
    }

    @Test
    void noMethod_WhetherOwnerHasBeenCreated_Success(
            @Autowired UserRepository userRepository) {
        List<User> usersFromDb = userRepository.findAll();
        List<User> filtered = usersFromDb.stream()
                .filter(user -> user.getFirstName().equals("owner") 
                && user.getEmail().equals("owner@bookstore.com"))
                .toList();
        Assertions.assertEquals(1, filtered.size());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void findAll_WithPageable_ListOfUsers(
            @Autowired RoleRepository roleRepository) throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        MvcResult result = mockMvc.perform(get("/users")
                .content(objectMapper.writeValueAsString(pageable))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto[].class);
        
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(2, actual.length);
        Assertions.assertEquals("owner@bookstore.com", actual[0].getEmail());
        Assertions.assertEquals(userFromDb.getEmail(), actual[1].getEmail());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateRoles_WithUserRole_UpdatedUser(
            @Autowired RoleRepository roleRepository) throws Exception {
        final Role userRole = roleRepository.findById(1L).get();
        UserResponseDto expected = new UserResponseDto();
        expected.setId(userFromDb.getId());
        expected.setEmail(userFromDb.getEmail());
        expected.setFirstName(userFromDb.getFirstName());
        expected.setLastName(userFromDb.getLastName());
        expected.setShippingAddress(userFromDb.getShippingAddress());
        List<Role> roles = new ArrayList<>();
        roles.add(userRole);
        expected.setRoles(roles);

        MvcResult result = mockMvc.perform(patch("/users/roles/" + userFromDb.getId())
                .content(objectMapper.writeValueAsString(List.of(userRole)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
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
                    new ClassPathResource("db/user/delete-test-user.sql"));
        }
    }
}
