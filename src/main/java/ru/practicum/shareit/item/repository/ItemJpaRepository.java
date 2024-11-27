package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOrderByIdAsc();

    List<Item> findAllByOwnerOrderByIdAsc(Long ownerId);


    @Query("select it from Item as it where it.available " +
            "and lower(it.name) like lower(concat('%', :text,'%')) " +
            "or lower(it.description) like lower(concat('%', :text,'%')) order by it.id")
    List<Item> findAvailableByNameOrDescription(@Param("text") String text);

}
