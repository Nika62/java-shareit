package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {

    ItemDto createItem(ItemDto itemDto);

    ItemDto updateItem(long userId, long id, ItemDto itemDto);

    ItemDto getItemById(long id);

    List<ItemDto> getSearchItem(String text);

    List<ItemDto> getAllItemsUser(long userId);

    boolean deleteItemById(long id);

}
