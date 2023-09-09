package shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final UserMapper userMapper;

    public ItemDto convertItemToItemDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        itemDto.setUser(userMapper.convertUserToUserDto(item.getUser()));

        if (Objects.nonNull(item.getRequest())) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public Item convertItemDtoToItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }

        Item item = new Item();

        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setUser(userMapper.convertUserDtoToUser(itemDto.getUser()));

        return item;
    }

    public ItemRequestDto convertItemToItemRequestDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemRequestDto temRequestDto = new ItemRequestDto();
        temRequestDto.setId(item.getId());
        temRequestDto.setName(item.getName());
        temRequestDto.setDescription(item.getDescription());
        temRequestDto.setAvailable(item.isAvailable());

        if (Objects.nonNull(item.getRequest())) {
            temRequestDto.setRequestId(item.getRequest().getId());
        }
        return temRequestDto;
    }

}