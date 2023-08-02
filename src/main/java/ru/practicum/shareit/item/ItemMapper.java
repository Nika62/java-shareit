package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public ItemDto convertItemToItemDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        if (item.getAvailable() != null) {
            itemDto.setAvailable(item.getAvailable());
        }

        return itemDto;
    }
}