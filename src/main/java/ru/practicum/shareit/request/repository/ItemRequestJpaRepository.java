package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;

public interface ItemRequestJpaRepository  extends JpaRepository<ItemRequest, Long> {

}
