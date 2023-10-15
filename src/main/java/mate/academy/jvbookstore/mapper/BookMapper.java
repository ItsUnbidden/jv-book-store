package mate.academy.jvbookstore.mapper;

import java.util.HashSet;
import mate.academy.jvbookstore.config.MapperConfig;
import mate.academy.jvbookstore.dto.book.BookDto;
import mate.academy.jvbookstore.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.jvbookstore.dto.book.CreateBookRequestDto;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    BookDtoWithoutCategoryIds toBookDtoWithoutCategoryIds(Book book);

    @AfterMapping
    default void setCategories(@MappingTarget BookDto bookDto, Book book) {
        bookDto.setCategoryIds(new HashSet<>(book.getCategories().stream()
                .map(Category::getId)
                .toList()));
    }
}
