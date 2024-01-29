package mate.academy.jvbookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.jvbookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.jvbookstore.dto.category.CategoryDto;
import mate.academy.jvbookstore.service.BookService;
import mate.academy.jvbookstore.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new category",
            description = "Requires ADMIN role to access",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CategoryDto.class)),
                    responseCode = "200",
                    description = "New category"), 
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
    public CategoryDto createCategory(@RequestBody @NonNull CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @GetMapping
    @Operation(
            summary = "Get all categories",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CategoryDto.class)),
                    responseCode = "200",
                    description = "List of categories"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public List<CategoryDto> getAll(
            @Parameter(
                    description = "Pagination and sorting")
            @NonNull Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get category by id", 
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CategoryDto.class)),
                    responseCode = "200",
                    description = "The category"),
                @ApiResponse(
                    responseCode = "400",
                    description = "Category not found"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public CategoryDto getById(
            @Parameter(
                description = "Id of the required category",
                required = true)
            @PathVariable @NonNull Long id) {
        return categoryService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update category by id", 
            description = "Requires ADMIN role to access",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CategoryDto.class)),
                    responseCode = "200",
                    description = "Updated book"), 
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
    public CategoryDto updateCategory(
            @Parameter(
                description = "Id of the required category",
                required = true)
            @PathVariable @NonNull Long id, 
            @RequestBody @NonNull CategoryDto categoryDto) {
        return categoryService.update(id, categoryDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete category by id", 
            description = "Requires ADMIN role to access",
            responses = {               
                @ApiResponse(
                    responseCode = "204"), 
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
    public void deleteCategory(
            @Parameter(
                description = "Id of the required category",
                required = true)
            @PathVariable @NonNull Long id) {
        categoryService.delete(id);
    }

    @GetMapping("/{id}/books")
    @Operation(
            summary = "Get books by category id",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CategoryDto.class)),
                    responseCode = "200",
                    description = "List of books"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            @Parameter(
                description = "Id of the required category",
                required = true)
            @PathVariable @NonNull Long id, 
            @Parameter(
                description = "Pagination and sorting")
            @NonNull Pageable pageable) {
        return bookService.findByCategoryId(id, pageable);
    }
}
