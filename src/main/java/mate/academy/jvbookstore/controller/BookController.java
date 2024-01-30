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
import mate.academy.jvbookstore.dto.book.BookDto;
import mate.academy.jvbookstore.dto.book.BookSearchParametersDto;
import mate.academy.jvbookstore.dto.book.CreateBookRequestDto;
import mate.academy.jvbookstore.service.BookService;
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

@Tag(name = "Book Management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(
            summary = "Get all books",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookDto.class)),
                    responseCode = "200",
                    description = "List of books"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public List<BookDto> getAll(
            @Parameter(
                description = "Pagination and sorting") 
            @NonNull Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new book",
            description = "Requires ADMIN role to access",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookDto.class)),
                    responseCode = "200",
                    description = "New book"), 
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
    public BookDto createBook(@RequestBody @Valid @NonNull CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get book by id", 
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookDto.class)),
                    responseCode = "200",
                    description = "The book"),
                @ApiResponse(
                    responseCode = "400",
                    description = "Book not found"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public BookDto getBookById(
            @Parameter(
                description = "Id of the required book",
                required = true)
            @NonNull @PathVariable Long id) {
        return bookService.findById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete book by id", 
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
    public void deleteBook(
            @Parameter(
                description = "Id of the required book",
                required = true)
            @NonNull @PathVariable Long id) {
        bookService.deleteById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update book by id", 
            description = "Requires ADMIN role to access",
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookDto.class)),
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
    public BookDto updateBook(@NonNull @PathVariable Long id, 
            @RequestBody @Valid @NonNull CreateBookRequestDto requestDto) {
        return bookService.updateBook(id, requestDto);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Get books by parameters", 
            responses = {
                @ApiResponse(
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = BookDto.class)),
                    responseCode = "200",
                    description = "List of books"), 
                @ApiResponse(
                    content = @Content(schema = @Schema(hidden = true)),
                    responseCode = "401",
                    description = "Unauthorized")
            }
    )
    public List<BookDto> searchBooks(
            @Parameter(
                description = "Parameters for search. "
                + "Price should look like [{min value}, {max value}]")
            @NonNull BookSearchParametersDto searchParameters,
            @Parameter(
                description = "Pagination and sorting") 
            @NonNull Pageable pageable) {
        return bookService.searchBooks(searchParameters, pageable);
    }
}
