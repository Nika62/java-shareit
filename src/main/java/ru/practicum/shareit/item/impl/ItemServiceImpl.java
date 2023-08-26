package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;


    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = getUserByIdOrTrows(userId);
        itemDto.setUser(userMapper.convertUserToUserDto(user));
        try {
          Item item =  itemRepository.save(itemMapper.convertItemDtoToItem(itemDto));
          return itemMapper.convertItemToItemDto(item);

        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Вещь уже зарегистрирована в базе");
        }
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = getItemByIdOrTrows(itemId);

        if (item.getUser().getId() != userId) {
            throw new NotFoundException("У пользователя с id " + userId + " нет вещи с id " + itemId);
        }
        item.setName(Objects.nonNull(itemDto.getName()) ? itemDto.getName() : item.getName());
        item.setDescription(Objects.nonNull(itemDto.getDescription()) ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(Objects.nonNull(itemDto.getAvailable()) ? itemDto.getAvailable() : item.isAvailable());

        return itemMapper.convertItemToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        Item item = getItemByIdOrTrows(itemId);
        ItemDto itemDto = itemMapper.convertItemToItemDto(item);
        if (item.getUser().getId() == userId) {
            recordLastNextBookingOnItem(itemDto, itemId,userId);
        }
        return itemDto;

    }

    @Override
    public List<ItemDto> getSearchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        text = "%" + text.toLowerCase() + "%";
        List<Item> items =  itemRepository.findAllByNameOrDescriptionAndAvailable(text);
        return items.stream()
                .map(itemMapper::convertItemToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemsUser(long userId) {
        List<ItemDto> items =  itemRepository.findAllItemsByUserIdOrderById(userId).stream()
                .map(itemMapper::convertItemToItemDto)
                .collect(Collectors.toList());

        for (int i = 0; i < items.size(); i++) {
            ItemDto itemDto = items.get(i);
            recordLastNextBookingOnItem(itemDto,itemDto.getId(),userId);
        }
        return items;
    }

    @Override
    public void deleteItemById(long id) {

        itemRepository.delete(getItemByIdOrTrows(id));
    }

    private User getUserByIdOrTrows(long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователя с id = " + id + " нет в базе"));
    }

    private Item getItemByIdOrTrows(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещи с id = " + itemId + " нет в базе"));
    }

    private void recordLastNextBookingOnItem(ItemDto itemDto, long itemId, long userId) {
        LocalDate date = LocalDate.now();
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime today = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59);

        List<Booking> pastBookings =  bookingRepository.getLastBooking(itemId, today);
        List<Booking> futureBookings =  bookingRepository.getNextBooking(itemId, userId, dateTime);
        Booking lastBooking = null;

        if (!pastBookings.isEmpty()) {
           lastBooking = pastBookings.size() > 1  ? pastBookings.get(1) : pastBookings.get(0);
        }
        Booking nextBooking = futureBookings.isEmpty() ? null : futureBookings.get(0);

        itemDto.setLastBooking(bookingMapper.convertBookingToBookingLastNextDto(lastBooking));
        itemDto.setNextBooking(bookingMapper.convertBookingToBookingLastNextDto(nextBooking));

    }
}
