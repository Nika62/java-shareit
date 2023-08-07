package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Item {
    @EqualsAndHashCode.Exclude
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long userId;
}
