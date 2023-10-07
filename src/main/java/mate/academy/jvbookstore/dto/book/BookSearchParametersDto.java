package mate.academy.jvbookstore.dto.book;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mate.academy.jvbookstore.dto.SearchParametersDto;

@Data
@EqualsAndHashCode(callSuper = false)
public class BookSearchParametersDto extends SearchParametersDto {
    private String[] authors;

    private String[] price;
}
