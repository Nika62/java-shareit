package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    ItemDto createItem(Item item);

    ItemDto updateItem(long userId, long id, Item item);

    ItemDto getItemById(long id);

    List<ItemDto> getSearchItem(String text);

    List<ItemDto> getAllItemsUser(long userId);

    boolean deleteItemById(long id);

}
