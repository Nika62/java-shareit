package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryIml implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    private final ItemMapper mapper;

    private long countItemId = 0;

    private long assignItemId(Item item) {
        item.setId(++countItemId);
        return countItemId;
    }

    @Override
    public ItemDto createItem(Item item) {
        if (!items.containsValue(item)) {
            items.put(assignItemId(item), item);
            return mapper.convertItemToItemDto(item);
        }
        log.info("Вещь {} уже существует в базе", item);
        throw new ObjectAlreadyExistsException("Вещь " + item + "уже существует в базе");
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, Item item) {
        checkItemExists(itemId);

        Item storedItem = items.get(itemId);
        if (storedItem.getUserId() == userId) {
            storedItem.setName(Objects.nonNull(item.getName()) ? item.getName() : storedItem.getName());
            storedItem.setDescription(Objects.nonNull(item.getDescription()) ? item.getDescription() : storedItem.getDescription());
            storedItem.setAvailable(Objects.nonNull(item.getAvailable()) ? item.getAvailable() : storedItem.getAvailable());
            storedItem.setUserId(storedItem.getUserId());
            return mapper.convertItemToItemDto(storedItem);
        }
        log.info("У пользователя с id = {} нет вещи с id = {}", userId, itemId);
        throw new NotFoundException("У пользователя с id = " + userId + " нет вещи с id = " + itemId);
    }

    @Override
    public ItemDto getItemById(long id) {
        checkItemExists(id);
        return mapper.convertItemToItemDto(items.get(id));
    }

    public List<ItemDto> getSearchItem(String text) {
        return items.values().stream()
                .filter(item -> item.getAvailable().equals(true))
                .filter(item -> new String(item.getName() + item.getDescription()).toLowerCase().contains(text.toLowerCase()))
                .map(item -> mapper.convertItemToItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemsUser(long userId) {
        return items.values().stream()
                .filter(item -> item.getUserId() == userId)
                .map(item -> mapper.convertItemToItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteItemById(long id) {
        items.remove(id);
        return items.containsKey(id);
    }

    private void checkItemExists(long id) {
        if (!items.containsKey(id)) {
            log.info("Вещь с id = {} не найдена", id);
            throw new NotFoundException("Вещь с id = " + id + " не найдена");
        }
    }
}
