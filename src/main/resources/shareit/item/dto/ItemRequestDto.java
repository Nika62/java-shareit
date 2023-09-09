package shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ItemRequestDto {
    @NotNull
    private long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 250, message = "Длина описания не должна превышать 200 символов")
    private String description;
    @NotNull(message = "Статус не может быть пустым")
    private Boolean available;
    @NotNull
    private long requestId;
}
