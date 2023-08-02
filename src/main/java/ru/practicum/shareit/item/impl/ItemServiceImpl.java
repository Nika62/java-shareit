package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(long userId, Item item) {
        userRepository.getUserById(userId);
        item.setUserId(userId);
        return itemRepository.createItem(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, Item item) {
        userRepository.getUserById(userId);
        return itemRepository.updateItem(userId, itemId, item);
    }

    @Override
    public ItemDto getItemById(long id) {
        return itemRepository.getItemById(id);
    }

    @Override
    public List<ItemDto> getSearchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.getSearchItem(text);
    }

    @Override
    public List<ItemDto> getAllItemsUser(long userId) {
        return itemRepository.getAllItemsUser(userId);
    }

    @Override
    public boolean deleteItemById(long id) {
        return itemRepository.deleteItemById(id);
    }
}
