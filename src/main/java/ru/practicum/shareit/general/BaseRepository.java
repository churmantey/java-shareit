package ru.practicum.shareit.general;

import java.util.List;

public interface BaseRepository<T> {

    T getElement(Long id);

    T addElement(T element);

    T updateElement(T newElement);

    void deleteElementById(Long id);

    List<T> getAllElements();

}
