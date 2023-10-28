package mate.academy.jvbookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.jvbookstore.model.Order.Status;

@Data
public class UpdateStatusRequestDto {
    @NotNull
    private Status status;
}
