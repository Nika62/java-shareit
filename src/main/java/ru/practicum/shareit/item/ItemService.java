package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemById(long id);

    List<ItemDto> getSearchItem(String text);

    List<ItemDto> getAllItemsUser(long userId);

    void deleteItemById(long id);

}
