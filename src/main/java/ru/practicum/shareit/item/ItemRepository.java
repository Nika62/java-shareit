package ru.practicum.shareit.item;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
@Component
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("from Item where lower(name) like :text or lower(description) like :text and available = true")
    List<Item> findAllByNameOrDescriptionAndAvailable(String text);

    List<Item> findAllItemsByUserIdOrderById(long id);

}
