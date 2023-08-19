package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;


    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        itemDto.setUserId(userId);
        User user = getUserByIdOrTrows(userId);
        try {
          Item item =  itemRepository.save(itemMapper.convertItemDtoToItem(user, itemDto));
          return itemMapper.convertItemToItemDto(item);

        } catch(DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Вещь уже зарегистрированна в базе");
        }
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = getItemByIdOrTrows(itemId);

        if(item.getUser().getId()!=userId) {
            throw new NotFoundException("У пользователя с id " + userId + " нет вещи с id " + itemId);
        }
        item.setName(Objects.nonNull(itemDto.getName()) ? itemDto.getName() : item.getName());
        item.setDescription(Objects.nonNull(itemDto.getDescription()) ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(Objects.nonNull(itemDto.getAvailable()) ? itemDto.getAvailable() : item.isAvailable());

        return itemMapper.convertItemToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(long id) {
        return itemMapper.convertItemToItemDto(getItemByIdOrTrows(id));
    }

    @Override
    public List<ItemDto> getSearchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        text = "%"+text.toLowerCase()+"%";
        List<Item> items =  itemRepository.findAllByNameOrDescriptionAndAvailable(text);
        return items.stream()
                .map(itemMapper::convertItemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemsUser(long userId) {

        return itemRepository.findAllItemsByUserId(userId).stream()
                .map(itemMapper::convertItemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItemById(long id) {

        itemRepository.delete(getItemByIdOrTrows(id));
    }

    private User getUserByIdOrTrows(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + id + " нет в базе"));
    }

    private Item getItemByIdOrTrows(long id) {
        return itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Вещи с id = " + id + " нет в базе"));
    }
}
