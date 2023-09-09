package shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getSearchItem(String text);

    List<ItemDto> getAllItemsUser(long userId);

    void deleteItemById(long id);

}
