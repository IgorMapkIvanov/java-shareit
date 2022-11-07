package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> getItemById(Long id);

    List<Item> getItemsByOwnerId(Long userId, PageRequest pageRequest);

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')) " +
            "and i.available = ?2 ")
    List<Item> searchItemsByNameOrDescriptionContainingTextIgnoreCaseAndAvailable(String text,
                                                                                  Boolean available,
                                                                                  PageRequest pageRequest);

    List<Item> getItemsByRequestId(Long requestId);
}
