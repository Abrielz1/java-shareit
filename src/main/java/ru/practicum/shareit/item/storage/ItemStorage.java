package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

@Repository
public interface ItemStorage extends JpaRepository<Item, Long> {

    List<Item> findAllByOwner(long owner);

    List<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);
}
