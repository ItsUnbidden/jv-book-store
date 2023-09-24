package mate.academy.jvbookstore.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BookSearchParametersDto extends SearchParametersDto {
    private String[] authors;

    private String[] price;
}
