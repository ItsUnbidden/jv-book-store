package mate.academy.jvbookstore.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.jvbookstore.dto.book.BookDto;
import mate.academy.jvbookstore.dto.book.CreateBookRequestDto;
import mate.academy.jvbookstore.exception.EntityNotFoundException;
import mate.academy.jvbookstore.mapper.BookMapper;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.model.Category;
import mate.academy.jvbookstore.repository.book.BookRepository;
import mate.academy.jvbookstore.repository.category.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Category category;
    private Book book;
    private CreateBookRequestDto requestDto;
    private BookDto bookDto;
    private Pageable pageable;
    
    @BeforeEach
    void initBeforeEach() {
        pageable = PageRequest.of(0, 10);

        category = new Category();
        category.setId(1L);
        category.setName("category1");
     
        book = new Book();
        book.setAuthor("author1");
        book.setCategories(Set.of(category));
        book.setIsbn("123456789");
        book.setPrice(BigDecimal.valueOf(9.99));
        book.setTitle("title1");

        requestDto = new CreateBookRequestDto();
        requestDto.setAuthor(book.getAuthor());
        requestDto.setIsbn(book.getIsbn());
        requestDto.setPrice(book.getPrice());
        requestDto.setTitle(book.getTitle());
        requestDto.setCategoryIds(new HashSet<>(book.getCategories().stream()
                .map(c -> c.getId())
                .toList()));

        bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setCategoryIds(new HashSet<>(book.getCategories().stream()
                .map(c -> c.getId())
                .toList()));
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setTitle(book.getTitle());
    }

    @Test
    void save_WithValidBook_SavedBook() {
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookRepository.save(book)).thenReturn(book);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        BookDto actual = bookService.save(requestDto);
        BookDto expected = bookMapper.toDto(book);
        Assertions.assertEquals(expected, actual);
    }
    
    @Test
    void findAll_AllBooksWithPageable_ListOfBooks() {
        List<Book> books = List.of(book);
        Page<Book> booksPage = new PageImpl<>(books, pageable, books.size());

        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookRepository.findAll(pageable)).thenReturn(booksPage);

        List<BookDto> actual = bookService.findAll(pageable);

        assertEquals(actual.size(), 1);
        assertEquals(actual.get(0), bookDto);
    }

    @Test
    void findById_WithCorrectId_Book() {
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        BookDto actual = bookService.findById(book.getId());

        assertEquals(bookDto, actual);
    }

    @Test
    void findById_WithIncorrectId_EntityNotFoundException() {
        Long invalidId = -1L;

        when(bookRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.findById(invalidId));
    }

    @Test
    void updateBook_WithChangedTitle_UpdatedBook() {
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);
        when(bookRepository.save(book)).thenReturn(book);

        BookDto actual = bookService.updateBook(book.getId(), requestDto);

        assertEquals(bookDto, actual);
    }
}
