package shareit.item;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item  i where lower(i.name) like ?1 or lower(i.description) like ?1 and i.available = true")
    List<Item> findAllByNameOrDescriptionAndAvailable(String text);

    @Query("from Item  i where i.user.id =?1 ORDER BY i.id")
    List<Item> findAllItemsByUserIdOrderById(long id);

    List<Item> findAllItemsByRequestId(long id);

}
