package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item dtoToItem(ItemDto itemDto);

    ItemDto itemToDto(Item item);

    List<Item> dtoListToItemList(List<ItemDto> itemDto);

    List<ItemDto> itemListToDtoList(List<Item> itemDto);

}
