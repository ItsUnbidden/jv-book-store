package mate.academy.jvbookstore.mapper;

import mate.academy.jvbookstore.config.MapperConfig;
import mate.academy.jvbookstore.dto.BookDto;
import mate.academy.jvbookstore.dto.CreateBookRequestDto;
import mate.academy.jvbookstore.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
